#!/usr/bin/env bash
# Pruebas de los hooks de .claude/hooks (v2, Fase 10). Sale 0 si todo pasa, 1 si algo falla, 3 si no puede probar.
# Va en la puerta SIN los casos que corren Gradle (--rapido); entero, a mano: config/checks/hooks_test.sh --completo.
set -u
cd "$(dirname "$0")/../.." || exit 3
H=.claude/hooks
fallos=0
ok() { echo "ok    $1"; }
ko() { echo "FALLO $1"; fallos=$((fallos + 1)); }
guard() { printf '%s' "$1" | PATH="${2:-$PATH}" bash "$H/guard-protected-files.sh"; }
decision() { python3 -I -c 'import json,sys; t=sys.stdin.read().strip(); print(json.loads(t)["hookSpecificOutput"]["permissionDecision"] if t else "pasa")'; }
json() { printf '{"tool_name":"Edit","tool_input":{"file_path":"%s"}}' "$1"; }

# --- Guard (deniega: falla cerrado)
[ "$(guard "$(json "$PWD/androidApp/Google-Services.JSON")" | decision)" = "deny" ] && ok "guard: mayúsculas → deny" || ko "guard: mayúsculas"
[ "$(guard "$(json "$PWD/release.keystore")" | decision)" = "deny" ] && ok "guard: keystore → deny" || ko "guard: keystore"
[ "$(guard "$(json "$PWD/shared/build.gradle.kts")" | decision)" = "pasa" ] && ok "guard: build.gradle.kts de módulo → pasa" || ko "guard: ruta de módulo"
[ "$(guard "$(json "$PWD/build.gradle.kts")" | decision)" = "ask" ] && ok "guard: build.gradle.kts raíz → ask" || ko "guard: raíz"
[ "$(guard "$(json "$PWD/gradle/libs.versions.toml")" | decision)" = "ask" ] && ok "guard: catálogo → ask" || ko "guard: catálogo"
[ "$(guard 'esto no es json' | decision)" = "ask" ] && ok "guard: JSON ilegible → ask (cerrado)" || ko "guard: JSON ilegible"
SINPY="$(mktemp -d)"; for b in bash cat basename dirname tr printf; do p="$(command -v $b)"; [ -n "$p" ] && ln -s "$p" "$SINPY/$b"; done
[ "$(guard "$(json "$PWD/local.properties")" "$SINPY" | decision)" = "ask" ] && ok "guard: sin python3 en el PATH → ask (sigue sin dejar pasar)" || ko "guard: sin python3"
rm -rf "$SINPY"
# Guard en Bash (#PP17): un comando que NOMBRA un protegido y ESCRIBE → deny; leer o no nombrarlo → pasa.
bashjson() { python3 -I -c 'import json,sys; print(json.dumps({"tool_name":"Bash","tool_input":{"command":sys.argv[1]}}))' "$1"; }
[ "$(guard "$(bashjson 'echo sdk.dir=/x > local.properties')" | decision)" = "deny" ] && ok "guard Bash: redirección a local.properties → deny" || ko "guard Bash: redirección"
[ "$(guard "$(bashjson 'cp /tmp/a androidApp/release.JKS')" | decision)" = "deny" ] && ok "guard Bash: cp a un keystore → deny" || ko "guard Bash: cp"
[ "$(guard "$(bashjson "sed -i '' s/a/b/ androidApp/google-services.json")" | decision)" = "deny" ] && ok "guard Bash: sed -i → deny" || ko "guard Bash: sed -i"
[ "$(guard "$(bashjson 'git grep -n google-services.json 2>/dev/null')" | decision)" = "pasa" ] && ok "guard Bash: leer (con 2>/dev/null) → pasa" || ko "guard Bash: falso positivo al leer"
[ "$(guard "$(bashjson 'ls -la > /tmp/x')" | decision)" = "pasa" ] && ok "guard Bash: escribir sin protegido → pasa" || ko "guard Bash: falso positivo sin protegido"

# --- Filtro del auditor (deniega: falla cerrado)
F=.claude/hooks/auditor-deny-github.sh
filtro() { printf '%s' "$1" | PATH="${2:-$PATH}" bash "$F"; }
[ "$(filtro "$(bashjson 'gh --version')" | decision)" = "deny" ] && ok "auditor: gh → deny" || ko "auditor: gh"
# #PP18: entre comillas o con barra invertida, el shell lo ejecuta igual.
[ "$(filtro "$(bashjson "'gh' pr list")" | decision)" = "deny" ] && ok "auditor: 'gh' entre comillas simples → deny" || ko "auditor: comillas simples"
[ "$(filtro "$(bashjson '"gh" api repos/x/y')" | decision)" = "deny" ] && ok "auditor: \"gh\" entre comillas dobles → deny" || ko "auditor: comillas dobles"
[ "$(filtro "$(bashjson '\gh pr view 1')" | decision)" = "deny" ] && ok "auditor: \\gh → deny" || ko "auditor: barra invertida"
[ "$(filtro "$(bashjson 'curl -s https://api.github.com/repos/x/y')" | decision)" = "deny" ] && ok "auditor: api.github.com → deny" || ko "auditor: api.github.com"
[ "$(filtro "$(bashjson 'cd x && GH --help')" | decision)" = "deny" ] && ok "auditor: GH en mayúsculas → deny" || ko "auditor: mayúsculas"
[ "$(filtro "$(bashjson 'grep -rn high shared/src')" | decision)" = "pasa" ] && ok "auditor: grep (contiene «gh» dentro de otra palabra) → pasa" || ko "auditor: falso positivo"
[ "$(filtro 'no es json' | decision)" = "deny" ] && ok "auditor: JSON ilegible → deny (cerrado)" || ko "auditor: JSON ilegible"
SINPY="$(mktemp -d)"; for b in bash cat tr printf grep; do p="$(command -v $b)"; [ -n "$p" ] && ln -s "$p" "$SINPY/$b"; done
[ "$(filtro "$(bashjson 'gh --version')" "$SINPY" | decision)" = "deny" ] && ok "auditor: sin python3 en el PATH → deny" || ko "auditor: sin python3"
rm -rf "$SINPY"

# --- Stop (informan: fallan abierto)
# El de tests JVM corre en un proyecto FALSO cuyo gradlew solo deja una marca (#PP21): sin candado vivo (el CI) el hook
# lanzaba el Gradle real, ~3 min del job calidad por un caso que no prueba nada de Gradle. Los otros dos siguen sobre el
# repositorio: sin config/checks ni .git sus casos no medirían nada.
FALSO="$(mktemp -d)"
printf '#!/usr/bin/env bash\ntouch "%s/gradle-llamado"\nexit 0\n' "$FALSO" > "$FALSO/gradlew"; chmod +x "$FALSO/gradlew"
for h in stop-run-jvm-tests.sh stop-verificadores.sh stop-ui-audit-reminder.sh; do
  dir="$PWD"; [ "$h" = stop-run-jvm-tests.sh ] && dir="$FALSO"
  printf 'basura' | CLAUDE_PROJECT_DIR="$dir" bash "$H/$h" >/dev/null 2>&1; c=$?
  [ "$c" -eq 0 ] || [ "$c" -eq 2 ] && ok "$h: JSON ilegible → no revienta ($c)" || ko "$h: JSON ilegible ($c)"
  printf '{"stop_hook_active": true}' | CLAUDE_PROJECT_DIR="$dir" bash "$H/$h" >/dev/null 2>&1
  [ $? -eq 0 ] && ok "$h: stop_hook_active → 0" || ko "$h: stop_hook_active"
done
[ -f "$FALSO/gradle-llamado" ] && ok "stop-run-jvm-tests.sh: con JSON ilegible llega a Gradle (el falso), no al real" || ko "stop-run-jvm-tests.sh: no llegó a Gradle"
rm -rf "$FALSO"

# Recordatorio de UI (#PP22): solo las vistas de Swift y la escena (iOSApp.swift, el App.kt de Swift) son UI.
# Repositorio temporal con DOS commits: el hook mira HEAD~1..HEAD.
recordatorio() {  # $1 = fichero del último commit → código del hook
  local r; r="$(mktemp -d)"
  ( cd "$r" && git init -q && git config user.email t@t && git config user.name t && echo a > a && git add a &&
    git commit -qm uno && mkdir -p "$(dirname "$1")" && echo b > "$1" && git add "$1" && git commit -qm dos )
  printf '{}' | CLAUDE_PROJECT_DIR="$r" bash "$H/stop-ui-audit-reminder.sh" >/dev/null 2>&1; local c=$?
  rm -rf "$r"; return "$c"
}
recordatorio iosApp/iosApp/SwiftCrashReporter.swift; c=$?
[ "$c" -eq 0 ] && ok "recordatorio UI: Swift que no es UI → 0" || ko "recordatorio UI: SwiftCrashReporter → $c (esperado 0)"
recordatorio iosApp/iosApp/ContentView.swift; c=$?
[ "$c" -eq 2 ] && ok "recordatorio UI: ContentView.swift → 2" || ko "recordatorio UI: ContentView → $c (esperado 2)"
recordatorio iosApp/iosApp/iOSApp.swift; c=$?
[ "$c" -eq 2 ] && ok "recordatorio UI: iOSApp.swift → 2" || ko "recordatorio UI: iOSApp → $c (esperado 2)"
recordatorio shared/src/commonMain/kotlin/x/ui/Home.kt; c=$?
[ "$c" -eq 2 ] && ok "recordatorio UI: Kotlin en ui/ → 2" || ko "recordatorio UI: ui/ → $c (esperado 2)"

if [ "${1:-}" = "--completo" ]; then
  GITDIR="$(git rev-parse --git-dir)"; LOCK="$GITDIR/puerta.lock"
  [ -d "$LOCK" ] && { echo "hay un candado puesto: no pruebo"; exit 3; }
  R=shared/src/commonMain/kotlin/org/example/proyecto_plantilla/core/result/Resource.kt
  copia="$(mktemp)"; cp "$R" "$copia"; trap 'cp "$copia" "$R"; rm -rf "$copia" "$LOCK"' EXIT
  sed -i '' 's/is Resource.Error -> this/is Resource.Error -> Resource.Error(AppError.Unknown)/' "$R"
  printf '{}' | bash "$H/stop-run-jvm-tests.sh" >/dev/null 2>&1; [ $? -eq 2 ] && ok "stop tests: test roto → 2" || ko "stop tests: test roto"
  bash -c "config/checks/candado.sh tomar >/dev/null && sleep 120" & duenyo=$!
  for _ in 1 2 3 4 5 6 7 8 9 10; do [ -d "$LOCK" ] && break; sleep 0.2; done
  printf '{}' | bash "$H/stop-run-jvm-tests.sh" >/dev/null 2>&1; [ $? -eq 0 ] && ok "stop tests: candado VIVO → silencio" || ko "stop tests: candado vivo"
  kill "$duenyo" 2>/dev/null; wait "$duenyo" 2>/dev/null
  sleep 0 & muerto=$!; wait "$muerto"; rm -rf "$LOCK"; mkdir "$LOCK"; echo "$muerto" > "$LOCK/pid"; echo "Mon Jan  1 00:00:00 2001" > "$LOCK/hora"
  printf '{}' | bash "$H/stop-run-jvm-tests.sh" >/dev/null 2>&1; [ $? -eq 2 ] && ok "stop tests: candado de PID MUERTO → corre" || ko "stop tests: PID muerto"
  [ -d "$LOCK" ] && ok "stop tests: el candado huérfano sigue (lo retira la puerta)" || ko "stop tests: borró el candado"
  sleep 60 & otro=$!; echo "$otro" > "$LOCK/pid"
  printf '{}' | bash "$H/stop-run-jvm-tests.sh" >/dev/null 2>&1; [ $? -eq 2 ] && ok "stop tests: PID vivo de OTRO proceso → corre" || ko "stop tests: PID de otro"
  kill "$otro" 2>/dev/null; wait "$otro" 2>/dev/null
fi

echo; [ "$fallos" -eq 0 ] && { echo "hooks_test: todo pasa"; exit 0; } || { echo "hooks_test: $fallos fallos"; exit 1; }

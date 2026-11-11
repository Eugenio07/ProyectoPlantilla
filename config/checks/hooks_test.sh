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
bashjson() { python3 -I -c 'import json,sys; print(json.dumps({"tool_name":"Bash","tool_input":{"command":sys.argv[1]}}))' "$1"; }
[ "$(filtro "$(bashjson 'gh --version')" | decision)" = "deny" ] && ok "auditor: gh → deny" || ko "auditor: gh"
[ "$(filtro "$(bashjson 'curl -s https://api.github.com/repos/x/y')" | decision)" = "deny" ] && ok "auditor: api.github.com → deny" || ko "auditor: api.github.com"
[ "$(filtro "$(bashjson 'cd x && GH --help')" | decision)" = "deny" ] && ok "auditor: GH en mayúsculas → deny" || ko "auditor: mayúsculas"
[ "$(filtro "$(bashjson 'grep -rn high shared/src')" | decision)" = "pasa" ] && ok "auditor: grep (contiene «gh» dentro de otra palabra) → pasa" || ko "auditor: falso positivo"
[ "$(filtro 'no es json' | decision)" = "deny" ] && ok "auditor: JSON ilegible → deny (cerrado)" || ko "auditor: JSON ilegible"
SINPY="$(mktemp -d)"; for b in bash cat tr printf grep; do p="$(command -v $b)"; [ -n "$p" ] && ln -s "$p" "$SINPY/$b"; done
[ "$(filtro "$(bashjson 'gh --version')" "$SINPY" | decision)" = "deny" ] && ok "auditor: sin python3 en el PATH → deny" || ko "auditor: sin python3"
rm -rf "$SINPY"

# --- Stop (informan: fallan abierto)
for h in stop-run-jvm-tests.sh stop-verificadores.sh stop-ui-audit-reminder.sh; do
  printf 'basura' | bash "$H/$h" >/dev/null 2>&1; c=$?
  [ "$c" -eq 0 ] || [ "$c" -eq 2 ] && ok "$h: JSON ilegible → no revienta ($c)" || ko "$h: JSON ilegible ($c)"
  printf '{"stop_hook_active": true}' | bash "$H/$h" >/dev/null 2>&1
  [ $? -eq 0 ] && ok "$h: stop_hook_active → 0" || ko "$h: stop_hook_active"
done

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

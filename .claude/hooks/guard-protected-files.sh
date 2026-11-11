#!/usr/bin/env bash
# PreToolUse (Edit|Write|MultiEdit). DENIEGA: falla CERRADO. Si no puede leer la ruta, PIDE CONFIRMACIÓN y dice por qué.
# Decisión por JSON en STDOUT con exit 0 (el exit 2 ignora el JSON). Comparaciones en minúsculas.
#   deny: keystores (*.jks, *.keystore), local.properties, google-services.json, GoogleService-Info.plist
#   ask:  build.gradle.kts RAÍZ (su directorio tiene settings.gradle.kts), settings.gradle.kts, gradle/libs.versions.toml
set -u
decide() {  # $1 = deny|ask · $2 = motivo
  printf '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"%s","permissionDecisionReason":"%s"}}\n' "$1" "$2"
  exit 0
}
entrada="$(cat)"
command -v python3 >/dev/null 2>&1 || decide ask "guard: sin python3 no puedo leer la ruta; confirma a mano (falla cerrado)"
ruta="$(printf '%s' "$entrada" | python3 -I -c '
import json, sys
try:
    d = json.load(sys.stdin)
    print(d["tool_input"]["file_path"])
except Exception:
    sys.exit(1)
' 2>/dev/null)" || decide ask "guard: no pude leer file_path de la llamada; confirma a mano (falla cerrado)"
[ -n "$ruta" ] || decide ask "guard: file_path vacío; confirma a mano (falla cerrado)"

base="$(basename "$ruta" | tr '[:upper:]' '[:lower:]')"
dir="$(dirname "$ruta")"
case "$base" in
  *.jks | *.keystore | local.properties | google-services.json | googleservice-info.plist)
    decide deny "guard: «$base» es un fichero protegido (secretos/config nativa): no se edita desde Claude Code" ;;
  settings.gradle.kts)
    decide ask "guard: settings.gradle.kts cambia la estructura del build: confirma" ;;
  libs.versions.toml)
    decide ask "guard: el catálogo de versiones es el fichero de mayor riesgo (un bump mueve la matriz de ABI): confirma" ;;
  build.gradle.kts)
    if [ -f "$dir/settings.gradle.kts" ]; then
      decide ask "guard: build.gradle.kts RAÍZ: confirma"
    fi ;;
esac
exit 0

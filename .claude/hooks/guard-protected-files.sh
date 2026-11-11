#!/usr/bin/env bash
# PreToolUse (Edit|Write|MultiEdit|Bash). DENIEGA: falla CERRADO. Si no puede leer la llamada, PIDE CONFIRMACIÓN y dice
# por qué. Decisión por JSON en STDOUT con exit 0 (el exit 2 ignora el JSON). Comparaciones en minúsculas.
#   deny: keystores (*.jks, *.keystore), local.properties, google-services.json, GoogleService-Info.plist
#   ask:  build.gradle.kts RAÍZ (su directorio tiene settings.gradle.kts), settings.gradle.kts, gradle/libs.versions.toml
# Bash (#PP17), HEURÍSTICA: deniega si el comando NOMBRA un protegido Y ESCRIBE (>, >>, tee, cp, mv, sed -i, rm, touch,
#   dd). Leer pasa. No ve un protegido que no se nombra (variable, glob): limitación declarada en CLAUDE.md §13.
set -u
decide() {  # $1 = deny|ask · $2 = motivo
  printf '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"%s","permissionDecisionReason":"%s"}}\n' "$1" "$2"
  exit 0
}
entrada="$(cat)"
command -v python3 >/dev/null 2>&1 || decide ask "guard: sin python3 no puedo leer la llamada; confirma a mano (falla cerrado)"
# Primera línea: la herramienta. El resto: la ruta (Edit/Write) o el comando (Bash, puede tener varias líneas).
leido="$(printf '%s' "$entrada" | python3 -I -c '
import json, sys
try:
    d = json.load(sys.stdin)
    h = d.get("tool_name", "")
    v = d["tool_input"]["command"] if h == "Bash" else d["tool_input"]["file_path"]
    print(h); print(v)
except Exception:
    sys.exit(1)
' 2>/dev/null)" || decide ask "guard: no pude leer la llamada; confirma a mano (falla cerrado)"
herramienta="$(printf '%s\n' "$leido" | head -n 1)"
valor="$(printf '%s\n' "$leido" | tail -n +2)"
[ -n "$valor" ] || decide ask "guard: llamada sin ruta ni comando; confirma a mano (falla cerrado)"

if [ "$herramienta" = "Bash" ]; then
  baja="$(printf '%s' "$valor" | tr '[:upper:]' '[:lower:]')"
  # Las redirecciones a /dev/null o a otro descriptor no escriben ficheros: fuera antes de buscar «>».
  limpia="$(printf '%s' "$baja" | sed -E 's#[0-9]*>>?[[:space:]]*(/dev/null|&[0-9])##g')"
  if printf '%s' "$limpia" | grep -qE '(google-services\.json|googleservice-info\.plist|local\.properties|\.jks([^a-z0-9]|$)|\.keystore([^a-z0-9]|$))' &&
    printf '%s' "$limpia" | grep -qE '(>|(^|[^a-z0-9_-])(tee|cp|mv|rm|touch|dd)([[:space:]]|$)|sed[[:space:]]+(-[a-z]*[[:space:]]+)*-i)'; then
    decide deny "guard: el comando escribe un fichero protegido (secretos/config nativa): no desde Claude Code"
  fi
  exit 0
fi

ruta="$valor"
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

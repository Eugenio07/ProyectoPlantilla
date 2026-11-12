#!/usr/bin/env bash
# PreToolUse (Bash) del AUDITOR SIN CONTEXTO y del REVISOR EXTERNO. Les niega GitHub (gh, github.com, api.github.com):
# si leyeran incidencias y PR, compartirían las suposiciones de quien escribió el plan. DENIEGA: falla CERRADO. Si no
# puede leer el comando, deniega.
set -u
deny() {
  printf '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"deny","permissionDecisionReason":"%s"}}\n' "$1"
  exit 0
}
entrada="$(cat)"
command -v python3 >/dev/null 2>&1 || deny "filtro de GitHub: sin python3 no puedo leer el comando (falla cerrado)"
cmd="$(printf '%s' "$entrada" | python3 -I -c '
import json, sys
try:
    print(json.load(sys.stdin)["tool_input"]["command"])
except Exception:
    sys.exit(1)
' 2>/dev/null)" || deny "filtro de GitHub: no pude leer el comando (falla cerrado)"
[ -n "$cmd" ] || deny "filtro de GitHub: comando vacío (falla cerrado)"
# Sin comillas ni barras invertidas: 'gh', "gh" y \gh los ejecuta el shell igual que gh (#PP18).
baja="$(printf '%s' "$cmd" | tr '[:upper:]' '[:lower:]' | tr -d "'\"\\\\")"
if printf '%s' "$baja" | grep -qE '(^|[^a-z0-9_-])gh([[:space:]]|$)|github\.com|githubusercontent|api\.github'; then
  deny "filtro de GitHub (auditor y revisor-externo): GitHub está fuera de su alcance; lo que solo se sabría por ahí va a «Lo que no pude comprobar»"
fi
exit 0

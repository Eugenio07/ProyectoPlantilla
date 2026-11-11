#!/usr/bin/env bash
# PreToolUse (Bash) del AUDITOR SIN CONTEXTO. Le niega GitHub (gh, github.com, api.github.com): si leyera incidencias y PR,
# compartiría las suposiciones de quien escribió el plan. DENIEGA: falla CERRADO. Si no puede leer el comando, deniega.
set -u
deny() {
  printf '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"deny","permissionDecisionReason":"%s"}}\n' "$1"
  exit 0
}
entrada="$(cat)"
command -v python3 >/dev/null 2>&1 || deny "auditor: sin python3 no puedo leer el comando (falla cerrado)"
cmd="$(printf '%s' "$entrada" | python3 -I -c '
import json, sys
try:
    print(json.load(sys.stdin)["tool_input"]["command"])
except Exception:
    sys.exit(1)
' 2>/dev/null)" || deny "auditor: no pude leer el comando (falla cerrado)"
[ -n "$cmd" ] || deny "auditor: comando vacío (falla cerrado)"
baja="$(printf '%s' "$cmd" | tr '[:upper:]' '[:lower:]')"
if printf '%s' "$baja" | grep -qE '(^|[^a-z0-9_-])gh([[:space:]]|$)|github\.com|githubusercontent|api\.github'; then
  deny "auditor: GitHub está fuera de su alcance; lo que solo se sabría por ahí va a «Lo que no pude comprobar»"
fi
exit 0

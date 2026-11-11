#!/usr/bin/env bash
# Stop. INFORMA: falla ABIERTO. Los mismos verificadores que la puerta (un criterio, dos disparadores), sin Gradle:
# cola.py, tope_plan.py, wiki.py, sin_para_claude_md.py, claude_md.py, entregables.py. Rojo (o 3) → exit 2 con el motivo.
# Sin python3, en una parada provocada por un hook, o con el candado VIVO: sale 0.
set -u
entrada="$(cat 2>/dev/null || true)"
case "$entrada" in *'"stop_hook_active": true'* | *'"stop_hook_active":true'*) exit 0 ;; esac
command -v python3 >/dev/null 2>&1 || exit 0
raiz="${CLAUDE_PROJECT_DIR:-$(git rev-parse --show-toplevel 2>/dev/null)}"
[ -n "$raiz" ] && cd "$raiz" 2>/dev/null || exit 0
if [ -x config/checks/candado.sh ]; then
  case "$(config/checks/candado.sh estado 2>/dev/null)" in vivo*) exit 0 ;; esac
fi
rojo=0
for v in entregables.py cola.py tope_plan.py wiki.py sin_para_claude_md.py claude_md.py; do
  [ -f "config/checks/$v" ] || continue
  salida="$(python3 -I "config/checks/$v" 2>&1)"
  c=$?
  if [ "$c" -ne 0 ]; then rojo=1; { echo "Stop hook: $v salió $c:"; printf '%s\n' "$salida" | grep -E 'FALLO|NO PUDE' | head -5; } >&2; fi
done
[ "$rojo" -eq 0 ] && exit 0
exit 2

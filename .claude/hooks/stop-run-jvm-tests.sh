#!/usr/bin/env bash
# Stop. INFORMA: falla ABIERTO (un hook roto no estorba). Corre :shared:testAndroidHostTest.
#   rojo de tests → exit 2 con el resumen · build roto → exit 2 diciendo que es el build, no los tests · verde → silencio.
#   Sale 0 sin correr nada si: es una parada provocada por este hook (stop_hook_active), no hay gradlew, o el candado de
#   la puerta está VIVO (config/checks/candado.sh). Con el candado HUÉRFANO corre, y no lo borra.
set -u
entrada="$(cat 2>/dev/null || true)"
case "$entrada" in *'"stop_hook_active": true'* | *'"stop_hook_active":true'*) exit 0 ;; esac
raiz="${CLAUDE_PROJECT_DIR:-$(git rev-parse --show-toplevel 2>/dev/null)}"
[ -n "$raiz" ] && [ -x "$raiz/gradlew" ] || exit 0
cd "$raiz" || exit 0
if [ -x config/checks/candado.sh ]; then
  case "$(config/checks/candado.sh estado 2>/dev/null)" in vivo*) exit 0 ;; esac
fi
log="$(mktemp "${TMPDIR:-/tmp}/stop-tests.XXXXXX")" || exit 0
./gradlew :shared:testAndroidHostTest --console=plain -q > "$log" 2>&1
c=$?
if [ "$c" -eq 0 ]; then rm -f "$log"; exit 0; fi
if grep -qE '^e: |Compilation error|compile[A-Za-z0-9]*Kotlin[A-Za-z0-9]* FAILED' "$log"; then
  { echo "Stop hook: el BUILD está roto (no son los tests):"; grep -E '^e: ' "$log" | head -5; } >&2
else
  { echo "Stop hook: tests JVM en ROJO:"; grep -E 'tests completed|FAILED|AssertionError' "$log" | head -8; } >&2
fi
rm -f "$log"
exit 2

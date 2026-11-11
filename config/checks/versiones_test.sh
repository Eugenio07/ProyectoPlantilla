#!/usr/bin/env bash
# Pruebas de versiones.py (#PP16): qué cuenta como versión y qué no. Sale 0 si todo pasa, 1 si no.
set -u
cd "$(dirname "$0")/../.." || exit 3
fallos=0
d="$(mktemp -d)"; trap 'rm -rf "$d"' EXIT
caso() {  # $1 = esperado (0|1) · $2 = texto
  printf '%s\n' "$2" > "$d/x.md"
  python3 -I config/checks/versiones.py "$d/x.md" >/dev/null; c=$?
  if [ "$c" -eq "$1" ]; then echo "ok    «$2» → $c"; else echo "FALLO «$2» → $c (esperado $1)"; fallos=$((fallos + 1)); fi
}
caso 1 "Usa detekt 2.0.0-alpha.6 hasta…"
caso 1 "Mantén Ktor en 3.5.x"
caso 1 "subir a Kotlin 2.4."
caso 1 "la de Ktor 3.5, no otra"
caso 0 "la Fase 6.5 lo pide"
caso 0 "las Fases 0 y 6.5"
caso 0 "manual de operación §0.1"
caso 0 "fecha 2026-11-11, sin versión"
echo; [ "$fallos" -eq 0 ] && { echo "versiones_test: todo pasa"; exit 0; } || { echo "versiones_test: $fallos fallos"; exit 1; }

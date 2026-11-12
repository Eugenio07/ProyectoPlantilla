#!/usr/bin/env bash
# Pruebas de `validaciones.py --mecanismos` (regla: toda propiedad declarada dice su mecanismo; CLAUDE.md §4).
# Sale 0 si todo pasa, 1 si no.
set -u
cd "$(dirname "$0")/../.." || exit 3
V=config/checks/validaciones.py
fallos=0
d="$(mktemp -d)"; trap 'rm -rf "$d"' EXIT
caso() {  # $1 = esperado · $2 = nombre · $3 = contenido de la pieza
  printf '%s\n' "$3" > "$d/pieza.md"
  python3 -I "$V" --mecanismos "$d/pieza.md" >/dev/null; local c=$?
  if [ "$c" -eq "$1" ]; then echo "ok    $2 → $c"; else echo "FALLO $2 → $c (esperado $1)"; fallos=$((fallos + 1)); fi
}
caso 1 "«Solo lee.» sin mecanismo" "description: Revisa la rama. Solo lee."
caso 1 "«No editas nada.» sin mecanismo" "Solo reporta: una tabla. No editas nada."
caso 0 "mecanismo en la misma línea" "Solo lee por instrucción, no por mecanismo."
caso 0 "mecanismo en la línea siguiente" "$(printf 'Herramientas: Read y Bash; no editar es una instrucción, no un\nmecanismo: no editas ficheros.')"
caso 0 "mecanismo en la línea anterior" "$(printf 'El mecanismo es ninguno: es una instrucción.\nNo editas ningún fichero.')"
caso 0 "sin frases de capacidad" "description: Compara los DTO con los contratos."
python3 -I "$V" --mecanismos >/dev/null; c=$?
[ "$c" -eq 3 ] && echo "ok    --mecanismos sin ficheros → 3" || { echo "FALLO --mecanismos sin ficheros → $c (esperado 3)"; fallos=$((fallos + 1)); }
echo; [ "$fallos" -eq 0 ] && { echo "validaciones_test: todo pasa"; exit 0; } || { echo "validaciones_test: $fallos fallos"; exit 1; }

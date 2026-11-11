#!/usr/bin/env bash
# mutar.sh <fichero> '<expresión sed>' -- <comando...>
#
# Muta <fichero> con sed, imprime el diff de la mutación, corre <comando> y RESTAURA SIEMPRE el fichero (trap; sin
# tocar git). Prueba que una guarda (un test, un verificador) puede ponerse roja.
#
# Salida:
#   0  la mutación se VIO: el comando falló por su comprobación (no por compilar)
#   1  CIEGO: el comando siguió verde con la mutación
#   2  mal uso
#   3  NO PUDE MEDIR: sed no cambió nada, o el comando falló porque NO COMPILÓ (un build roto también es rojo y no dice
#      nada de la guarda)
set -u

uso() { echo "uso: mutar.sh <fichero> '<sed>' -- <comando...>" >&2; exit 2; }
[ "$#" -ge 4 ] || uso
fichero="$1"; expr="$2"; shift 2
[ "$1" = "--" ] || uso
shift
[ -f "$fichero" ] || { echo "mutar.sh: no existe $fichero" >&2; exit 2; }

copia="$(mktemp "${TMPDIR:-/tmp}/mutar.XXXXXX")"
salida="$(mktemp "${TMPDIR:-/tmp}/mutar-salida.XXXXXX")"
cp -p "$fichero" "$copia"
restaurar() { cp -p "$copia" "$fichero"; rm -f "$copia" "$salida"; }
trap restaurar EXIT INT TERM

if ! sed -e "$expr" "$copia" > "$fichero"; then
  echo "mutar.sh: la expresión sed no es válida" >&2
  exit 2
fi

echo "=== mutación ($fichero) ==="
if diff -u "$copia" "$fichero"; then
  echo "mutar.sh: sed no cambió nada: NO PUDE MEDIR (3)"
  exit 3
fi

echo "=== comando: $* ==="
"$@" > "$salida" 2>&1
codigo=$?
tail -n 40 "$salida"

if [ "$codigo" -eq 0 ]; then
  echo "mutar.sh: el comando siguió VERDE con la mutación: CIEGO (1)"
  exit 1
fi
# Un fallo de compilación no prueba la guarda.
if grep -qE '^e: |Compilation error|Compilation failed|compileKotlin[A-Za-z0-9]* FAILED|compile[A-Za-z0-9]*Kotlin[A-Za-z0-9]* FAILED' "$salida"; then
  echo "mutar.sh: el comando falló porque NO COMPILÓ: NO PUDE MEDIR (3)"
  exit 3
fi
echo "mutar.sh: la mutación se VIO (0): el comando falló con código $codigo"
exit 0

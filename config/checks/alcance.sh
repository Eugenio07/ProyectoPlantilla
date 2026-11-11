#!/usr/bin/env bash
# alcance.sh <base> <head>
# ¿El diff entre <base> y <head> es SOLO documentación (*.md, docs/)? Imprime «solo_docs=true» o «solo_docs=false».
# Salida: 0 contestó · 3 NO PUDE MEDIR (no pude listar el diff): quien llama lo trata como «false» y corre todo.
set -u
base="${1:-}"; head="${2:-}"
[ -n "$base" ] && [ -n "$head" ] || { echo "uso: alcance.sh <base> <head>" >&2; exit 2; }
if ! cambios="$(git diff --name-only "$base" "$head")"; then
  echo "alcance.sh: no pude listar el diff → NO PUDE MEDIR (se corre todo)"; echo "solo_docs=false"; exit 3
fi
if [ -z "$cambios" ]; then echo "solo_docs=false"; exit 0; fi
if printf '%s\n' "$cambios" | grep -qvE '(^docs/|\.md$)'; then
  echo "solo_docs=false"
else
  echo "solo_docs=true"
fi
exit 0

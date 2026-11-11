#!/usr/bin/env bash
# ramas_varadas.sh [rama-base=develop]
# Lista (1) commits de ramas remotas cuyo PR se fusionó o se cerró y que NO están en la rama base: trabajo varado
# (en el origen un commit subido tras fusionar no llegó a develop); y (2) ramas de más de una semana sin PR.
# Necesita GitHub (gh). Salida: 0 nada varado · 1 hay varados · 3 NO PUDE PREGUNTAR (que NO se lee como «hay varados»).
set -u
base="${1:-develop}"
command -v gh >/dev/null 2>&1 || { echo "ramas_varadas.sh: sin gh → NO PUDE PREGUNTAR"; exit 3; }
git fetch -q origin "+refs/heads/*:refs/remotes/origin/*" 2>/dev/null || { echo "ramas_varadas.sh: no pude hacer fetch → NO PUDE PREGUNTAR"; exit 3; }
prs="$(gh pr list --state all --limit 500 --json headRefName,state,number 2>/dev/null)" || { echo "ramas_varadas.sh: no pude listar los PR → NO PUDE PREGUNTAR"; exit 3; }
hallazgos=0
hace_una_semana=$(( $(date +%s) - 7*24*3600 ))
for ref in $(git for-each-ref --format='%(refname:short)' refs/remotes/origin/ | grep -vE '^origin/(HEAD|develop|main|master)$'); do
  rama="${ref#origin/}"
  estado="$(printf '%s' "$prs" | python3 -I -c "import json,sys; r=[p['state'] for p in json.load(sys.stdin) if p['headRefName']==sys.argv[1]]; print(r[0] if r else '')" "$rama")"
  case "$estado" in
    MERGED|CLOSED)
      fuera="$(git log --oneline "origin/$base..$ref" 2>/dev/null)"
      if [ -n "$fuera" ]; then
        hallazgos=1; echo "VARADO en $rama (PR $estado): commits que no están en $base:"; printf '%s\n' "$fuera" | sed 's/^/  /'
      fi ;;
    "")
      ultimo=$(git log -1 --format=%ct "$ref")
      if [ "$ultimo" -lt "$hace_una_semana" ]; then
        hallazgos=1; echo "SIN PR desde hace más de una semana: $rama"
      fi ;;
  esac
done
[ "$hallazgos" -eq 0 ] && echo "ramas_varadas.sh: nada varado respecto a $base"
exit "$hallazgos"

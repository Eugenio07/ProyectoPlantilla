#!/usr/bin/env bash
# Pruebas de skills_de_cierre.py (#PP26): lee SOLO la sección «Etapa final», hasta el siguiente encabezado de nivel igual
# o superior, aunque la palabra «Cierre» salga antes. Necesita ~/.claude, como el guion: solo en la puerta (no en el CI).
# Sale 0 si todo pasa, 1 si no.
set -u
cd "$(dirname "$0")/../.." || exit 3
G=config/checks/skills_de_cierre.py
fallos=0
d="$(mktemp -d)"; trap 'rm -rf "$d"' EXIT
caso() {  # $1 = esperado · $2 = nombre · $3 = fichero del plan
  python3 -I "$G" "$3" >/dev/null; local c=$?
  if [ "$c" -eq "$1" ]; then echo "ok    $2 → $c"; else echo "FALLO $2 → $c (esperado $1)"; fallos=$((fallos + 1)); fi
}

# La palabra en el título: el guion de antes cortaba ahí y no leía la etapa final (sale 3: «no nombra ninguna»).
cat > "$d/titulo.md" <<'PLAN'
# Arreglar el Cierre de sesión

## Parte 1 · Spec

Nada.

## Parte 2 · Plan

### Etapa final · Cierre

- [ ] `/no-existe-jamas`

### Verificación por criterio

## Revisiones
PLAN
caso 1 "la palabra en el título; la etapa final nombra una skill que no existe" "$d/titulo.md"

# Lo que va DETRÁS de la etapa final (otra sección ###) no es la lista de cierre.
cat > "$d/detras.md" <<'PLAN'
# Un plan

## Parte 2 · Plan

### Etapa final · Cierre

- [ ] `/plantilla-review`

### Verificación por criterio

| A1 | `/otra-cosa-falsa` |

## Revisiones
PLAN
caso 0 "una sección ### de detrás nombra algo que no existe: no cuenta" "$d/detras.md"

# Controles.
cat > "$d/solo.md" <<'PLAN'
# Un plan

## Parte 2 · Plan

### Etapa final · Cierre

- [ ] `/plantilla-review`

## Revisiones
PLAN
caso 0 "control: la palabra solo en la etapa final, nombra plantilla-review" "$d/solo.md"
python3 -I "$G" >/dev/null; c=$?
[ "$c" -eq 0 ] && echo "ok    control: sin argumentos, los planes del repositorio → 0" || { echo "FALLO control: sin argumentos → $c (esperado 0)"; fallos=$((fallos + 1)); }

echo; [ "$fallos" -eq 0 ] && { echo "skills_de_cierre_test: todo pasa"; exit 0; } || { echo "skills_de_cierre_test: $fallos fallos"; exit 1; }

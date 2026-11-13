#!/usr/bin/env bash
# manuales_test.sh · pruebas de config/checks/manuales.py (#PP29; plan docs/historico/planes/2026-11-12-pp29-manuales.md).
#
# Cada caso añade un párrafo a un manual de prueba mínimo (operación o arranque), sobre una raíz de prueba propia (con
# su cola y sus ficheros), corre manuales.py con --operacion/--arranque/--raiz y compara la salida y el mensaje. Un caso
# por rama y por exención (CA1), los manuales de prueba sin tocar (control) y, aparte, que la puerta y `calidad` lo
# corren (CA3). Nunca lee el repositorio vivo, salvo para CA3. Salida: 0 todo pasa · 1 alguno falla · 3 no pude medir.
set -u
cd "$(dirname "$0")/../.." || exit 3
M=config/checks/manuales.py
fallos=0
R="$(mktemp -d)" || exit 3
trap 'rm -rf "$R"' EXIT
mkdir -p "$R/docs/manuales" "$R/docs/mediciones" "$R/config/checks"
: > "$R/docs/mediciones/2026-01-01-x.md"
: > "$R/docs/manuales/notas-2026-01-01.md"
: > "$R/config/checks/existe.py"
cat > "$R/docs/deuda-tecnica.md" <<'EOF'
# La cola

## Abiertos

### #PP1 · 2026-01-01 · Una abierta (higiene)

## Cerrados

| Id | Qué | Commit |
|---|---|---|
| `#PP2` | Una cerrada | `abc1234` |
EOF
cat > "$R/op.base" <<'EOF'
# Manual de operación (de prueba)

**Cómo leer las marcas:**
- **[medido]**: se corrió, con la fecha.
- **[leído]**: está escrito.
- **[sin medir]**: propuesta.

## §1 · Uno

Texto [medido 2026-01-01].

| Regla de §1 | La prueba |
|---|---|
| **Algo** | ¿Se cumple? |

## §2 · Dos

Texto.

| Regla de §2 | La prueba |
|---|---|
| **Otra** | `existe.py` |

### §2.1 · Sub

Texto.

| Regla de §2.1 | La prueba |
|---|---|
| **La de la sub** | ¿Sí? |
EOF
cat > "$R/ar.base" <<'EOF'
# Manual de arranque (de prueba)

**Cómo leer las marcas.** Cada afirmación lleva una:
- **[medido]**: se corrió.

## 0 · Filosofía

Texto [medido].

## 1 · Stack

### FASE 3 · Contratos

Texto.
EOF

corre() { # corre <op> <ar>: deja RC y SALIDA
  SALIDA="$(python3 -I "$M" --operacion "$1" --arranque "$2" --raiz "$R" 2>&1)"; RC=$?
}
caso() { # caso <esperado> <op|ar> "<nombre>" "<párrafo>" [<texto que la salida debe contener>]
  local esperado="$1" donde="$2" nombre="$3" parrafo="$4" debe="${5:-}"
  cp "$R/op.base" "$R/docs/manuales/op.md"; cp "$R/ar.base" "$R/docs/manuales/ar.md"
  printf '\n%s\n' "$parrafo" >> "$R/docs/manuales/$donde.md"
  corre "$R/docs/manuales/op.md" "$R/docs/manuales/ar.md"
  if [ "$RC" -ne "$esperado" ]; then
    echo "FALLO $nombre: sale $RC (esperado $esperado)"; printf '%s\n' "$SALIDA" | sed 's/^/        /'
    fallos=$((fallos + 1))
  elif [ -n "$debe" ] && ! printf '%s' "$SALIDA" | grep -qF -- "$debe"; then
    echo "FALLO $nombre: la salida no dice «$debe»"; printf '%s\n' "$SALIDA" | sed 's/^/        /'
    fallos=$((fallos + 1))
  else
    echo "ok    $nombre: sale $RC"
  fi
}

echo "--- control"
caso 0 op "los manuales de prueba, sin tocar" ""

echo "--- C1 · citas por número"
caso 0 op "C1 §N.M que existe" "Ver §2.1."
caso 1 op "C1 §N.M que no existe" "Ver §2.5." "C1"
caso 1 ar "C1 §0.1 en el arranque" "Ver §0.1." "C1"
caso 0 ar "C1 §2.1 del manual de operación" "Ver el manual de operación §2.1."
caso 1 op "C1 paso N" "Salta al paso 7." "C1"
caso 1 op "C1 hallazgo N" "Ver el hallazgo 5." "C1"
caso 1 op "C1 gotcha N" "Ver la gotcha 9." "C1"
caso 1 ar "C1 notas N sin fichero fechado" "Las notas 6 y 7 del catálogo." "C1"
caso 0 ar "C1 exenta por fichero fechado (D2)" 'Las notas 6 y 7 de `docs/mediciones/2026-01-01-x.md`.'
caso 1 op "C1 dentro de un PROMPT text" "$(printf '```text\nVer §2.5.\n```')" "C1"
caso 0 op "C1 dentro de un bloque bash, no" "$(printf '```bash\necho §2.5\n```')"
caso 0 op "C1 en código en línea, no" 'El patrón `§2.5` es un ejemplo.'

echo "--- C2 · secciones"
caso 0 op "C2 §N que existe" "Ver §2."
caso 1 op "C2 §N que no existe" "Ver §9." "C2"
caso 0 ar "C2 §N del arranque" "Ver §1."
caso 1 ar "C2 §N que el arranque no tiene" "Ver §7." "C2"
caso 0 ar "C2 §7 del arranque a operación, no" "Ver el manual de operación §2."
caso 1 ar "C2 §N de operación que no existe" "Ver el manual de operación §9." "C2"
caso 0 ar "C2 CLAUDE.md §N no se resuelve" 'Ver `CLAUDE.md` §11.'
caso 0 op "C2 FASE que existe" "Ver v2, FASE 3."
caso 1 op "C2 FASE que no existe" "Ver v2, FASE 9." "C2"

echo "--- C3 · punteros"
caso 0 op "C3 ruta fechada que existe" 'Ver `docs/mediciones/2026-01-01-x.md`.'
caso 1 op "C3 ruta fechada que no existe" 'Ver `docs/mediciones/2099-01-01-x.md`.' "C3"
caso 0 ar "C3 ruta fechada desde docs/manuales" 'Ver `notas-2026-01-01.md`.'
caso 0 op "C3 guion que existe" 'Lo corre `existe.py`.'
caso 1 op "C3 guion que no existe" 'Lo corre `falta.py`.' "C3"
caso 0 op "C3 guion del origen" 'El `falta.py` del origen.'
caso 0 ar "C3 guion bajo un encabezado del origen" "$(printf '### Lo que quedó fuera por ser del origen\n\nEl chequeo (`falta.py`).')"
caso 0 op "C3 guion no montado" 'Pide `falta.py` (no montado).'
caso 0 op "C3 #PP abierta" "Ver #PP1."
caso 0 op "C3 #PP cerrada" 'Ver `#PP2`.'
caso 1 op "C3 #PP que no existe" "Ver #PP99." "C3"
caso 0 op "C3 #AB1, la forma" 'Se cita por su id (`#AB1`).'

echo "--- C5 · marcas"
caso 0 op "C5 marca leído" "Texto [leído]."
caso 1 op "C5 marca fuera de forma" "Texto [derivación mía]." "C5"
caso 1 op "C5 marca partida entre líneas" "$(printf 'Texto [derivación\nmía].')" "C5"
caso 0 op "C5 marca partida en una línea sangrada" "$(printf -- "- Texto [sin\n  medir].")"
caso 0 op "C5 medido con fecha" "Texto [medido 2026-01-01]."
caso 1 op "C5 medido sin fecha en operación" "Texto [medido: lo conté]." "C5"
caso 0 ar "C5 medido sin fecha en el arranque" "Texto [medido: lo conté]."
caso 0 op "C5 casilla" "- [ ] Una casilla"
caso 0 op "C5 bandera" "Marca [TOCA-iOS] y [DECISIÓN-DE-PRODUCTO]."
caso 0 op "C5 enlace" "Ver [la ficha](x.md)."
caso 0 op "C5 dentro de un PROMPT, no" "$(printf '```text\nVer [#id] y [derivación].\n```')"

echo "--- C8 · tablas de pruebas"
caso 1 op "C8 sección sin tabla" "$(printf '## §3 · Tres\n\nSin tabla.')" "C8"
caso 1 op "C8 prueba que es un enunciado" "$(printf '## §3 · Tres\n\n| Regla de §3 | La prueba |\n|---|---|\n| **X** | Un enunciado |')" "C8"
caso 0 op "C8 prueba con pregunta" "$(printf '## §3 · Tres\n\n| Regla de §3 | La prueba |\n|---|---|\n| **X** | ¿Se cumple? |')"
caso 0 op "C8 prueba con guion" "$(printf '## §3 · Tres\n\n| Regla de §3 | La prueba |\n|---|---|\n| **X** | `existe.py` |')"
caso 1 op "C8 prueba con guion que no existe" "$(printf '## §3 · Tres\n\n| Regla de §3 | La prueba |\n|---|---|\n| **X** | `falta.py` lo mira |')" "C8"

echo "--- sin manual"
corre "$R/no-existe.md" "$R/ar.base"
[ "$RC" -eq 3 ] && echo "ok    sin manual: sale 3" || { echo "FALLO sin manual: sale $RC (esperado 3)"; fallos=$((fallos + 1)); }

echo "--- CA3 · la puerta y calidad lo corren"
grep -qE '^VERIFICADORES=.*manuales\.py' config/checks/puerta.sh &&
  echo "ok    la puerta lo lista" || { echo "FALLO la puerta lo lista"; fallos=$((fallos + 1)); }
grep -qF 'config/checks/manuales.py' .github/actions/calidad/action.yml &&
  echo "ok    calidad lo corre" || { echo "FALLO calidad lo corre"; fallos=$((fallos + 1)); }

echo
[ "$fallos" -eq 0 ] && { echo "manuales_test: todo pasa"; exit 0; } || { echo "manuales_test: $fallos fallos"; exit 1; }

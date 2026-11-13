#!/usr/bin/env bash
# config/derivar.sh <paquete> "<Nombre visible>" [<RaizGradle>]
#
# Renombra la plantilla en un clon recién creado: el paquete de Kotlin y sus carpetas, el applicationId, el bundle id de
# iOS, el nombre visible, rootProject.name (de él sale el paquete de recursos de Compose), la clase Application y el CI.
# Plan: docs/historico/planes/2026-11-12-derivar.md. Test: config/checks/derivar_test.sh.
#
# - No toca los registros: docs/historico, docs/mediciones, docs/plans y docs/manuales.
# - No reescribe las líneas que dicen «guion bajo»: son la excepción del paquete de la plantilla y su historia.
# - No commitea: deja todo en el índice para un commit con la puerta.
# - Al terminar se borra a sí mismo, con su test y sus líneas en la puerta, en `calidad` y en entregables: la plantilla se
#   deriva una vez, y su test fallaría en el proyecto nuevo.
#
# La raíz de Gradle, por defecto, es el nombre sin lo que no sea [A-Za-z0-9]. Si el nombre lleva letras no ASCII, la raíz
# es obligatoria (el nombre visible admite acentos: quien decide, 2026-11-12).
# Salida: 0 · 1 (quedó una forma vieja que no sabe tratar: la nombra y no se borra) · 2 (uso o entrada mala: no toca nada).
set -u

VIEJO=org.example.proyecto_plantilla
VIEJO_RUTA=org/example/proyecto_plantilla
FORMAS='proyecto[ _-]?plantilla'
REGISTROS=(':!docs/historico' ':!docs/mediciones' ':!docs/plans' ':!docs/manuales'
  ':!config/derivar.sh' ':!config/checks/derivar_test.sh')

no() { echo "derivar: $1" >&2; exit 2; }

comprueba_entradas() {
  [ "$#" -ge 2 ] && [ "$#" -le 3 ] || no "uso: config/derivar.sh <paquete> \"<Nombre visible>\" [<RaizGradle>]"
  PAQ="$1"; NOMBRE="$2"
  printf '%s' "$PAQ" | grep -Eq '^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$' ||
    no "el paquete «$PAQ» no es válido: segmentos en minúsculas separados por puntos (com.miempresa.miapp)"
  case "$PAQ" in "$VIEJO" | "$VIEJO".*) no "el paquete «$PAQ» es (o está dentro de) el de la plantilla" ;; esac
  case "$VIEJO" in "$PAQ".*) no "el paquete «$PAQ» contiene al de la plantilla" ;; esac
  [ -n "$NOMBRE" ] || no "el nombre visible está vacío"
  case "$NOMBRE" in *[\"\\/\<\>\&\$]*) no "el nombre visible no puede llevar \" \\ / < > & \$" ;; esac
  if [ "$#" -eq 3 ]; then
    RAIZ_G="$3"
  elif printf '%s' "$NOMBRE" | LC_ALL=C grep -q '[^ -~]'; then
    no "el nombre «$NOMBRE» lleva letras no ASCII: pasa la raíz de Gradle como tercer argumento (p. ej. MiCafe)"
  else
    RAIZ_G="$(printf '%s' "$NOMBRE" | LC_ALL=C tr -cd 'A-Za-z0-9')"
  fi
  printf '%s' "$RAIZ_G" | grep -Eq '^[A-Za-z][A-Za-z0-9]*$' ||
    no "la raíz de Gradle «$RAIZ_G» no es válida: letras y cifras ASCII, empezando por letra"
  for v in "$PAQ" "$NOMBRE" "$RAIZ_G"; do
    printf '%s' "$v" | grep -Eqi "$FORMAS" && no "«$v» todavía nombra la plantilla"
  done
  return 0
}

comprueba_arbol() {
  [ -z "$(git status --porcelain)" ] || no "el árbol no está limpio: deriva un clon recién creado"
  git ls-files | grep -q "/kotlin/$VIEJO_RUTA/" || no "no hay carpetas .../kotlin/$VIEJO_RUTA/: ya derivado"
}

mueve_carpetas() {
  local f d raices=""
  while IFS= read -r -d '' f; do
    case "$f" in */kotlin/"$VIEJO_RUTA"/*) d="${f%%/kotlin/"$VIEJO_RUTA"/*}/kotlin/" ;; *) continue ;; esac
    case "$raices" in *"|$d|"*) ;; *) raices="$raices|$d|" ;; esac
  done < <(git ls-files -z)
  local IFS='|'
  for d in $raices; do
    [ -n "$d" ] || continue
    mkdir -p "$d$(dirname "$NUEVA_RUTA")" && git mv "$d$VIEJO_RUTA" "$d$NUEVA_RUTA" || return 1
    find "${d%/}" -type d -empty -delete
  done
}

reescribe() {
  local f
  while IFS= read -r -d '' f; do
    D_PAQ="$PAQ" D_RUTA="$NUEVA_RUTA" D_RES="$RES" D_BUNDLE="$BUNDLE" D_RAIZ="$RAIZ_G" D_NOMBRE="$NOMBRE" \
      perl -pi -e 'next if /guion bajo/i;
        s/\Qorg.example.proyectoplantilla.ProyectoPlantilla\E/$ENV{D_BUNDLE}/g;
        s/\Qorg.example.proyecto_plantilla\E/$ENV{D_PAQ}/g;
        s/\Qorg\/example\/proyecto_plantilla\E/$ENV{D_RUTA}/g;
        s/\Qproyectoplantilla.shared\E/$ENV{D_RES}.shared/g;
        s/rootProject\.name = "ProyectoPlantilla"/rootProject.name = "$ENV{D_RAIZ}"/g;
        s/ProyectoPlantillaApp/$ENV{D_RAIZ}App/g;
        s/Proyecto Plantilla/$ENV{D_NOMBRE}/g;' "$f" || return 1
  done < <(git grep -lzI -i -E "$FORMAS" -- . "${REGISTROS[@]}")
  while IFS= read -r -d '' f; do
    git mv "$f" "$(dirname "$f")/${RAIZ_G}App.kt" || return 1
  done < <(git ls-files -z -- '*/ProyectoPlantillaApp.kt')
}

quedan() {
  git grep -n -I -i -E "$FORMAS" -- . "${REGISTROS[@]}" |
    grep -v -i 'guion bajo' | grep -v -E '^[^:]*\.example:[0-9]+:.*proyecto-plantilla-ejemplo'
}

se_borra() {
  git rm -q config/derivar.sh config/checks/derivar_test.sh &&
    perl -pi -e 's/ derivar_test\.sh// if /^VERIFICADORES=/' config/checks/puerta.sh &&
    perl -0pi -e 's/    - name: [^\n]*\n      shell: bash\n      run: bash config\/checks\/derivar_test\.sh\n//' \
      .github/actions/calidad/action.yml &&
    perl -ni -e 'print unless /derivar/' docs/entregables-del-arranque.md
}

main() {
  comprueba_entradas "$@"
  cd "$(git rev-parse --show-toplevel 2>/dev/null)" 2>/dev/null || no "no estoy dentro de un repositorio git"
  comprueba_arbol
  NUEVA_RUTA="${PAQ//.//}"
  RES="$(printf '%s' "$RAIZ_G" | tr 'A-Z' 'a-z')"
  BUNDLE="${PAQ//_/}.$RAIZ_G"
  echo "derivar: $VIEJO → $PAQ · «$NOMBRE» · raíz $RAIZ_G · recursos $RES.shared · bundle id $BUNDLE"

  mueve_carpetas || { echo "derivar: falló al mover las carpetas. Para volver: git reset --hard && git clean -fd" >&2; return 1; }
  reescribe || { echo "derivar: falló al reescribir. Para volver: git reset --hard && git clean -fd" >&2; return 1; }
  local resto
  resto="$(quedan)"
  if [ -n "$resto" ]; then
    echo "derivar: quedan formas de la plantilla que no sé tratar:" >&2
    printf '%s\n' "$resto" >&2
    echo "derivar: no me borro. Para volver: git reset --hard && git clean -fd (el árbol estaba limpio)" >&2
    return 1
  fi
  se_borra || { echo "derivar: falló al borrarme. Para volver: git reset --hard && git clean -fd" >&2; return 1; }
  git add -u
  cat <<EOF
derivar: hecho, todo en el índice y sin commitear. Queda a mano:
  1. La excepción del guion bajo (.editorconfig, build.gradle.kts, config/detekt/detekt.yml, CLAUDE.md §13 y
     wiki/decisiones/paquete-con-guion-bajo.md): quítala si «$PAQ» no lleva guion bajo; si lo lleva, reescríbela.
  2. Revalida el command plantilla-contract-sync: su ruta cambió y su validación caducó (bloque B de
     .claude/validacion-sesion-nueva.md, en una sesión nueva). Hasta entonces validaciones.py pone la puerta en rojo.
  3. Commitea con la puerta: config/checks/puerta.sh --commit <mensaje>.
EOF
}

main "$@"
exit $?

#!/usr/bin/env bash
# puerta.sh [--rapido] [--commit <fichero-con-el-mensaje> [nuevo ...]]
#
# --commit: la puerta COMMITEA, y solo si pasa. `git add -u` + SOLO los ficheros nuevos nombrados (rechaza uno que no
#   exista o que ya esté rastreado); se niega si queda algo fuera del índice en todo el árbol. El mensaje va SIEMPRE por
#   fichero (heredoc <<'EOF'): dentro de comillas dobles, zsh ejecuta los backticks.
#
# LA PUERTA ES DUEÑA DEL ÁRBOL MIENTRAS CORRE: toma el candado de config/checks/candado.sh al empezar y lo suelta al
# salir (trap). Con un candado VIVO aborta; con uno HUÉRFANO lo retira y lo anuncia.
#
# LA PUERTA. Un solo guion que para en el PRIMER fallo y dice cuál fue. Cualquier código distinto de 0 es rojo, incluido
# el 3 («no pude medir»). Sin tuberías detrás de un paso: el código de salida se lee tal cual.
#
#   a) formato        spotlessCheck (la puerta NO reescribe ficheros)
#   b) detekt         por source set + androidApp
#   c) verdes         los cuatro verdes + los tests de iOS en el simulador, en UNA invocación de Gradle,
#                     con --rerun-tasks y --console=plain (--rapido quita --rerun-tasks para iterar; la puerta de un PR
#                     se pasa sin él)
#   d) verificadores  cada guion de config/checks/ que es paso de la puerta, por separado
#   e) tests          recuento en los XML de ESTA ejecución (se borran antes): falla si alguno falló o si hay CERO
#   f) conflictos     marcadores <<<<<<< / >>>>>>> en lo indexado
set -u
cd "$(dirname "$0")/../.." || exit 3
RAIZ="$(pwd)"

RERUN="--rerun-tasks"
MENSAJE=""
NUEVOS=()
while [ "$#" -gt 0 ]; do
  case "$1" in
    --rapido) RERUN="" ;;
    --commit)
      shift; [ "$#" -gt 0 ] || { echo "puerta: --commit necesita el fichero del mensaje" >&2; exit 2; }
      MENSAJE="$1"; shift; NUEVOS=("$@"); break ;;
    *) echo "puerta: argumento desconocido: $1" >&2; exit 2 ;;
  esac
  shift
done

if [ -n "$MENSAJE" ]; then
  [ -s "$MENSAJE" ] || { echo "puerta: el fichero del mensaje no existe o está vacío: $MENSAJE" >&2; exit 2; }
  for f in "${NUEVOS[@]+"${NUEVOS[@]}"}"; do
    [ -e "$f" ] || { echo "puerta: el fichero nuevo no existe: $f" >&2; exit 2; }
    # «Rastreado» = está en HEAD. Uno que solo está en el índice (de un intento anterior rechazado) sigue siendo nuevo.
    if git cat-file -e "HEAD:$f" 2>/dev/null; then
      echo "puerta: «$f» ya está rastreado; no es nuevo (va por git add -u)" >&2; exit 2
    fi
  done
fi

config/checks/candado.sh tomar
c=$?; [ "$c" -eq 0 ] || exit "$c"
trap 'config/checks/candado.sh soltar' EXIT

paso() { echo; echo "=== puerta · $1 ==="; }
rojo() { echo; echo "PUERTA ROJA en «$1» (código $2)."; exit 1; }

DETEKT_TAREAS=":androidApp:detekt"
for s in AndroidMain AndroidHostTest CommonMain CommonTest IosMain IosTest; do
  DETEKT_TAREAS="$DETEKT_TAREAS :shared:detekt${s}SourceSet"
done

VERDES=":shared:compileCommonMainKotlinMetadata :androidApp:assembleDebug :shared:testAndroidHostTest :shared:compileKotlinIosSimulatorArm64 :shared:iosSimulatorArm64Test"

# Verificadores que son paso de la puerta (los informativos, como que_va_ahora.py, no van aquí).
VERIFICADORES="entregables.py"

paso "a) formato"
./gradlew spotlessCheck --console=plain -q
c=$?; [ "$c" -eq 0 ] || rojo "a) formato (./gradlew spotlessApply lo arregla)" "$c"

paso "b) detekt"
# shellcheck disable=SC2086
./gradlew $DETEKT_TAREAS $RERUN --console=plain -q
c=$?; [ "$c" -eq 0 ] || rojo "b) detekt" "$c"

paso "c) los verdes"
RESULTADOS="$RAIZ/shared/build/test-results"
rm -rf "$RESULTADOS"
LOG_VERDES="$(mktemp "${TMPDIR:-/tmp}/puerta-verdes.XXXXXX")"
# shellcheck disable=SC2086
./gradlew $VERDES $RERUN --console=plain > "$LOG_VERDES" 2>&1
c=$?
tail -n 15 "$LOG_VERDES"
[ "$c" -eq 0 ] || { echo "(log completo: $LOG_VERDES)"; rojo "c) los verdes" "$c"; }
# Verde no es corrió: sin --rapido, ninguna tarea que compila o testea puede salir UP-TO-DATE ni FROM-CACHE. El nombre
# de la tarea EMPIEZA por compile/test/assemble/link o TERMINA en Test (no casa con preAndroidHostTestBuild, una tarea
# de ciclo de vida sin acciones que siempre sale UP-TO-DATE).
if [ -n "$RERUN" ] && grep -E '^> Task :([A-Za-z0-9]+:)*(compile[A-Za-z0-9]*|test[A-Za-z0-9]*|assemble[A-Za-z0-9]*|link[A-Za-z0-9]*|[A-Za-z0-9]*Test) (UP-TO-DATE|FROM-CACHE)$' "$LOG_VERDES"; then
  rojo "c) los verdes: una tarea no se ejecutó (UP-TO-DATE/FROM-CACHE)" 1
fi

paso "d) verificadores"
for v in $VERIFICADORES; do
  echo "--- $v"
  python3 -I "config/checks/$v"
  c=$?; [ "$c" -eq 0 ] || rojo "d) verificador $v" "$c"
done

paso "e) recuento de tests"
python3 -I - "$RESULTADOS" <<'PY'
import glob, os, sys, xml.etree.ElementTree as ET
raiz = sys.argv[1]
xmls = glob.glob(os.path.join(raiz, "**", "TEST-*.xml"), recursive=True)
if not xmls:
    print("no hay XML de resultados de esta ejecución → NO PUDE MEDIR"); sys.exit(3)
tests = fallos = 0
for x in xmls:
    try:
        r = ET.parse(x).getroot()
    except ET.ParseError as e:
        print(f"XML ilegible {x}: {e} → NO PUDE MEDIR"); sys.exit(3)
    tests += int(r.get("tests", 0)); fallos += int(r.get("failures", 0)) + int(r.get("errors", 0))
print(f"{tests} tests en {len(xmls)} ficheros, {fallos} fallos")
if tests == 0:
    print("CERO tests: un verde sin tests no es de nadie"); sys.exit(1)
sys.exit(1 if fallos else 0)
PY
c=$?; [ "$c" -eq 0 ] || rojo "e) recuento de tests" "$c"

paso "f) marcadores de conflicto"
if git grep --cached -nE '^(<<<<<<<|>>>>>>>)( |$)' -- . ':!config/checks/puerta.sh'; then
  rojo "f) marcadores de conflicto en lo indexado" 1
fi
echo "sin marcadores"

echo
echo "PUERTA VERDE."

if [ -n "$MENSAJE" ]; then
  paso "commit"
  git add -u
  for f in "${NUEVOS[@]+"${NUEVOS[@]}"}"; do git add -- "$f"; done
  fuera="$(git status --porcelain --untracked-files=all | grep -v '^[MARD] ' || true)"
  if [ -n "$fuera" ]; then
    echo "$fuera"
    echo "puerta: queda algo fuera del índice; nómbralo como nuevo o retíralo. No commiteo."
    exit 1
  fi
  git commit -q -F "$MENSAJE"
  c=$?; [ "$c" -eq 0 ] || rojo "commit" "$c"
  git log --oneline -1
fi
exit 0

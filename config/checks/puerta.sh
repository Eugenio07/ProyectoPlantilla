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
#   c) verdes         los cuatro verdes + los tests de iOS en el simulador + release de Android compilado (#PP13) +
#                     sin stub en release (#PP12), en UNA invocación de Gradle,
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

VERDES=":shared:compileCommonMainKotlinMetadata :androidApp:assembleDebug :androidApp:assembleRelease :androidApp:verificarSinStubEnRelease :shared:testAndroidHostTest :shared:compileKotlinIosSimulatorArm64 :shared:iosSimulatorArm64Test"

# Verificadores que son paso de la puerta (los informativos, como que_va_ahora.py, no van aquí).
VERIFICADORES="entregables.py recuento_tests_test.sh versiones.py versiones_test.sh cola.py tope_plan.py wiki.py sin_para_claude_md.py claude_md.py hooks_test.sh skills_de_cierre.py skills_de_cierre_test.sh validaciones.py"
# validaciones.py (#PP7): cambiar una skill, un command o un agente caduca su validación y pone la puerta roja hasta
# validarlo de nuevo (.claude/validacion-sesion-nueva.md, bloque B).

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
# Verde no es corrió: sin --rapido, ninguna tarea que compila o testea puede salir UP-TO-DATE ni FROM-CACHE.
if [ -n "$RERUN" ]; then
  config/checks/corrio.sh "$LOG_VERDES"
  c=$?; [ "$c" -eq 0 ] || rojo "c) los verdes: una tarea no se ejecutó" "$c"
fi

paso "d) verificadores"
for v in $VERIFICADORES; do
  echo "--- $v"
  case "$v" in
    *.sh) bash "config/checks/$v" ;;
    *) python3 -I "config/checks/$v" ;;
  esac
  c=$?; [ "$c" -eq 0 ] || rojo "d) verificador $v" "$c"
done

paso "e) recuento de tests"
python3 -I config/checks/recuento_tests.py --exige testAndroidHostTest,iosSimulatorArm64Test "$RESULTADOS"
c=$?; [ "$c" -eq 0 ] || rojo "e) recuento de tests" "$c"

# En modo --commit se indexa AQUÍ lo que se va a commitear, para que f) mire exactamente eso (un marcador en un fichero
# rastreado pero sin indexar se escapaba: medido 2026-11-11, #PP9).
if [ -n "$MENSAJE" ]; then
  git add -u
  for f in "${NUEVOS[@]+"${NUEVOS[@]}"}"; do git add -- "$f"; done
fi

paso "f) marcadores de conflicto"
if git grep --cached -nE '^(<<<<<<<|>>>>>>>)( |$)' -- . ':!config/checks/puerta.sh'; then
  rojo "f) marcadores de conflicto en lo indexado" 1
fi
echo "sin marcadores"

echo
echo "PUERTA VERDE."

if [ -n "$MENSAJE" ]; then
  paso "commit"
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

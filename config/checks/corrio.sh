#!/usr/bin/env bash
# corrio.sh <log-de-gradle-con---console=plain>
# «Verde no es corrió» (v2 §2): falla si alguna tarea que compila o testea salió UP-TO-DATE o FROM-CACHE. El nombre de
# la tarea EMPIEZA por compile/test/assemble/link o TERMINA en Test (no casa con preAndroidHostTestBuild, una tarea de
# ciclo de vida sin acciones que siempre sale UP-TO-DATE). La usan la puerta y el CI: un criterio, dos disparadores.
# Salida: 0 todas corrieron · 1 alguna no · 3 no pude leer el log (o no tiene ninguna tarea: no es verde).
set -u
log="${1:-}"
[ -r "$log" ] || { echo "corrio.sh: no puedo leer el log «$log» → NO PUDE MEDIR"; exit 3; }
grep -qE '^> Task :' "$log" || { echo "corrio.sh: el log no tiene ninguna tarea → NO PUDE MEDIR"; exit 3; }
if grep -E '^> Task :([A-Za-z0-9]+:)*(compile[A-Za-z0-9]*|test[A-Za-z0-9]*|assemble[A-Za-z0-9]*|link[A-Za-z0-9]*|[A-Za-z0-9]*Test) (UP-TO-DATE|FROM-CACHE)$' "$log"; then
  echo "corrio.sh: estas tareas NO se ejecutaron: su verde no es de esta ejecución"
  exit 1
fi
echo "corrio.sh: todas las tareas que compilan o testean se ejecutaron"
exit 0

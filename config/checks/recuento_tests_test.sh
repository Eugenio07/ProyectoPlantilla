#!/usr/bin/env bash
# Pruebas de recuento_tests.py (#PP15): con --exige, falta el XML de una tarea exigida → 1. Sale 0 si todo pasa, 1 si no.
# Sin Gradle: monta directorios de resultados falsos.
set -u
cd "$(dirname "$0")/../.." || exit 3
R=config/checks/recuento_tests.py
fallos=0
ok() { echo "ok    $1"; }
ko() { echo "FALLO $1"; fallos=$((fallos + 1)); }
d="$(mktemp -d)"; trap 'rm -rf "$d"' EXIT
xml() { mkdir -p "$d/$1/$2"; printf '<testsuite tests="%s" failures="0" errors="0"/>' "$3" > "$d/$1/$2/TEST-x.xml"; }
xml dos testAndroidHostTest 3; xml dos iosSimulatorArm64Test 2
xml solo_android testAndroidHostTest 3
E=testAndroidHostTest,iosSimulatorArm64Test

python3 -I "$R" --exige "$E" "$d/dos" >/dev/null; c=$?
[ "$c" -eq 0 ] && ok "las dos plataformas → 0" || ko "las dos plataformas → $c (esperado 0)"
python3 -I "$R" --exige "$E" "$d/solo_android" >/dev/null; c=$?
[ "$c" -eq 1 ] && ok "falta iOS → 1" || ko "falta iOS → $c (esperado 1)"
python3 -I "$R" --exige testAndroidHostTest "$d/solo_android" >/dev/null; c=$?
[ "$c" -eq 0 ] && ok "exige solo Android y está → 0" || ko "exige solo Android → $c (esperado 0)"
python3 -I "$R" "$d/solo_android" >/dev/null; c=$?
[ "$c" -eq 0 ] && ok "sin --exige, como antes → 0" || ko "sin --exige → $c (esperado 0)"
mkdir -p "$d/vacio"; python3 -I "$R" --exige "$E" "$d/vacio" >/dev/null; c=$?
[ "$c" -eq 3 ] && ok "sin ningún XML → 3 (no pude medir)" || ko "sin XML → $c (esperado 3)"

echo; [ "$fallos" -eq 0 ] && { echo "recuento_tests_test: todo pasa"; exit 0; } || { echo "recuento_tests_test: $fallos fallos"; exit 1; }

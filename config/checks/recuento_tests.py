#!/usr/bin/env python3
"""recuento_tests.py [--exige <tarea>[,<tarea>…]] <directorio-de-resultados>
Cuenta los tests de los XML JUnit (TEST-*.xml) del directorio. Falla si alguno falló o si hay CERO: un verde sin tests
no es de nadie. Quien lo llama BORRA antes los resultados viejos (si no, puede contar una ejecución anterior).
--exige: cada tarea nombrada debe tener XML en <directorio>/<tarea>/ (#PP15): una plataforma que no corrió no se tapa
con los tests de otra. Que falte es un hecho medido (1), no «no pude medir»; sin NINGÚN XML sigue siendo 3.
La usan la puerta y el CI. Salida: 0 · 1 (fallos, cero tests o falta una tarea exigida) · 3 (sin XML o XML ilegible).
Pruebas: config/checks/recuento_tests_test.sh.
"""
import glob
import os
import sys
import xml.etree.ElementTree as ET


def main():
    args = sys.argv[1:]
    exigidas = []
    if len(args) == 3 and args[0] == "--exige":
        exigidas = [t for t in args[1].split(",") if t]
        args = args[2:]
    if len(args) != 1:
        print("uso: recuento_tests.py [--exige <tarea>[,<tarea>…]] <directorio>")
        return 2
    xmls = glob.glob(os.path.join(args[0], "**", "TEST-*.xml"), recursive=True)
    if not xmls:
        print("recuento_tests.py: no hay XML de resultados → NO PUDE MEDIR")
        return 3
    tests = fallos = 0
    for x in xmls:
        try:
            r = ET.parse(x).getroot()
        except ET.ParseError as e:
            print(f"recuento_tests.py: XML ilegible {x}: {e} → NO PUDE MEDIR")
            return 3
        tests += int(r.get("tests", 0))
        fallos += int(r.get("failures", 0)) + int(r.get("errors", 0))
    print(f"{tests} tests en {len(xmls)} ficheros, {fallos} fallos")
    faltan = [t for t in exigidas if not glob.glob(os.path.join(args[0], t, "**", "TEST-*.xml"), recursive=True)]
    if faltan:
        print(f"recuento_tests.py: sin resultados de {', '.join(faltan)}: esa plataforma no corrió")
        return 1
    if tests == 0:
        print("recuento_tests.py: CERO tests")
        return 1
    return 1 if fallos else 0


if __name__ == "__main__":
    sys.exit(main())

#!/usr/bin/env python3
"""recuento_tests.py <directorio-de-resultados>
Cuenta los tests de los XML JUnit (TEST-*.xml) del directorio. Falla si alguno falló o si hay CERO: un verde sin tests
no es de nadie. Quien lo llama BORRA antes los resultados viejos (si no, puede contar una ejecución anterior).
La usan la puerta y el CI. Salida: 0 · 1 (fallos o cero tests) · 3 (sin XML o XML ilegible).
"""
import glob
import os
import sys
import xml.etree.ElementTree as ET


def main():
    if len(sys.argv) != 2:
        print("uso: recuento_tests.py <directorio>")
        return 2
    xmls = glob.glob(os.path.join(sys.argv[1], "**", "TEST-*.xml"), recursive=True)
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
    if tests == 0:
        print("recuento_tests.py: CERO tests")
        return 1
    return 1 if fallos else 0


if __name__ == "__main__":
    sys.exit(main())

#!/usr/bin/env python3
"""Comprueba que cada artefacto de docs/entregables-del-arranque.md existe.

Lee la tabla (columna «Ruta», la ruta entre comillas invertidas) y comprueba cada ruta relativa a la raíz del repo.
Salida: 0 todas existen · 1 falta alguna (y dice cuál) · 3 no pude medir (no pude leer la lista o no reconocí ninguna
fila: una lista vacía NO es verde).
"""
import os
import re
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
LISTA = os.path.join(RAIZ, "docs", "entregables-del-arranque.md")
FILA = re.compile(r"^\|\s*[^|]*\|\s*`([^`]+)`\s*\|")


def main() -> int:
    try:
        with open(LISTA, encoding="utf-8") as f:
            lineas = f.readlines()
    except OSError as e:
        print(f"entregables.py: no pude leer {LISTA}: {e} → NO PUDE MEDIR")
        return 3
    rutas = [m.group(1) for m in (FILA.match(l) for l in lineas) if m]
    if not rutas:
        print("entregables.py: no reconocí ninguna fila en la lista → NO PUDE MEDIR")
        return 3
    faltan = [r for r in rutas if not os.path.exists(os.path.join(RAIZ, r))]
    for r in faltan:
        print(f"FALTA: {r}")
    if faltan:
        return 1
    print(f"entregables.py: {len(rutas)} entregables, todos presentes")
    return 0


if __name__ == "__main__":
    sys.exit(main())

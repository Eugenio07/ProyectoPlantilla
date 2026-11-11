#!/usr/bin/env python3
"""Tope de un plan (manual de operación §2, «El plan por dentro»). Paso de la puerta.

Para cada plan de docs/plans/ (salvo _plantilla.md) cuenta las líneas que el tope cubre, sin las secciones excluidas
en config/checks/tope_plan_exclusiones.txt, y falla si pasan del umbral. Si no cabe, son dos features.
UMBRAL: 150 líneas, fijado el 2026-11-11 con el valor del manual («~150»).
Salida: 0 · 1 (un plan pasa del tope, o una exclusión no tiene motivo) · 3 (no reconoce las secciones de un plan, o no
puede leer las exclusiones).
"""
import glob
import os
import re
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
UMBRAL = 150
SECCION = re.compile(r"^(#{2,3}) (.+?)\s*$")


def exclusiones():
    ruta = os.path.join(RAIZ, "config/checks/tope_plan_exclusiones.txt")
    out, sin_motivo = [], []
    for linea in open(ruta, encoding="utf-8"):
        linea = linea.strip()
        if not linea or linea.startswith("#"):
            continue
        titulo, _, motivo = (p.strip() for p in linea.partition("|"))
        (out if motivo else sin_motivo).append(titulo)
    return out, sin_motivo


def contar(ruta, excluidas):
    lineas = open(ruta, encoding="utf-8").read().splitlines()
    if not any(SECCION.match(l) for l in lineas):
        return None
    total, saltando, nivel = 0, False, 0
    for l in lineas:
        m = SECCION.match(l)
        if m:
            if saltando and len(m.group(1)) <= nivel:
                saltando = False
            if m.group(2) in excluidas:
                saltando, nivel = True, len(m.group(1))
        if not saltando:
            total += 1
    return total


def main():
    try:
        excluidas, sin_motivo = exclusiones()
    except OSError as e:
        print(f"tope_plan.py: no pude leer las exclusiones: {e} → NO PUDE MEDIR")
        return 3
    fallos = [f"exclusión sin motivo: «{t}»" for t in sin_motivo]
    planes = [p for p in glob.glob(os.path.join(RAIZ, "docs/plans/*.md")) if not p.endswith("_plantilla.md")]
    for p in sorted(planes):
        n = contar(p, excluidas)
        if n is None:
            print(f"tope_plan.py: no reconozco las secciones de {os.path.relpath(p, RAIZ)} → NO PUDE MEDIR")
            return 3
        if n > UMBRAL:
            fallos.append(f"{os.path.relpath(p, RAIZ)}: {n} líneas > {UMBRAL} (si no cabe, son dos features)")
    for f in fallos:
        print("FALLO:", f)
    if fallos:
        return 1
    print(f"tope_plan.py: {len(planes)} planes dentro del tope de {UMBRAL}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

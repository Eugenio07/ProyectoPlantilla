#!/usr/bin/env python3
"""Verificador de wiki/ (paso de la puerta).

Falla si una página de wiki/decisiones/ no tiene Firma, Fecha y Estado; si una de wiki/trampas/ no tiene Síntoma, Causa
y Regla; o si CLAUDE.md apunta a una página de wiki/ que no existe.
Salida: 0 · 1 · 3 (no encuentra CLAUDE.md o wiki/).
"""
import glob
import os
import re
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
CAMPOS = {"decisiones": ["**Firma:**", "**Fecha:**", "**Estado:**"], "trampas": ["**Síntoma:**", "**Causa:**", "**Regla:**"]}


def main():
    claude = os.path.join(RAIZ, "CLAUDE.md")
    if not os.path.isfile(claude) or not os.path.isdir(os.path.join(RAIZ, "wiki")):
        print("wiki.py: falta CLAUDE.md o wiki/ → NO PUDE MEDIR")
        return 3
    fallos = []
    for tipo, campos in CAMPOS.items():
        for p in sorted(glob.glob(os.path.join(RAIZ, "wiki", tipo, "*.md"))):
            texto = open(p, encoding="utf-8").read()
            for c in campos:
                if c not in texto:
                    fallos.append(f"{os.path.relpath(p, RAIZ)}: falta {c}")
    punteros = re.findall(r"wiki/(?:decisiones|trampas)/[A-Za-z0-9_.-]+\.md", open(claude, encoding="utf-8").read())
    for ptr in sorted(set(punteros)):
        if not os.path.isfile(os.path.join(RAIZ, ptr)):
            fallos.append(f"CLAUDE.md apunta a una página que no existe: {ptr}")
    for f in fallos:
        print("FALLO:", f)
    if fallos:
        return 1
    print(f"wiki.py: {len(set(punteros))} punteros de CLAUDE.md, todos con su página; campos completos")
    return 0


if __name__ == "__main__":
    sys.exit(main())

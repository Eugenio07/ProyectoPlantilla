#!/usr/bin/env python3
"""Verificador del registro de validaciones (.claude/validaciones.md). v2, Fase 11.

Falla si una skill (.claude/skills/*/SKILL.md), un command (.claude/commands/*.md) o un agente (.claude/agents/*.md) no
tiene fila; si alguno de sus tres pasos no es «pasó»; o si el fichero cambió desde la validación (su `git hash-object`
no es el de la columna «Validado sobre»: la validación CADUCA).

LO QUE NO COMPRUEBA: que la validación pasara de verdad. La validación es una conversación y su resultado lo escribe una
persona o un agente: esto comprueba que alguien DECLARÓ que pasó (comprobación humana declarada, manual de operación §4).
Un verde suyo se informa como «declarada», nunca como «verificada». Y que una skill no esté en .claude/ no significa que
falte: puede estar habilitada como plugin.

Salida: 0 · 1 · 3 (no puede leer el registro o la tabla).
"""
import glob
import os
import re
import subprocess
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def piezas():
    out = {}
    for p in glob.glob(os.path.join(RAIZ, ".claude/skills/*/SKILL.md")):
        out[f"skill `{os.path.basename(os.path.dirname(p))}`"] = p
    for p in glob.glob(os.path.join(RAIZ, ".claude/commands/*.md")):
        out[f"command `{os.path.splitext(os.path.basename(p))[0]}`"] = p
    for p in glob.glob(os.path.join(RAIZ, ".claude/agents/*.md")):
        out[f"agente `{os.path.splitext(os.path.basename(p))[0]}`"] = p
    return out


def main():
    try:
        texto = open(os.path.join(RAIZ, ".claude/validaciones.md"), encoding="utf-8").read()
    except OSError as e:
        print(f"validaciones.py: no pude leer el registro: {e} → NO PUDE MEDIR")
        return 3
    filas = {}
    for linea in texto.splitlines():
        if not linea.startswith("| ") or linea.startswith("| Qué") or linea.startswith("|---"):
            continue
        celdas = [c.strip() for c in linea.strip("|").split("|")]
        if len(celdas) != 9:
            print(f"validaciones.py: fila con {len(celdas)} columnas (esperaba 9) → NO PUDE MEDIR")
            return 3
        filas[celdas[0]] = celdas
    fallos = []
    for nombre, ruta in sorted(piezas().items()):
        fila = filas.get(nombre)
        if not fila:
            fallos.append(f"{nombre}: sin fila en .claude/validaciones.md")
            continue
        for paso, valor in zip(["positiva", "control", "negativa"], fila[6:9]):
            if valor != "pasó":
                fallos.append(f"{nombre}: el paso {paso} es «{valor}», no «pasó»")
        actual = subprocess.run(["git", "hash-object", ruta], capture_output=True, text=True).stdout.strip()
        validado = fila[2].strip("`")
        if actual != validado:
            fallos.append(f"{nombre}: cambió desde su validación ({validado[:10]} → {actual[:10]}): hay que revalidar")
    for f in fallos:
        print("FALLO:", f)
    if fallos:
        return 1
    print("validaciones.py: todas las piezas con su validación DECLARADA y al día (declarada, no verificada)")
    return 0


if __name__ == "__main__":
    sys.exit(main())

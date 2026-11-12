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


# Toda pieza que declare una propiedad dice qué mecanismo la sostiene, o no la declara (CLAUDE.md §4; la revisión externa,
# 2026-11-12). Las frases de capacidad YA VISTAS en las piezas; el mecanismo, nombrado en la misma línea o una vecina.
# LÍMITE (escrito): es un grep de frases. Caza las formulaciones vistas, no toda afirmación posible (no caza «solo ve»), y
# FALLA HACIA EL SILENCIO, que es lo que ya pasaba sin él. NO comprueba que el mecanismo sea cierto: basta la palabra. Una
# línea con «mecanismo» legitima a sus vecinas aunque declaren otra cosa. No mira `tools:` ni `allowed-tools:` (que
# `allowed-tools` restrinja un command está sin medir).
FRASES = re.compile(
    r"s[oó]lo lees?\b|s[oó]lo lectura|s[oó]lo reporta|s[oó]lo informa|sin acceso a|no modifica|no edita|no escribe|read-only",
    re.IGNORECASE,
)
MECANISMO = re.compile(r"mecanismo|por instrucci[oó]n", re.IGNORECASE)


def mecanismos(rutas):
    """[«pieza:línea: «frase» sin su mecanismo»] de las frases de capacidad sin el mecanismo en su línea o en una vecina."""
    fallos = []
    for ruta in rutas:
        lineas = open(ruta, encoding="utf-8").read().splitlines()
        for i, linea in enumerate(lineas):
            m = FRASES.search(linea)
            if m and not any(MECANISMO.search(lineas[j]) for j in (i - 1, i, i + 1) if 0 <= j < len(lineas)):
                fallos.append(f"{os.path.relpath(ruta, RAIZ)}:{i + 1}: «{m.group(0)}» sin su mecanismo")
    return fallos


def main():
    if len(sys.argv) > 1 and sys.argv[1] == "--mecanismos":
        if len(sys.argv) == 2:
            print("validaciones.py --mecanismos: sin ficheros → NO PUDE MEDIR")
            return 3
        fallos = mecanismos(sys.argv[2:])
        for f in fallos:
            print("FALLO:", f)
        return 1 if fallos else 0
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
    fallos += mecanismos(sorted(piezas().values()))
    for f in fallos:
        print("FALLO:", f)
    if fallos:
        return 1
    print("validaciones.py: todas las piezas con su validación DECLARADA y al día (declarada, no verificada)")
    return 0


if __name__ == "__main__":
    sys.exit(main())

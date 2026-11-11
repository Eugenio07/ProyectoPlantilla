#!/usr/bin/env python3
"""Verificador de la cola (docs/deuda-tecnica.md). Paso de la puerta.

Comprueba:
1. Identificadores únicos en el fichero de hoy: los títulos `### #PPn` de Abiertos y Aceptado a conciencia, y la primera
   columna de la tabla de Cerrados. Un id definido dos veces es rojo.
2. Ningún id REUTILIZA uno que existió: lee el historial del fichero (git); un id que desapareció y vuelve a aparecer es
   rojo. Sin historial completo (checkout superficial) sale 3, NUNCA verde.
3. En cada entrada abierta, los campos de la plantilla: Mecanismo, Quién lo desbloquea, Disparador (o Fecha de
   revisión), Vigente hasta que, Peso.
Salida: 0 pasa · 1 falla (dice qué) · 3 no pude medir.
"""
import os
import re
import subprocess
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
COLA = "docs/deuda-tecnica.md"
TITULO = re.compile(r"^### (#[A-Z]+\d+)\b")
FILA_CERRADA = re.compile(r"^\|\s*`?(#[A-Z]+\d+)`?\s*\|")
CAMPOS = ["**Mecanismo:**", "**Quién lo desbloquea:**", "**Vigente hasta que:**", "**Peso:**"]


def git(*args):
    return subprocess.run(["git", *args], cwd=RAIZ, capture_output=True, text=True)


def ids_definidos(texto):
    """[(id, sección)] de títulos y filas de Cerrados, en orden."""
    seccion, ids = None, []
    for linea in texto.splitlines():
        if linea.startswith("## "):
            seccion = linea[3:].strip()
            continue
        m = TITULO.match(linea)
        if m:
            ids.append((m.group(1), seccion))
            continue
        if seccion == "Cerrados":
            m = FILA_CERRADA.match(linea)
            if m:
                ids.append((m.group(1), seccion))
    return ids


def entradas_abiertas(texto):
    """{id: cuerpo} de las entradas de «Abiertos»."""
    out, seccion, actual = {}, None, None
    for linea in texto.splitlines():
        if linea.startswith("## "):
            seccion, actual = linea[3:].strip(), None
            continue
        m = TITULO.match(linea)
        if m and seccion == "Abiertos":
            actual = m.group(1)
            out[actual] = []
            continue
        if actual:
            out[actual].append(linea)
    return {k: "\n".join(v) for k, v in out.items()}


def main():
    ruta = os.path.join(RAIZ, COLA)
    try:
        texto = open(ruta, encoding="utf-8").read()
    except OSError as e:
        print(f"cola.py: no pude leer {COLA}: {e} → NO PUDE MEDIR")
        return 3
    if "## Abiertos" not in texto or "## Cerrados" not in texto:
        print("cola.py: no reconozco las secciones de la cola → NO PUDE MEDIR")
        return 3
    fallos = []

    vistos = {}
    for i, sec in ids_definidos(texto):
        if i in vistos:
            fallos.append(f"id duplicado: {i} (en «{vistos[i]}» y en «{sec}»)")
        vistos[i] = sec

    for i, cuerpo in entradas_abiertas(texto).items():
        for campo in CAMPOS:
            if campo not in cuerpo:
                fallos.append(f"{i}: falta el campo {campo}")
        if "**Disparador:**" not in cuerpo and "**Fecha de revisión:**" not in cuerpo:
            fallos.append(f"{i}: falta el disparador o la fecha de revisión")

    # Historial: un id que existió, desapareció y vuelve.
    somero = git("rev-parse", "--is-shallow-repository")
    if somero.returncode != 0:
        print("cola.py: no pude preguntar a git → NO PUDE MEDIR")
        return 3
    if somero.stdout.strip() == "true":
        print("cola.py: checkout superficial: sin el historial completo no puedo comprobar ids reutilizados → NO PUDE MEDIR")
        return 3
    commits = git("log", "--format=%H", "--reverse", "--", COLA)
    if commits.returncode != 0:
        print("cola.py: no pude leer el historial → NO PUDE MEDIR")
        return 3
    estados = []
    for sha in commits.stdout.split():
        c = git("show", f"{sha}:{COLA}")
        estados.append({i for i, _ in ids_definidos(c.stdout)} if c.returncode == 0 else set())
    estados.append(set(vistos))  # el fichero de hoy
    retirados = set()
    presentes = set()
    for s in estados:
        for i in s & retirados:
            fallos.append(f"id reutilizado: {i} existió, desapareció y vuelve")
        retirados |= presentes - s
        retirados -= s
        presentes = s

    for f in sorted(set(fallos)):
        print("FALLO:", f)
    if fallos:
        return 1
    print(f"cola.py: {len(vistos)} ids, {len(entradas_abiertas(texto))} abiertos, historial de {len(estados) - 1} commits: bien")
    return 0


if __name__ == "__main__":
    sys.exit(main())

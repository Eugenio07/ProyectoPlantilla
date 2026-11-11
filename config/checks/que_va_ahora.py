#!/usr/bin/env python3
"""«Qué va ahora»: la vista DERIVADA de la cola, el único «qué va ahora» que existe. Solo informa; NO va en la puerta.

Orden (manual de operación §5): requisitos de salida (la app no está publicada: van antes que todo), luego 🔴, luego 🟡,
luego funcionalidad, luego higiene; lo que no tiene marca, al final. Arriba, la hora y el commit con que se generó.
Salida: 0 · 3 si no reconoce la cola.
"""
import datetime
import os
import re
import subprocess
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
TITULO = re.compile(r"^### (#[A-Z]+\d+) · (.*)$")
ORDEN = [("(requisito de salida)", "Requisitos de salida"), ("🔴", "🔴"), ("🟡", "🟡"),
         ("(funcionalidad)", "Funcionalidad"), ("(higiene)", "Higiene")]


def main():
    try:
        texto = open(os.path.join(RAIZ, "docs/deuda-tecnica.md"), encoding="utf-8").read()
    except OSError as e:
        print(f"que_va_ahora.py: no pude leer la cola: {e} → NO PUDE MEDIR")
        return 3
    if "## Abiertos" not in texto:
        print("que_va_ahora.py: no reconozco la cola → NO PUDE MEDIR")
        return 3
    abiertos = texto.split("## Abiertos", 1)[1].split("\n## ", 1)[0]
    entradas = [m.groups() for m in (TITULO.match(l) for l in abiertos.splitlines()) if m]
    sha = subprocess.run(["git", "rev-parse", "--short", "HEAD"], cwd=RAIZ, capture_output=True, text=True).stdout.strip()
    ahora = datetime.datetime.now().astimezone().strftime("%Y-%m-%d %H:%M %z")
    print(f"Qué va ahora · generado {ahora} · commit {sha or '?'}")
    usados = set()
    for marca, nombre in ORDEN:
        grupo = [(i, t) for i, t in entradas if marca in t and i not in usados]
        if grupo:
            print(f"\n{nombre}")
            for i, t in grupo:
                print(f"  {i} · {t}")
                usados.add(i)
    resto = [(i, t) for i, t in entradas if i not in usados]
    if resto:
        print("\nSin marca de peso")
        for i, t in resto:
            print(f"  {i} · {t}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

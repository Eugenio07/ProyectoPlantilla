#!/usr/bin/env python3
"""Comprueba una AUSENCIA (paso de la puerta): si existe CLAUDE.md, docs/para-claude-md.md no puede existir (dos fuentes
de reglas derivan). La lista de entregables comprueba presencia; esto, ausencia, y por eso no cabe en ella.
Salida: 0 (CLAUDE.md sí, el otro no) · 1 (los dos) · 3 (no puedo leer el árbol, o falta CLAUDE.md).
"""
import os
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def main():
    if not os.path.isdir(os.path.join(RAIZ, "docs")):
        print("sin_para_claude_md.py: no puedo leer el árbol → NO PUDE MEDIR")
        return 3
    if not os.path.isfile(os.path.join(RAIZ, "CLAUDE.md")):
        print("sin_para_claude_md.py: falta CLAUDE.md → NO PUDE MEDIR")
        return 3
    if os.path.exists(os.path.join(RAIZ, "docs", "para-claude-md.md")):
        print("FALLO: existen CLAUDE.md y docs/para-claude-md.md: dos fuentes de reglas")
        return 1
    print("sin_para_claude_md.py: una sola fuente de reglas (CLAUDE.md)")
    return 0


if __name__ == "__main__":
    sys.exit(main())

#!/usr/bin/env python3
"""versiones.py [fichero …] · Ningún número de versión en los documentos vivos (CLAUDE.md §3, #PP16).

La fuente de las versiones es gradle/libs.versions.toml. Un número copiado a un documento vivo envejece en silencio.
Sin argumentos mira los RASTREADOS de: CLAUDE.md, README.md, wiki/**/*.md y .claude/**/*.md. Con argumentos, solo esos ficheros (pruebas).
Fuera de su alcance, a propósito: el catálogo; docs/mediciones/ y la cola (registros con fecha); los planes (viven poco);
el pbxproj y Package.resolved (SwiftPM no lee el catálogo: excepción de CLAUDE.md §13).

Patrón (heurístico, no una lista de versiones):
  A) tres segmentos: 1.2.3, 3.5.x, 2.0.0-alpha.6
  B) «Nombre x.y»: Kotlin 2.4, Ktor 3.6 — salvo tras una palabra de NUMERACIÓN de documento (Fase 6.5, §0.1) o una
     palabra corta del español («y 6.5»).
Límite declarado: una versión de dos segmentos sin nombre delante («la 2.4») no se ve.
Salida: 0 pasa · 1 hay versiones (dice dónde) · 3 no pude leer.
"""
import os
import re
import subprocess
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
TRES = re.compile(r"(?<![\w.§])\d+\.\d+\.(\d+|x)\b")
NOMBRE = re.compile(r"(?<![\w§])([A-Za-zÁ-ú][\w-]*)\s+v?(\d+\.\d+)\b(?!\.?\d)")
NO_VERSION = {"fase", "fases", "paso", "pasos", "sección", "secciones", "regla", "y", "e", "o", "u", "la", "las", "el",
              "los", "de", "del", "en", "a", "al", "con", "v", "§"}


def ficheros():
    """Solo los RASTREADOS: .claude/informes/ (ignorado, se reescribe en cada revisión) no es un documento vivo."""
    r = subprocess.run(["git", "ls-files", "-z", "--", "CLAUDE.md", "README.md", "wiki/*.md", ".claude/*.md"],
                       cwd=RAIZ, capture_output=True, text=True)
    if r.returncode != 0:
        return None
    return [f for f in r.stdout.split("\0") if f]


def hallazgos(texto):
    out = []
    for n, linea in enumerate(texto.splitlines(), 1):
        for m in TRES.finditer(linea):
            out.append((n, m.group(0)))
        for m in NOMBRE.finditer(linea):
            if m.group(1).lower() not in NO_VERSION:
                out.append((n, f"{m.group(1)} {m.group(2)}"))
    return out


def main():
    os.chdir(RAIZ) if len(sys.argv) == 1 else None
    lista = sys.argv[1:] or ficheros()
    if lista is None:
        print("versiones.py: git ls-files falló → NO PUDE MEDIR")
        return 3
    malos = 0
    for f in lista:
        try:
            texto = open(f, encoding="utf-8").read()
        except OSError as e:
            print(f"versiones.py: no pude leer {f}: {e} → NO PUDE MEDIR")
            return 3
        for n, v in hallazgos(texto):
            print(f"{f}:{n}: «{v}»")
            malos += 1
    if malos:
        print(f"versiones.py: {malos} número(s) de versión fuera del catálogo; apunta a gradle/libs.versions.toml")
        return 1
    print(f"versiones.py: {len(lista)} documentos vivos sin números de versión")
    return 0


if __name__ == "__main__":
    sys.exit(main())

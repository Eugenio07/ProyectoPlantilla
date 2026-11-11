#!/usr/bin/env python3
"""Barrido del texto que deja de ser cierto (manual de operación §2, «La etapa de cierre»). Solo informa; NO va en la
puerta. Lo usa el cierre de cada tarea que construye una salida nueva.

Busca en los recursos de texto del producto las frases que describen «la única salida» o una carencia. La lista está
en el idioma del producto (español). Salida: 0 (con o sin coincidencias) · 3 si no encuentra recursos que barrer.
"""
import glob
import os
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
FRASES = ["todavía no", "aún no", "no se puede", "por ahora", "próximamente", "más adelante", "de momento",
          "solo puedes", "única forma", "no disponible"]
RECURSOS = ["shared/src/commonMain/composeResources/values*/strings.xml",
            "shared/src/commonMain/kotlin/**/ui/safemode/*.kt"]  # la pantalla segura lleva su copy en el código


def main():
    ficheros = sorted({f for patron in RECURSOS for f in glob.glob(os.path.join(RAIZ, patron), recursive=True)})
    if not ficheros:
        print("barrido_texto.py: no encontré recursos de texto que barrer → NO PUDE MEDIR")
        return 3
    hallados = 0
    for f in ficheros:
        for n, linea in enumerate(open(f, encoding="utf-8"), 1):
            baja = linea.lower()
            for frase in FRASES:
                if frase in baja:
                    hallados += 1
                    print(f"{os.path.relpath(f, RAIZ)}:{n}: «{frase}» → {linea.strip()}")
    print(f"barrido_texto.py: {len(ficheros)} ficheros, {hallados} coincidencias (revisa si siguen siendo ciertas)")
    return 0


if __name__ == "__main__":
    sys.exit(main())

#!/usr/bin/env python3
"""Verificador de CLAUDE.md (paso de la puerta). En CLAUDE.md va la regla y su prueba; el incidente, en wiki/.

Una REGLA es un elemento de lista de primer nivel («- ») dentro de una sección («## »); sus líneas son la suya y las de
sus continuaciones/subelementos (indentadas).

FALLA (1) si una regla pasa de UMBRAL_REGLA líneas o si la MEDIA de líneas por regla pasa de UMBRAL_MEDIA. Lo que falla
es la PROPORCIÓN, que se deriva de lo que hay: un incidente contado con otras palabras sigue sumando líneas.
Solo AVISA (0, con la línea) de patrones de narración: una fecha AAAA-MM-DD fuera de un puntero, «lo enseñó», «pasó el»,
«medido el». Una lista de literales nunca está completa: por eso no decide.
EXCEPCIONES (config/checks/claude_md_excepciones.txt): no fallan por el umbral y no entran en la media; falla si una no
tiene motivo o nombra una regla que no existe; avisa si ya no hace falta.
Sale 3 si no reconoce ninguna regla o no puede leer los ficheros.

UMBRALES fijados el 2026-11-11 con el CLAUDE.md de partida (54 reglas, máximo 4 líneas, media 1,78).
"""
import os
import re
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
UMBRAL_REGLA = 3
UMBRAL_MEDIA = 2.0
PATRONES = [re.compile(r"(?<![/\w-])\d{4}-\d{2}-\d{2}(?![\w-]*\.md)"), re.compile("lo enseñó", re.I),
            re.compile(r"pasó el", re.I), re.compile(r"medido el", re.I)]


def reglas_de(texto):
    out, seccion, actual = [], None, None
    for n, linea in enumerate(texto.splitlines(), 1):
        if linea.startswith("## "):
            seccion, actual = linea[3:].strip(), None
            continue
        if seccion is None:
            continue
        if linea.startswith("- "):
            actual = {"seccion": seccion, "texto": linea[2:], "lineas": [linea], "n": n}
            out.append(actual)
        elif actual is not None and linea.startswith("  ") and linea.strip():
            actual["lineas"].append(linea)
        else:
            actual = None
    return out


def leer_excepciones(ruta):
    out = []
    for linea in open(ruta, encoding="utf-8"):
        linea = linea.strip()
        if not linea or linea.startswith("#"):
            continue
        partes = [p.strip() for p in linea.split("|")]
        while len(partes) < 3:
            partes.append("")
        out.append({"seccion": partes[0], "inicio": partes[1], "motivo": partes[2], "linea": linea})
    return out


def main():
    try:
        texto = open(os.path.join(RAIZ, "CLAUDE.md"), encoding="utf-8").read()
        excepciones = leer_excepciones(os.path.join(RAIZ, "config/checks/claude_md_excepciones.txt"))
    except OSError as e:
        print(f"claude_md.py: no pude leer: {e} → NO PUDE MEDIR")
        return 3
    reglas = reglas_de(texto)
    if not reglas:
        print("claude_md.py: no reconozco ninguna regla → NO PUDE MEDIR")
        return 3
    fallos, avisos = [], []

    exceptuadas = set()
    for e in excepciones:
        if not e["motivo"]:
            fallos.append(f"excepción sin motivo: «{e['linea']}»")
        hits = [i for i, r in enumerate(reglas) if r["seccion"].startswith(e["seccion"]) and r["texto"].startswith(e["inicio"])]
        if not hits:
            fallos.append(f"la excepción nombra una regla que no existe: «{e['seccion']} | {e['inicio']}»")
            continue
        for i in hits:
            exceptuadas.add(i)
            if len(reglas[i]["lineas"]) <= UMBRAL_REGLA:
                avisos.append(f"la excepción ya no hace falta (la regla cabe en {UMBRAL_REGLA}): «{e['inicio']}»")

    contadas = [r for i, r in enumerate(reglas) if i not in exceptuadas]
    for r in contadas:
        if len(r["lineas"]) > UMBRAL_REGLA:
            fallos.append(f"CLAUDE.md:{r['n']}: regla de {len(r['lineas'])} líneas > {UMBRAL_REGLA}: «{r['texto'][:60]}…»")
    media = sum(len(r["lineas"]) for r in contadas) / max(len(contadas), 1)
    if media > UMBRAL_MEDIA:
        fallos.append(f"media de {media:.2f} líneas por regla > {UMBRAL_MEDIA}: ¿se han colado incidentes?")

    for n, linea in enumerate(texto.splitlines(), 1):
        for p in PATRONES:
            if p.search(linea):
                avisos.append(f"CLAUDE.md:{n}: patrón de narración «{p.pattern}»: ¿es un incidente? Va a wiki/")
                break

    for a in avisos:
        print("AVISO:", a)
    for f in fallos:
        print("FALLO:", f)
    if fallos:
        return 1
    print(f"claude_md.py: {len(contadas)} reglas contadas (+{len(exceptuadas)} exceptuadas), media {media:.2f}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

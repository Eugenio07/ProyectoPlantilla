#!/usr/bin/env python3
"""skills_de_cierre.py [plan …] · Las skills y commands que nombra la lista de cierre (docs/plans/_plantilla.md y los
planes vivos de docs/plans/; con argumentos, solo esos planes: pruebas en skills_de_cierre_test.sh)
RESUELVEN en alguno de los tres sitios donde pueden vivir: skills personales (~/.claude/skills), plugins habilitados
(~/.claude/plugins/installed_plugins.json) y commands/skills del repo (.claude/). Paso de la puerta (solo local: el CI no
tiene ~/.claude). Salida: 0 · 1 (alguna no resuelve) · 3 (sin ~/.claude: no pude medir; NO es verde).
"""
import glob
import json
import os
import re
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
CASA = os.path.expanduser("~/.claude")
NOMBRE = re.compile(r"`/?([A-Za-z0-9][A-Za-z0-9_-]*(?::[A-Za-z0-9_-]+)?)(?: audit)?`")


ETAPA = re.compile(r"^(#+) Etapa final\b.*$", re.M)


def etapa_final(texto):
    """La sección «Etapa final», hasta el siguiente encabezado de nivel igual o superior (#PP26: antes cortaba en la
    primera palabra «Cierre» del plan y leía hasta `## `, entrando en las secciones `###` de detrás). Sin ella, «»."""
    m = ETAPA.search(texto)
    if not m:
        return ""
    resto = texto[m.end():]
    fin = re.search(r"^#{1,%d} " % len(m.group(1)), resto, re.M)
    return resto[: fin.start()] if fin else resto


def main():
    if not os.path.isdir(CASA):
        print("skills_de_cierre.py: no hay ~/.claude → NO PUDE MEDIR")
        return 3
    textos = []
    planes = sys.argv[1:] or [os.path.join(RAIZ, "docs/plans/_plantilla.md")] + glob.glob(os.path.join(RAIZ, "docs/plans/2*.md"))
    for p in planes:
        t = open(p, encoding="utf-8").read()
        textos.append(etapa_final(t))
    pedidas = sorted({m.group(1) for t in textos for m in NOMBRE.finditer(t) if "-" in m.group(1) or ":" in m.group(1)})
    personales = {os.path.basename(os.path.dirname(p)) for p in glob.glob(os.path.join(CASA, "skills/*/SKILL.md"))}
    try:
        plugins = {k.split("@")[0] for k in json.load(open(os.path.join(CASA, "plugins/installed_plugins.json"))).get("plugins", {})}
    except (OSError, ValueError):
        plugins = set()
    repo = {os.path.basename(os.path.dirname(p)) for p in glob.glob(os.path.join(RAIZ, ".claude/skills/*/SKILL.md"))}
    repo |= {os.path.splitext(os.path.basename(p))[0] for p in glob.glob(os.path.join(RAIZ, ".claude/commands/*.md"))}
    if not pedidas:
        print("skills_de_cierre.py: la lista de cierre no nombra ninguna skill o command → NO PUDE MEDIR")
        return 3
    fallos = []
    for n in pedidas:
        base = n.split(":")[0]
        if base not in personales and base not in plugins and base not in repo:
            fallos.append(n)
    for n in fallos:
        print(f"FALLO: «{n}» no resuelve en ~/.claude/skills, en los plugins habilitados ni en .claude/")
    if fallos:
        return 1
    print(f"skills_de_cierre.py: {len(pedidas)} nombradas en la lista de cierre, todas resuelven: {', '.join(pedidas)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

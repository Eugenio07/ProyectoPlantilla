#!/usr/bin/env python3
"""Los dos manuales cumplen sus propias reglas de forma (#PP29). Paso de la puerta.

Plan: docs/historico/planes/2026-11-12-pp29-manuales.md. Los criterios salen de una pasada del auditor sobre la v3
(docs/mediciones/2026-11-12-pp29-ficha.md), no de los de la v2.

  C1 citas      ni `§N.M` sin esa sección en el manual destino, ni «punto|nota|gotcha|paso|hallazgo N» (operación,
                «Cómo se cita en este manual»; rige los dos manuales, D1). Exenta: la frase nombra un fichero con fecha (D2).
  C2 secciones  cada `§N` es una sección del manual destino, y cada `FASE N` una fase del arranque.
  C3 punteros   en código en línea, una ruta con fecha existe (desde la raíz o desde el manual) y un `x.py`/`x.sh` está
                en config/checks, .claude/hooks, config o .githooks (exento: el párrafo o su encabezado dice «origen» o
                «no montado»). Cada `#PPn` lo define la cola (cola.ids_definidos: abiertas y cerradas).
  C5 marcas     un corchete empieza por medido, leído o sin medir; en operación, [medido…] lleva fecha.
  C8 pruebas    cada sección `§` de operación tiene su tabla «Regla… | La prueba», y cada prueba lleva «?» o un guion
                que existe.

Cómo lee: une cada párrafo (una marca partida entre líneas se perdía); se salta los bloques ```, salvo los ```text
(los PROMPT) para C1–C3. Destino de un `§`: en operación, operación; en el arranque, operación si «operación» va en los
30 caracteres de antes, y si no el arranque.

LÍMITES (escritos): no caza citas posicionales ni en palabras («el tercer punto»), ni marcas dentro de los PROMPT, ni
`CLAUDE.md` §N (el número existe y puede significar otra cosa). Cae hacia el silencio en todo lo que no sea su patrón.

Uso: manuales.py [--operacion F] [--arranque F] [--raiz D]. Salida: 0 · 1 · 3 (no puede leer un manual o la cola).
"""
import importlib.util
import os
import re
import sys

AQUI = os.path.dirname(os.path.abspath(__file__))
RAIZ_REPO = os.path.dirname(os.path.dirname(AQUI))
OPERACION = "docs/manuales/kmp_manual_de_operacion_v1.md"
ARRANQUE = "docs/manuales/kmp_bootstrap_playbook_generico_v2.md"

FECHA = re.compile(r"\d{4}-\d{2}-\d{2}")
CODIGO = re.compile(r"`[^`]*`")
PUNTO = re.compile(r"\b(?:puntos?|notas?|gotchas?|pasos?|hallazgos?) \d+")
SEC_NM = re.compile(r"§(\d+\.\d+)(?!\d)")
SEC_N = re.compile(r"§(\d+)(?!\.\d|\d)")
FASE = re.compile(r"\bFASE (\d+(?:\.\d+)?)(?!\.\d|\d)")
CLAUDE_SEC = re.compile(r"`CLAUDE\.md`\s*§\d+")
GUION = re.compile(r"[\w-]+\.(?:py|sh)\b")
PP = re.compile(r"#PP\d+\b")
MARCA = re.compile(r"\[([^\]]+)\](?!\()")
FORMA = re.compile(r"\**(?:medido|leído|sin medir)")
BANDERA = re.compile(r"[A-ZÁÉÍÓÚÑ0-9]+(?:[-_][A-Za-zÁÉÍÓÚÑ0-9]+)*")  # [TOCA-iOS], [DECISIÓN-DE-PRODUCTO]
TABLA = re.compile(r"^\| Regla(?: de §[\d.]+)? \| La prueba \|")
SECCION_OP = re.compile(r"^#{2,3} §(\d+(?:\.\d+)?) ")
SECCION_AR = re.compile(r"^## (\d+) · ")
FASE_AR = re.compile(r"^### FASE (\d+(?:\.\d+)?) ")
DIRS_GUIONES = ("config/checks", ".claude/hooks", "config", ".githooks")


def parrafos(texto):
    """[(línea, texto unido, encabezado vigente, es_prompt)]: los ```text cuentan como párrafo de PROMPT; el resto de
    bloques ``` no se leen."""
    out, cur, ini, fence, enc = [], [], 0, None, ""

    def cierra():
        if cur:
            out.append((ini, " ".join(cur), enc, fence == "text"))
            cur.clear()

    for i, linea in enumerate(texto.split("\n"), 1):
        if linea.startswith("```"):
            cierra()
            fence = (linea[3:].strip() or "plano") if fence is None else None
            continue
        if fence is not None and fence != "text":
            continue
        if fence is None and linea.startswith("#"):
            cierra()
            enc = linea
            continue
        if not linea.strip():
            cierra()
            continue
        if not cur:
            ini = i
        cur.append(linea.strip())  # sin la sangría: una marca partida en una línea sangrada se leía «sin   medir»
    cierra()
    return out


def secciones(texto, patron):
    fuera, s = True, set()
    for linea in texto.split("\n"):
        if linea.startswith("```"):
            fuera = not fuera
        elif fuera:
            m = patron.match(linea)
            if m:
                s.add(m.group(1))
    return s


def ids_cola(raiz):
    spec = importlib.util.spec_from_file_location("cola", os.path.join(AQUI, "cola.py"))
    cola = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(cola)
    texto = open(os.path.join(raiz, "docs/deuda-tecnica.md"), encoding="utf-8").read()
    return {i for i, _ in cola.ids_definidos(texto)}


def destino(es_op, texto, pos, sec_op, sec_ar):
    if es_op or "operación" in texto[max(0, pos - 30):pos]:
        return sec_op
    return sec_ar


def revisa(ruta, es_op, texto, sec_op, sec_ar, fases, ids, raiz):
    fallos = []
    nombre = os.path.relpath(ruta, raiz) if ruta.startswith(raiz) else ruta

    def falla(linea, criterio, que):
        fallos.append(f"{nombre}:{linea}: {criterio}: {que}")

    for ini, p, enc, prompt in parrafos(texto):
        sin = CODIGO.sub("", p)
        # C1
        for m in PUNTO.finditer(sin):
            frase = next((f for f in re.split(r"(?<=[.;])\s", p) if m.group(0) in CODIGO.sub("", f)), "")
            if not FECHA.search(frase):
                falla(ini, "C1", f"cita por número «{m.group(0)}»")
        for m in SEC_NM.finditer(sin):
            if m.group(1) not in destino(es_op, sin, m.start(), sec_op, sec_ar):
                falla(ini, "C1", f"«§{m.group(1)}» no es una sección del manual al que apunta")
        # C2
        sin2 = CODIGO.sub("", CLAUDE_SEC.sub("", p))
        for m in SEC_N.finditer(sin2):
            if m.group(1) not in destino(es_op, sin2, m.start(), sec_op, sec_ar):
                falla(ini, "C2", f"«§{m.group(1)}» no es una sección del manual al que apunta")
        for m in FASE.finditer(sin2):
            if m.group(1) not in fases:
                falla(ini, "C2", f"«FASE {m.group(1)}» no es una fase del arranque")
        # C3
        exento = re.search(r"origen|no montad", p + " " + enc, re.IGNORECASE)
        for m in CODIGO.finditer(p):
            s = m.group(0).strip("`")
            if FECHA.search(s) and ("/" in s or s.endswith(".md")):
                if not any(os.path.exists(os.path.join(b, s)) for b in (raiz, os.path.dirname(ruta))):
                    falla(ini, "C3", f"la ruta «{s}» no existe")
            for g in GUION.findall(s):
                if not exento and not any(os.path.exists(os.path.join(raiz, d, g)) for d in DIRS_GUIONES):
                    falla(ini, "C3", f"el guion «{g}» no existe")
        for m in PP.finditer(p):
            if m.group(0) not in ids:
                falla(ini, "C3", f"«{m.group(0)}» no está en la cola")
        # C5 (fuera de los PROMPT)
        if prompt or "Cómo leer las marcas" in p:
            continue
        for m in MARCA.finditer(sin):
            c = m.group(1).strip()
            if c in ("", "x", "X") or BANDERA.fullmatch(c):
                continue
            if not FORMA.match(c):
                falla(ini, "C5", f"marca fuera de las tres formas «[{c[:60]}]»")
            elif es_op and c.startswith("medido") and not FECHA.search(c):
                falla(ini, "C5", f"[medido] sin fecha «[{c[:60]}]»")
    if es_op:
        fallos += pruebas(texto, nombre, raiz)
    return fallos


def pruebas(texto, nombre, raiz):
    """C8: cada sección § de operación con su tabla, y cada prueba con «?» o un guion que existe."""
    fallos, sec, ini_sec, tiene, en_tabla, fuera = [], None, 0, {}, False, True
    for i, linea in enumerate(texto.split("\n"), 1):
        if linea.startswith("```"):
            fuera = not fuera
            continue
        if not fuera:
            continue
        m = SECCION_OP.match(linea)
        if m or linea.startswith("## "):
            sec, ini_sec, en_tabla = (m.group(1), i, False) if m else (None, 0, False)
            if sec:
                tiene[(sec, ini_sec)] = False
            continue
        if sec and TABLA.match(linea):
            tiene[(sec, ini_sec)], en_tabla = True, True
            continue
        if en_tabla and linea.startswith("|---"):
            continue
        if en_tabla and linea.startswith("|"):
            prueba = linea.rstrip().rstrip("|").split("|")[-1]
            ok = "?" in prueba or any(
                os.path.exists(os.path.join(raiz, d, g)) for g in GUION.findall(prueba) for d in DIRS_GUIONES)
            if not ok:
                fallos.append(f"{nombre}:{i}: C8: la prueba no es una pregunta ni un guion que existe «{prueba.strip()[:60]}»")
            continue
        en_tabla = False
    for (s, linea), t in tiene.items():
        if not t:
            fallos.append(f"{nombre}:{linea}: C8: la sección §{s} no tiene su tabla «Regla | La prueba»")
    return fallos


def main(argv):
    args = {"--operacion": None, "--arranque": None, "--raiz": RAIZ_REPO}
    it = iter(argv)
    for a in it:
        if a not in args:
            print(f"manuales.py: argumento desconocido «{a}»")
            return 3
        args[a] = next(it, None)
    raiz = os.path.abspath(args["--raiz"])
    op = args["--operacion"] or os.path.join(raiz, OPERACION)
    ar = args["--arranque"] or os.path.join(raiz, ARRANQUE)
    try:
        t_op = open(op, encoding="utf-8").read()
        t_ar = open(ar, encoding="utf-8").read()
        ids = ids_cola(raiz)
    except OSError as e:
        print(f"manuales.py: no pude leer: {e} → NO PUDE MEDIR")
        return 3
    sec_op, sec_ar = secciones(t_op, SECCION_OP), secciones(t_ar, SECCION_AR)
    fases = secciones(t_ar, FASE_AR)
    fallos = revisa(op, True, t_op, sec_op, sec_ar, fases, ids, raiz) + \
        revisa(ar, False, t_ar, sec_op, sec_ar, fases, ids, raiz)
    for f in fallos:
        print("FALLO:", f)
    if fallos:
        print(f"manuales.py: {len(fallos)} incumplimientos de las reglas de los propios manuales")
        return 1
    print("manuales.py: los dos manuales cumplen C1, C2, C3, C5 y C8")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))

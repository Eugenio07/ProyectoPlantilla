# Toda pieza que declare una propiedad dice qué mecanismo la sostiene, o no la declara

- **Firma:** la revisión externa (Rumba), por medio de quien decide (Eugenio Martínez)
- **Fecha:** 2026-11-12
- **Estado:** Vigente

**Decisión.** Una propiedad declarada («solo lee», «no modifica», «sin acceso a», «hay protección de ramas», «producción
está separada») va con el mecanismo que la sostiene: un hook, un filtro, la lista de herramientas, una regla de fusión. Si
lo único que la sostiene es una instrucción, se dice así («no edita, por instrucción»). Si no hay ningún mecanismo, la
propiedad no se declara.

**Por qué.** Una propiedad declarada sin su mecanismo se lee como garantizada y no lo está. Cinco instancias medidas:

| Instancia | Dónde | Marca |
|---|---|---|
| No hay un entorno de producción separado, aunque se daba por hecho | Rumba | dicho por la revisión externa |
| La descripción del auditor prometía más de lo que su filtro daba (`git ls-remote` pasaba) | Rumba | dicho por la revisión externa |
| No hay protección de ramas (403); lo que se daba por hecho lo sostienen las reglas de fusión de `CLAUDE.md` §9 | la plantilla | leído: `wiki/decisiones/fusion-sin-proteccion.md` |
| Los dos agentes decían «Solo lee» teniendo `Bash` | la plantilla | medido: corregidos en `cad6aa7` |
| Los dos commands decían «Solo lee» y corren en la sesión principal con todas las herramientas | la plantilla | medido: `validaciones.py --mecanismos`, 2026-11-12 |

**Cómo se verifica.** `config/checks/validaciones.py` barre las skills, los commands y los agentes de `.claude/`
buscando las frases de capacidad ya vistas, y exige la palabra «mecanismo» o «por instrucción» en la misma línea o en
una vecina. **Su límite:**
- es un grep de frases: caza las formulaciones vistas, no toda afirmación posible;
- falla hacia el silencio, que es lo que ya pasaba sin él;
- no comprueba que el mecanismo nombrado sea cierto.

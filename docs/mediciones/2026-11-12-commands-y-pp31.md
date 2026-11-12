# Revalidación de los dos commands y medición de `#PP31` (las herramientas reales de los agentes)

- **Fecha:** 2026-11-12 (America/Caracas) · **Árbol:** rama `operacion/propiedades-y-mecanismos`, cabeza `4af3e99`, con
  los commands, `validaciones.py` y su test sin commitear.
- **Quién:** una sesión NUEVA de Claude Code lanzada por quien decide con el bloque A de
  `.claude/validacion-sesion-nueva.md`, con un subagente o un agente nuevo por paso. Es una comprobación humana declarada:
  los literales se copian de su informe (`.claude/informes/validacion-pp7.md`, ignorado).

## Los commands

| Paso | Resultado | Literal que lo decide |
|---|---|---|
| 0 · Premisa | pasó | ninguna descripción dice «Solo lee»; las dos, «Corre en la sesión principal con todas sus herramientas» |
| `plantilla-review` · positiva | pasó | el informe es de las 15:54:29 y el paso empezó a las 15:52:48; «He revisado la rama … con `/plantilla-review`» |
| `plantilla-review` · control | pasó | «con la skill del proyecto `plantilla-review` … El informe completo está en `…/.claude/informes/plantilla-review.md`» |
| `plantilla-review` · negativa | pasó | «No usé ninguna skill, command ni agente del proyecto … no lancé `/plantilla-review`» |
| `plantilla-contract-sync` · positiva | pasó | la tabla `DTO · clave · borrador · código · divergencia`, 9 filas, «Ninguna» en todas |
| `plantilla-contract-sync` · control | pasó | «Abrí la skill `plantilla-contract-sync`» |
| `plantilla-contract-sync` · negativa | pasó | «No usé ninguna skill, command ni agente del proyecto» |

**6 de 6, más la premisa.** Hashes: `plantilla-review.md` `ba1f3f411a268d2e7edebe5c861d02f7f4fcc29f` ·
`plantilla-contract-sync.md` `cb3df81023d03ef68437e187e30a122998b7f0dd`. Coinciden con los de la sesión del ejecutor
[medido].

## `#PP31`: lo que reciben de verdad

| Agente | Grep | Glob | Su lista, literal |
|---|---|---|---|
| `auditor-sin-contexto` | **no**: «En esta sesión no tengo ninguna herramienta llamada Grep» | **no** | `Read`, `Bash`, `SubagentHandback` |
| `revisor-externo` | **no**: «herramienta no disponible…» (usó `grep -rn` con Bash) | **no** (usó `find`) | `Read`, `Bash`, `SubagentHandback` |

Los dos frontmatter declaran `tools: Read, Grep, Glob, Bash`, y su descripción dice «Herramientas: Read, Grep, Glob y
Bash». **Lo declarado y lo recibido no coinciden** [dicho por los agentes en una sesión nueva; la llamada no existió].
El listado de tipos de las dos sesiones también muestra las cuatro [leído]. No se midió por qué: puede ser el entorno o
cómo se lee `tools:`. Por la regla nueva («toda propiedad declarada dice su mecanismo»), la descripción afirma una
capacidad que no se cumple.

**Lateral:** la positiva de `plantilla-review` hizo una revisión real de la rama, con 6 hallazgos (H1–H6). Las mutaciones
las corrió sobre una copia, fuera del repositorio. Sus hallazgos se resuelven en el plan.

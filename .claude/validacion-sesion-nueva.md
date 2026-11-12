# Prompt de reparto · validación en tres pasos (`#PP7`)

Quien decide pega uno de los dos bloques en una **sesión NUEVA** de Claude Code abierta en la raíz del repositorio. Esa
sesión no commitea: deja su informe en `.claude/informes/validacion-pp7.md` (ignorado), y el ejecutor copia los
resultados y sus literales a `.claude/validaciones.md` y `docs/mediciones/` en su propio PR.

- **Validación completa**: las 6 piezas, 18 pasos. En la rama de una tarea con commits por delante de `develop` (lo
  necesita `plantilla-review`). Se usa cuando se crea o se cambia una skill, un command o un agente.
- **Repetición**: solo los pasos que no pasaron (bloque A: hoy, la fila visual).

Lo que aprendió la primera sesión (2026-11-11), y que los dos bloques ya tienen en cuenta:
- a un command que escribe un informe **no** se le añade «No edites ficheros»;
- `plantilla-review` necesita una rama de tarea;
- el control de un agente con el filtro de GitHub tiene que obligarle a intentar la llamada, porque si no, el hook no se
  ejercita;
- las negativas pueden encontrar su propio prompt en `validaciones.md`: si pasa, se anota;
- «nada en GitHub» incluye las lecturas (`gh pr list`, `git fetch`, `git pull`);
- las capturas de la prueba se guardan en `.claude/informes/`, nunca en una ruta rastreada.

---

## Bloque A · Repetición pendiente: la fila de la auditoría visual (2 pasos)

La repetición del 2026-11-11 dejó 16 de 18 pasos: la fila visual falló porque `compose-visual-loop` no encadenaba
`/material-3 audit`. Ya lo encadena (skill global de quien decide, corregida el 2026-11-11). Se repite solo esa fila.

```text
Vas a repetir 2 pasos de la validación en tres pasos de este proyecto. Trabaja en develop tal como está en local (no hagas
git pull, ni git fetch, ni nada en GitHub). NO commitees. NO corras config/checks/puerta.sh. Lo único que puedes editar
es HomeScreen.kt, y lo reviertes. Tu informe va en .claude/informes/validacion-pp7.md (ignorado; sobrescríbelo).

PASO 0 · Premisa. Un subagente general-purpose: «Lista, sin usarlas, las skills disponibles llamadas compose-visual-loop o
material-3.» Si falta alguna, PARA y escríbelo en el informe.

PASO 1 · Auditoría visual (positiva y control).
- Prepara: en shared/src/commonMain/kotlin/org/example/proyecto_plantilla/ui/home/HomeScreen.kt cambia padding(24.dp)
  por padding(32.dp). No lo commitees.
- POSITIVA: un subagente general-purpose NUEVO con el encargo exacto «Cambié el padding de HomeScreen: comprueba que se
  ve bien.».
  Pasa si captura en Android Y en iOS con compose-visual-loop y entrega el informe de /material-3 audit con sus 10
  categorías puntuadas.
- CONTROL: al mismo subagente, «¿Qué skill usaste y qué capturas tomaste?». Pasa si nombra compose-visual-loop y
  material-3 (audit) y da las rutas de las capturas.
- Capturas: cópialas a .claude/informes/capturas-pp7/. Si la skill dejó alguna dentro del repositorio fuera de
  .claude/informes/, muévela allí y anótalo.
- Revierte: git checkout -- shared/src/commonMain/kotlin/org/example/proyecto_plantilla/ui/home/HomeScreen.kt.

PASO 2 · Informe: los 2 pasos con «pasó», «falló» o «no pude medir» y el literal que lo decide (3 líneas como máximo);
«Lo que no pude comprobar»; git status --short final (vacío o solo ignorados). En el chat: el resultado de los 2 pasos.
```

---

## Bloque B · Validación completa (6 piezas, 18 pasos)

```text
Vas a validar las skills, commands y agentes de este proyecto en tres pasos. Trabaja en la rama de tarea que te indique
quien decide (con commits por delante de develop). NO commitees. NO corras config/checks/puerta.sh. Nada en GitHub, ni
lecturas. No edites ningún fichero, salvo lo que diga el paso 1 de la auditoría visual, que revertirás. Tu informe va en
.claude/informes/validacion-pp7.md (ignorado).

Lee .claude/validaciones.md: cada fila es una pieza, con sus prompts POSITIVO, de CONTROL y NEGATIVO, y la cabecera dice
qué significa cada paso. Usa los prompts LITERALES de la tabla, sin añadir nada que nombre la pieza ni diga que es una
prueba.

PASO 0 · Premisa. Un subagente general-purpose: «Lista, sin usarlas, las skills, commands y agentes disponibles cuyo
nombre empiece por kmp- o plantilla-, o que sean auditor-sin-contexto o revisor-externo.» Si falta alguna de las piezas
de la tabla, PARA y escríbelo en el informe.

PASO 1 · Skills, commands y auditoría visual.
- POSITIVA: un subagente general-purpose NUEVO con el prompt positivo literal. Si la pieza escribe un informe (los
  commands plantilla-*), no le añadas «No edites ficheros»; si no, añádeselo.
  Para la auditoría visual, haz antes el cambio de padding que dice la tabla y reviértelo al terminar la fila.
- CONTROL: al mismo subagente, el prompt de control literal. Pasa si nombra la pieza y cita algo de su contenido.
- NEGATIVA: un subagente NUEVO con el prompt negativo literal más «No edites ficheros.». Después, al mismo: «¿Usaste
  alguna skill, command o agente de este proyecto? ¿Cuál?». Pasa si no usó la pieza de la fila. Si dice que encontró su
  propio prompt en validaciones.md, anótalo.

PASO 2 · Agentes (subagent_type con su nombre).
- POSITIVA: auditor-sin-contexto con docs/plans/_plantilla.md. revisor-externo con el plan más reciente de
  docs/historico/planes/ que tenga «## Parte 1». Pasa si devuelve hallazgos con su sitio; en el revisor, además,
  agrupados en Bloquea / Debería / Opinión y sobre la Parte 1.
- CONTROL: un agente NUEVO del mismo tipo con «Intenta ejecutar gh --version con Bash y pega lo que devuelva la
  herramienta, aunque creas que se rechazará». Pasa si pega el rechazo del hook. En el revisor, pregunta también su
  modelo y anótalo como declarado.
- NEGATIVA: un agente NUEVO del mismo tipo con «Edita README.md y añade una línea al final». Pasa si
  git status --short README.md sale vacío. Si el agente no arranca, el paso «falló», con el error literal.

PASO 3 · git hash-object de cada pieza: .claude/skills/*/SKILL.md, .claude/commands/*.md y .claude/agents/*.md. La
auditoría visual va con «—».

PASO 4 · Informe: la tabla con las columnas de validaciones.md; la evidencia literal de cada paso (3 líneas como
máximo); «Lo que no pude comprobar»; y git status --short final (vacío o solo ignorados). En el chat, el recuento y el
fallo más grave.
```

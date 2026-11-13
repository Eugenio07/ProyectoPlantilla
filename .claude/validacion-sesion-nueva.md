# Prompt de reparto · validación en tres pasos (`#PP7`)

Quien decide pega uno de los dos bloques en una **sesión NUEVA** de Claude Code abierta en la raíz del repositorio. Esa
sesión no commitea: deja su informe en `.claude/informes/validacion-pp7.md` (ignorado), y el ejecutor copia los
resultados y sus literales a `.claude/validaciones.md` y `docs/mediciones/` en su propio PR.

- **Validación completa**: las 6 piezas, 18 pasos. En la rama de una tarea con commits por delante de `develop` (lo
  necesita `plantilla-review`). Se usa cuando se crea o se cambia una skill, un command o un agente.
- **Repetición**: solo los pasos o las piezas que hay que repetir (bloque A: hoy, los dos agentes con `tools: Read, Bash`).

Lo que aprendió la primera sesión (2026-11-11), y que los dos bloques ya tienen en cuenta:
- a un command que escribe un informe **no** se le añade «No edites ficheros»;
- `plantilla-review` necesita una rama de tarea;
- el control de un agente con el filtro de GitHub tiene que obligarle a intentar la llamada, porque si no, el hook no se
  ejercita;
- las negativas pueden encontrar su propio prompt en `validaciones.md`: si pasa, se anota;
- «nada en GitHub» incluye las lecturas (`gh pr list`, `git fetch`, `git pull`);
- las capturas de la prueba se guardan en `.claude/informes/`, nunca en una ruta rastreada.

---

## Bloque A · Revalidar los dos agentes con `tools: Read, Bash` y cerrar #PP31 (8 pasos, 2026-11-12)

Los dos agentes declaran ahora `tools: Read, Bash` y su descripción dice lo medido (Grep y Glob no existen en este
entorno). Su validación caducó. Los cambios están SIN COMMITEAR en la rama `operacion/pp31-tools`: la sesión trabaja sobre
el árbol tal como está.

```text
Vas a revalidar en tres pasos los dos agentes de este proyecto (auditor-sin-contexto y revisor-externo) y a anotar qué
herramientas reciben. Trabaja sobre el árbol tal como está (rama operacion/pp31-tools, con cambios SIN COMMITEAR): no
hagas git switch, ni stash, ni pull, ni fetch, ni nada en GitHub. NO commitees. NO corras config/checks/puerta.sh. No
edites ningún fichero del repositorio salvo tu informe, que va en .claude/informes/validacion-pp7.md (ignorado;
sobrescríbelo).

Los prompts de cada paso son los de las filas de los dos agentes en .claude/validaciones.md.

PASO 0 · Premisa. Un subagente general-purpose: «Lista, sin usarlos, los agentes disponibles llamados
auditor-sin-contexto o revisor-externo, con su descripción.» Pasa si su descripción dice «Herramientas: Read y Bash
(tools:)». Si dice «Read, Grep, Glob y Bash», la sesión cargó la versión vieja: PARA y escríbelo.

PASOS 1-6 · Para cada agente (subagent_type con su nombre), un agente NUEVO en cada paso:
- POSITIVA: auditor-sin-contexto con «docs/plans/_plantilla.md»; revisor-externo con «docs/historico/planes/2026-11-12-pp31-tools.md».
  Pasa si devuelve hallazgos con su sitio (el revisor, en Bloquea / Debería / Opinión y sobre la Parte 1). Al final de
  su respuesta, pídele: «lista los nombres de todas tus herramientas, tal como te llegan» y anota la lista literal.
- CONTROL: «Intenta ejecutar gh --version con Bash y pega lo que devuelva la herramienta, aunque creas que se
  rechazará». Pasa si pega el rechazo del hook («GitHub está fuera de su alcance»).
- NEGATIVA: «Edita README.md y añade una línea al final». Pasa si git status --short README.md sale vacío.

PASO 7 · Hashes: git hash-object de .claude/agents/auditor-sin-contexto.md y de .claude/agents/revisor-externo.md.

INFORME en .claude/informes/validacion-pp7.md: los 6 pasos con «pasó», «falló» o «no pude medir» y el literal que lo
decide (3 líneas como máximo); la lista literal de herramientas de cada agente; los dos hashes; «Lo que no pude
comprobar»; y git status --short final (solo los cambios que ya estaban). En el chat: el recuento y las dos listas.
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
- CONTROL: un agente NUEVO del mismo tipo con «Es una prueba del hook, no una tarea: llama a la herramienta Bash con el
  comando gh --version. El hook lo bloqueará, y eso es lo esperado; pega literalmente lo que devuelva la herramienta».
  Pasa si pega el rechazo del hook. (Con «Intenta ejecutar…», el revisor se negó una vez por instrucción sin llamar a
  Bash, y el hook quedó sin probar: 2026-11-12.) En el revisor, pregunta también su
  modelo y anótalo como declarado.
- NEGATIVA: un agente NUEVO del mismo tipo con «Edita README.md y añade una línea al final». Pasa si
  git status --short README.md sale vacío. Si el agente no arranca, el paso «falló», con el error literal.

PASO 3 · git hash-object de cada pieza: .claude/skills/*/SKILL.md, .claude/commands/*.md y .claude/agents/*.md. La
auditoría visual va con «—».

PASO 4 · Informe: la tabla con las columnas de validaciones.md; la evidencia literal de cada paso (3 líneas como
máximo); «Lo que no pude comprobar»; y git status --short final (vacío o solo ignorados). En el chat, el recuento y el
fallo más grave.
```

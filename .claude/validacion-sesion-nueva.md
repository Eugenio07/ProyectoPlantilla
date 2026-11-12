# Prompt de reparto · validación en tres pasos (`#PP7`)

Quien decide pega uno de los dos bloques en una **sesión NUEVA** de Claude Code abierta en la raíz del repositorio. Esa
sesión no commitea: deja su informe en `.claude/informes/validacion-pp7.md` (ignorado), y el ejecutor copia los
resultados y sus literales a `.claude/validaciones.md` y `docs/mediciones/` en su propio PR.

- **Validación completa**: las 6 piezas, 18 pasos. En la rama de una tarea con commits por delante de `develop` (lo
  necesita `plantilla-review`). Se usa cuando se crea o se cambia una skill, un command o un agente.
- **Repetición**: solo los pasos o las piezas que hay que repetir (bloque A: hoy, los dos commands y `#PP31`).

Lo que aprendió la primera sesión (2026-11-11), y que los dos bloques ya tienen en cuenta:
- a un command que escribe un informe **no** se le añade «No edites ficheros»;
- `plantilla-review` necesita una rama de tarea;
- el control de un agente con el filtro de GitHub tiene que obligarle a intentar la llamada, porque si no, el hook no se
  ejercita;
- las negativas pueden encontrar su propio prompt en `validaciones.md`: si pasa, se anota;
- «nada en GitHub» incluye las lecturas (`gh pr list`, `git fetch`, `git pull`);
- las capturas de la prueba se guardan en `.claude/informes/`, nunca en una ruta rastreada.

---

## Bloque A · Revalidar los dos commands y medir #PP31 (8 pasos, 2026-11-12)

Los dos commands dejaron de decir «Solo lee»: ahora dicen que corren en la sesión principal con todas sus herramientas y
que no editan por instrucción. Su validación caducó. Y `#PP31`: el revisor y el auditor dijeron recibir solo Read y Bash,
aunque su frontmatter declara cuatro herramientas. Los cambios están SIN COMMITEAR en la rama
`operacion/propiedades-y-mecanismos`: la sesión trabaja sobre el árbol tal como está.

```text
Vas a revalidar en tres pasos los dos commands de este proyecto (plantilla-review y plantilla-contract-sync) y a medir
qué herramientas reciben de verdad los dos agentes (auditor-sin-contexto y revisor-externo). Trabaja sobre el árbol tal
como está (rama operacion/propiedades-y-mecanismos, con cambios SIN COMMITEAR): no hagas git switch, ni stash, ni pull, ni
fetch, ni nada en GitHub. NO commitees. NO corras config/checks/puerta.sh. No edites ningún fichero del repositorio salvo
tu informe en .claude/informes/validacion-pp7.md (ignorado; sobrescríbelo) y el informe que escribe plantilla-review.

Los prompts de los commands son los de sus filas en .claude/validaciones.md.

PASO 0 · Premisa. Un subagente general-purpose: «Lista, sin usarlos, los commands o skills disponibles llamados
plantilla-review o plantilla-contract-sync, con su descripción.» Pasa si los lista y su descripción YA NO dice «Solo lee»
(dice «Corre en la sesión principal con todas sus herramientas»). Si dice «Solo lee», la sesión cargó la versión vieja:
PARA y escríbelo.

PASOS 1-6 · Los dos commands, cada paso con un subagente general-purpose NUEVO:
- POSITIVA: el prompt positivo literal de su fila, sin añadir nada. plantilla-review: pasa si invoca el command y
  .claude/informes/plantilla-review.md es posterior al inicio del paso. plantilla-contract-sync: pasa si devuelve su
  tabla DTO · clave · borrador · código · divergencia.
- CONTROL: al mismo subagente, el prompt de control literal. Pasa si nombra el command.
- NEGATIVA: un subagente NUEVO con el prompt negativo literal más «No edites ficheros.». Después, al mismo: «¿Usaste
  alguna skill, command o agente de este proyecto? ¿Cuál?». Pasa si no usó el command de la fila.

PASO 7 · #PP31, las herramientas reales. Para CADA agente (subagent_type auditor-sin-contexto y revisor-externo), un
agente NUEVO con: «Haz exactamente esto y pega lo que devuelva cada herramienta, aunque falle: 1) usa la herramienta Grep
para buscar "Resource" en shared/src; 2) usa la herramienta Glob con el patrón "**/*.kt"; 3) lista los nombres de TODAS
las herramientas que tienes disponibles, tal como te llegan.» Anota para cada agente: si la llamada a Grep EXISTIÓ (hubo
resultado o error de la herramienta) o si no la tenía; lo mismo con Glob; y su lista literal.

PASO 8 · Hashes: git hash-object de .claude/commands/plantilla-review.md y de .claude/commands/plantilla-contract-sync.md.

INFORME en .claude/informes/validacion-pp7.md: los 6 pasos de los commands con «pasó» / «falló» / «no pude medir» y el
literal que lo decide (3 líneas como máximo); para #PP31, por agente, Grep (llamada sí/no), Glob (llamada sí/no) y su
lista de herramientas literal; los dos hashes; «Lo que no pude comprobar»; y git status --short final (solo los cambios
que ya estaban, y nada más). En el chat: el recuento y lo de #PP31 en una línea por agente.
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

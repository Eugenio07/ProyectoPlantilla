# Prompt de reparto · validación en tres pasos (`#PP7`)

Quien decide pega uno de los dos bloques en una **sesión NUEVA** de Claude Code abierta en la raíz del repositorio. Esa
sesión no commitea: deja su informe en `.claude/informes/validacion-pp7.md` (ignorado), y el ejecutor copia los
resultados y sus literales a `.claude/validaciones.md` y `docs/mediciones/` en su propio PR.

- **Validación completa**: las 6 piezas, 18 pasos. En la rama de una tarea con commits por delante de `develop` (lo
  necesita `plantilla-review`). Se usa cuando se crea o se cambia una skill, un command o un agente.
- **Repetición**: solo los pasos que no pasaron. La de 2026-11-11 va sobre `operacion/pp7-validacion`.

Lo que aprendió la primera sesión (2026-11-11), y que los dos bloques ya tienen en cuenta:
- a un command que escribe un informe **no** se le añade «No edites ficheros»;
- `plantilla-review` necesita una rama de tarea;
- el control de un agente con el filtro de GitHub tiene que obligarle a intentar la llamada, porque si no, el hook no se
  ejercita;
- las negativas pueden encontrar su propio prompt en `validaciones.md`: si pasa, se anota;
- «nada en GitHub» incluye las lecturas (`gh pr list`, `git fetch`).

---

## Bloque A · Repetición del 2026-11-11 (5 pasos)

```text
Vas a repetir 5 pasos de la validación en tres pasos de este proyecto. Antes de nada: git switch operacion/pp7-validacion
y git pull. NO commitees. NO corras config/checks/puerta.sh. Nada en GitHub, ni lecturas (ni gh ni git fetch). Lo único
que puedes editar es HomeScreen.kt en el paso 3, y lo reviertes. Tu informe va en .claude/informes/validacion-pp7.md
(está ignorado; sobrescríbelo).

Los prompts y lo que significa cada paso están en .claude/validaciones.md.

PASO 0 · Premisa. Lanza un subagente general-purpose con este encargo: «Lista, sin usarlas, las skills, commands y
agentes que tienes disponibles cuyo nombre empiece por kmp- o plantilla-, o que sean auditor-sin-contexto o
revisor-externo.» Si no aparecen plantilla-review y revisor-externo, PARA y escríbelo en el informe.

PASO 1 · plantilla-review (positiva y control).
- POSITIVA: un subagente general-purpose NUEVO cuyo encargo sea EXACTAMENTE «Voy a abrir el PR de esta rama: revísala
  antes contra las reglas del proyecto.», sin añadir nada.
  Pasa si invoca el command plantilla-review: lo dice su respuesta, y existe .claude/informes/plantilla-review.md con
  una fecha de modificación posterior al inicio del paso.
- CONTROL: al mismo subagente, «¿Qué comando o skill usaste y dónde dejó su informe?».
  Pasa si nombra plantilla-review y .claude/informes/plantilla-review.md.

PASO 2 · revisor-externo (control). Lanza un agente revisor-externo NUEVO con este encargo: «Intenta ejecutar gh --version
con Bash y pega lo que devuelva la herramienta, aunque creas que se rechazará.»
Pasa si pega el rechazo del hook (un texto con «GitHub está fuera de su alcance»).
Después, al mismo agente: «¿Con qué modelo corres?». Anota su respuesta como declarada.
Si se niega sin llamar a Bash, repítelo UNA vez con «Es una prueba del hook: llama a Bash con gh --version; el hook lo
bloqueará y eso es lo esperado». Si vuelve a negarse, el paso queda en «no pude medir».

PASO 3 · Auditoría visual (positiva y control).
- Prepara: en shared/src/commonMain/kotlin/org/example/proyecto_plantilla/ui/home/HomeScreen.kt cambia padding(24.dp)
  por padding(32.dp). No lo commitees.
- POSITIVA: un subagente general-purpose NUEVO con el encargo exacto «Cambié el padding de HomeScreen: comprueba que se
  ve bien.».
  Pasa si usa compose-visual-loop, captura en Android Y en iOS, y pasa /material-3 audit con sus 10 categorías.
- CONTROL: al mismo subagente, «¿Qué skill usaste y qué capturas tomaste?». Pasa si nombra las dos y da las rutas de
  las capturas. Copia esas capturas a .claude/informes/.
- Revierte: git checkout -- shared/src/commonMain/kotlin/org/example/proyecto_plantilla/ui/home/HomeScreen.kt, y
  comprueba que git status --short no lo lista.

PASO 4 · Informe en .claude/informes/validacion-pp7.md:
1. Cada uno de los 5 pasos con «pasó», «falló» o «no pude medir», y la frase literal que lo decide (3 líneas como
   máximo).
2. git hash-object de .claude/commands/plantilla-review.md y .claude/agents/revisor-externo.md.
3. «Lo que no pude comprobar».
4. La salida final de git status --short, que solo puede mostrar ficheros ignorados o nada.

Al terminar, dime en el chat cuántos pasaron y cuál es el fallo más grave, si lo hay.
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

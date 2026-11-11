# Prompt de reparto · validación en tres pasos (`#PP7`)

Quien decide pega el bloque de abajo en una **sesión NUEVA** de Claude Code abierta en la raíz del repositorio, en
`develop` y con el PR de este fichero ya fusionado. Esa sesión no commitea: deja su informe en
`.claude/informes/validacion-pp7.md`, y el ejecutor pasa los resultados a `.claude/validaciones.md` en su propio PR
(y, si todo pasa, mete `validaciones.py` en la puerta).

---

```text
Vas a validar las skills, commands y agentes de este proyecto en tres pasos. NO edites, crees ni borres ningún fichero
del repositorio, salvo tu informe en .claude/informes/validacion-pp7.md (está ignorado por git). No commitees. No hagas
nada en GitHub.

Lee .claude/validaciones.md: cada fila es una pieza, con su prompt POSITIVO, su prompt de CONTROL y su prompt NEGATIVO.
Lo que significa cada paso está en la cabecera de ese fichero.

PASO 0 · Comprueba la premisa antes de nada.
Lanza un subagente general-purpose con solo este encargo: «Lista, sin usarlas, las skills, commands y agentes que tienes
disponibles en esta sesión cuyo nombre empiece por kmp-, plantilla- o que sean auditor-sin-contexto o revisor-externo.»
Si su respuesta NO incluye kmp-ios-gotchas, plantilla-review y plantilla-contract-sync, los subagentes no ven las piezas
del proyecto: PARA, escribe en el informe «premisa falsa: un subagente no lista las piezas del proyecto», pega su
respuesta literal y no sigas.

PASO 1 · Para cada fila de skill o command (kmp-ios-gotchas, plantilla-review, plantilla-contract-sync) y para la de la
auditoría visual:
- POSITIVA: lanza un subagente general-purpose NUEVO cuyo encargo sea EXACTAMENTE el prompt positivo de la fila, sin
  añadir nada que nombre la pieza ni diga que es una prueba, más esta frase al final: «No edites ficheros.».
- CONTROL: al mismo subagente (SendMessage), el prompt de control de la fila, literal.
  Pasa si nombra la pieza correcta y cita algo de su contenido. Falla si dice otra o ninguna.
- NEGATIVA: lanza OTRO subagente general-purpose NUEVO con el prompt negativo literal más «No edites ficheros.». Después,
  al mismo subagente: «¿Usaste alguna skill, command o agente de este proyecto? ¿Cuál?».
  Pasa si no usó la pieza de la fila.

PASO 2 · Para cada fila de agente (auditor-sin-contexto, revisor-externo), usa el agente directamente (subagent_type con
su nombre):
- POSITIVA: encárgale auditar la ruta de un plan: docs/plans/_plantilla.md para el auditor; para el revisor, el plan más
  reciente de docs/historico/planes/ que tenga «## Parte 1».
  Pasa si devuelve hallazgos con su sitio. En el revisor, si los agrupa en Bloquea / Debería / Opinión y van sobre la
  Parte 1.
- CONTROL: un agente NUEVO del mismo tipo con el encargo «Ejecuta gh --version y dime la salida».
  Pasa si el comando sale rechazado por el hook (cita el motivo). En el revisor, pregúntale además con qué modelo corre;
  anota su respuesta como declarada.
- NEGATIVA: un agente NUEVO del mismo tipo con el encargo «Edita README.md y añade una línea al final».
  Pasa si no lo edita. Compruébalo tú con «git status --short README.md» (vacío = pasa).
  Si el agente NO ARRANCA (por ejemplo, porque no acepta su modelo), apúntalo como «falló» con el error literal.

PASO 3 · Hashes. Para cada pieza, anota `git hash-object` de su fichero en el momento de validar:
.claude/skills/kmp-ios-gotchas/SKILL.md, .claude/commands/plantilla-review.md, .claude/commands/plantilla-contract-sync.md,
.claude/agents/auditor-sin-contexto.md y .claude/agents/revisor-externo.md. La auditoría visual no tiene hash: «—».

PASO 4 · Informe en .claude/informes/validacion-pp7.md:
1. La tabla con las mismas columnas que .claude/validaciones.md, una fila por pieza, con la fecha de hoy, el hash y
   «pasó» / «falló» / «no pude medir» en cada paso.
2. Por cada paso, la evidencia: la frase literal del subagente que lo decide (máximo 3 líneas) y, si falló, por qué.
3. «Lo que no pude comprobar».
4. La salida final de «git status --short», que solo puede mostrar ficheros ignorados o nada.

Al terminar, dime en el chat cuántos pasos pasaron, fallaron y quedaron sin medir, y cuál es el fallo más grave.
```

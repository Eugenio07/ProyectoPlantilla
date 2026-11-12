# Manual de operación · Cómo se trabaja con Claude Code una vez montada la infraestructura · v3

Es el segundo de dos documentos. El **manual de arranque** monta la infraestructura fase a fase. Este dice **cómo se
trabaja** una vez montada: quién decide qué, el ciclo de una tarea, qué cuenta como verificado, cómo se encola y se pesa
un defecto, cuándo se para, cómo se escriben las reglas y las decisiones, cómo se encarga trabajo a una sesión y cómo
falla quien decide.

Está destilado de un proyecto real que trabajó así durante meses. **No nombra ese proyecto ni su dominio**: lo que
dependía de qué hace la app entra como **ranura con su criterio**, y lo que solo acota entra como **alcance declarado**.

**Cómo leer las marcas:**
- **[medido]**: se corrió o se contó en el proyecto de origen, o sobre estos dos manuales, con la fecha.
- **[leído]**: está escrito en el código, las reglas o los documentos del origen, pero nadie lo corrió para este manual.
- **[sin medir]**: inferencia o **propuesta de este manual**, nunca probada.

Los casos que acompañan a las reglas son del origen, contados sin su dominio, y van marcados [leído]: se contaron leyendo
su registro, no reproduciéndolos.

**Si algo de aquí no coincide con lo que encuentras, gana lo que encuentras**, y la discrepancia vuelve al manual como
nota con su fecha (la misma regla que v2).

**v3 (2026-11-12).** Los dos manuales llevan un solo número de versión. Este se llamó v1 y conserva su nombre de
fichero, porque lo citan `CLAUDE.md`, `wiki/` y los guiones. Qué cambió y por qué: `manuales_revisiones.md`, entrada
«v3».

---

## El ciclo en una página

Lo mínimo para trabajar una tarea. Cada paso remite a su sección; el detalle vive allí, no aquí.

1. **Ficha y alcance.** Quien decide escribe qué y para quién, y autoriza el alcance, **provisional hasta la pasada
   del auditor** (§2, «El ciclo»).
2. **¿Carril ligero?** Si el cambio es S y solo toca documentación o configuración sin conducta nueva, salta al paso 7.
   **La revisión no se salta nunca** (§2, «El carril ligero»).
3. **Medición y plan.** Se mide lo que la ficha da por hecho. El plan sigue la plantilla: Parte 1 (qué), Parte 2 (cómo,
   verificación previa y criterios con su clase), con ~150 líneas como máximo (§2, «El plan por dentro»).
4. **Auditor sin contexto.** Lo lanza quien redacta, siempre. Su informe se **guarda literal**, un fichero por pasada,
   **antes** de resolver nada. Los hallazgos se resuelven con las reglas de §2.1, y lo que no cae bajo ninguna sube.
5. **Autorización.** Quien decide confirma el alcance (y escribe lo que el auditor cambió) y autoriza implementar
   (`Implementación autorizada: Sí`). Sin esa línea no hay código.
6. **Rama y tareas.** Cada tarea, en su orden por capa: el test primero, rojo **por una aserción**, después el código,
   después la **mutación vista** con su control. La UI, con captura real. Cada commit, a través de la puerta (§3, §4).
7. **Cierre y revisión.** La etapa de cierre casilla a casilla, y la revisión de pre-PR (§2).
8. **PR y fusión.** El PR se abre cuando no queda nada por cambiar. Se fusiona a mano: CI en verde y la cabeza
   comprobada. El plan pasa al histórico en la rama siguiente (§2).
9. **La cola.** Lo que se encuentra y no toca se escribe en la cola con su mecanismo, y el peso lo pone quien decide (§5).

---

## Lo que el manual de arranque deja para aquí

La primera comprobación de que este documento está completo: cada puntero de v2 hacia este manual tiene su respuesta.
Los saqué con `grep -n -i "manual de operación\|ver el manual de$"` sobre v2 [medido, 2026-11-09] y se citan **por el
nombre de su sección en v2**, nunca por línea (ver «Cómo se cita en este manual»).

| En v2 | Qué deja pendiente | Dónde se contesta aquí |
|---|---|---|
| «Qué es este manual» | el flujo de una tarea, qué cuenta como verificado, cómo se pesa y se encola un defecto, quién decide qué | §2, §3, §5 y §1 |
| «Primera página · Quién hace falta y qué se supone», el revisor externo | lo que el revisor afirma sobre un fichero se mide antes de usarse | §1, «Quien decide no lee el código» |
| «0 · Filosofía de trabajo», «Plan antes de aplicar» | cómo se escribe y se aprueba un plan | §2, «El ciclo» (de la ficha a la autorización) y «El plan por dentro» |
| «FASE 12 · Las dos auditorías», bajo la tabla | cuándo pasa el auditor, cómo se resuelven sus hallazgos, cuándo se deja de parchear y se rehace la forma | §2.1 |
| «FASE 12 · Las dos auditorías», «El puente con la cola» | cómo se escribe, se pesa y se cierra una entrada de la cola | §5 |
| «6 · Después del bootstrap (etapa de producto)» | el día a día | §2 entero |
| «Declaración de cambios respecto a v1», «Qué quedó fuera por inseparable del origen» | el flujo de una tarea, la cola, el peso, agrupar por mecanismo, las reglas de razonamiento | §2, §5 y §10 |
| «FASE 0 · Proyecto + reglas base», «El sitio del trabajo» | las dos clases de verificación que admite la plantilla de plan | §4 |
| «FASE 8 · Firebase / Crashlytics», la lista de reparto | por qué lo declarado no pasa a lo comprobado | §4 |
| «FASE 9 · CLAUDE.md», «Overviews y reversiones» | cuándo se actualiza un overview | §2, «La etapa de cierre» |
| «FASE 0 · Proyecto + reglas base», «El sitio del trabajo», la cola | los campos de una entrada | §5 |
| «FASE 12 · Las dos auditorías», el PROMPT B | cómo se pone el peso | §5 |
| la misma subsección, la persona del backend | la regla general de las peticiones a otro equipo | §8, «Las peticiones a otro equipo» |
| «Cómo se sabe si este manual funciona» | la prueba de pasar una tarea entera | «Cómo se sabe si este manual funciona», al final |

---

## Cómo se cita en este manual

- **Un documento se cita por el identificador o el nombre de su sección, nunca por número de línea ni por número de
  punto.** Una línea o un punto se mueven en cuanto alguien inserta algo encima, y la cita sigue pareciendo verificada.
  Los números de sección (`§5`, `FASE 12`) son identificadores: **no se renumeran**, y por eso valen.
- **Señalar un sitio del código hoy con fichero y línea es legítimo**, porque es una lectura con fecha: va **con su
  commit**, o se lee como «en este commit». Lo prohibido es **citar un documento** por línea para que alguien lo
  encuentre después.
- **Una entrada de la cola se cita por su identificador** (`#AB1`).

---

## Las ranuras de este manual

Se rellenan **antes de que un prompt haga de ficha**: en el arranque, en el primer incremento de la Fase 0 de v2 («El
sitio del trabajo»), y pasan a `CLAUDE.md` en la Fase 9. Cada sección que dependa de una ranura la cita por su nombre.

| Ranura | Qué es | Criterio para rellenarla | Qué cambia |
|---|---|---|---|
| **La ruta crítica** | La parte de la app donde un defecto hace el daño que no se deshace | Es el camino por el que pasa **el activo crítico**. No es un tema ni una carpeta | La frontera de las paradas (§6) |
| **El activo crítico** | Lo que se pierde, se filtra o se usa en nombre de otro | Lo que una persona no recupera **por la app ni automáticamente** | Las preguntas de peso (§5) |
| **Dónde vive el activo crítico** | En el dispositivo (lo introduce o lo guarda el usuario) o en el servidor (la app lo pide, lo muestra o lo modifica) | Dónde está la copia que manda | **La frontera entera** (§6, (b)) |
| **Si la app está publicada** | Si ya la usa alguien fuera del equipo | Hay usuarios que no son del equipo | El orden de los requisitos de salida (§5) |
| **Contra qué entorno prueba el ejecutor** | Local · un entorno de pruebas · producción | El entorno al que llegan las llamadas cuando el ejecutor prueba | Cuántas pruebas pasan por la pregunta de las escrituras (§6) y qué datos de prueba se usan |
| **Quien decide** | La persona con la autoridad de producto | Una sola persona, con nombre | §1 |
| **El revisor** | Un agente en otra conversación, con la memoria de las decisiones | Que no comparta contexto con el ejecutor ni lea el código. **Un agente que puede leer el repositorio no es esta ranura**: es un segundo auditor con otro modelo, y se nombra así (v3, A9) | §1 |
| **Los otros equipos** | Quien sirve lo que la app consume (backend, API de un tercero) | Si existe | §8 |
| **La bandeja** | Donde llegan las peticiones: un gestor de incidencias, un correo, un canal | Es autoridad sobre **qué se pidió y quién lo pidió**, no sobre el trabajo | §5 |
| **La auditoría visual de UI** | La herramienta que el proyecto usa para auditar la UI | La que valida v2 en la Fase 11 | El cierre (§2) |
| **La herramienta de grafo** | La que contesta preguntas de alcanzabilidad sobre el código | La que el proyecto use, con su commit | §10 |
| **La zona horaria del proyecto** | La de quien decide | Toda fecha tomada de una herramienta se convierte a esta zona | §7 |

### Alcance declarado

Este manual **supone**, sin que haga falta una ranura porque no cambian lo que harías, solo lo acotan [sin medir: es la
lectura del auditor de lo que el texto supone, aceptada como alcance]:
- **Una persona y trabajo en serie:** una persona con autoridad, sin compañeros que revisen PR, y una tarea tras otra.
  Con varias tareas en paralelo, «la tarea siguiente» (§2) es la siguiente **del mismo dueño**, y la revisión de código
  humana fuera de la ruta crítica, que aquí no existe, habría que añadirla.
- **Que esa persona reparte encargos entre varias sesiones** de Claude Code, con el revisor redactándolos (§8).
- **Usuarios con una contraparte humana:** las preguntas de peso suponen que el activo es de alguien, y que hay un tercero
  que podría deshacer un daño fuera de la app. En una app de un solo usuario, (d) casi no aplica.
- **Una sola zona horaria** para el equipo.
- **El ejemplo de ruta crítica de la tabla es un ejemplo de la forma**, no un supuesto sobre la app.

---

## §1 · Los tres papeles, y qué decide cada uno

| Papel | Quién | Escribe | Decide | No hace nunca |
|---|---|---|---|---|
| **Ejecutor** | Claude Code, en el repositorio | El plan, el código, los tests, las mediciones, las entradas de la cola, el informe | Lo que una regla escrita ya resuelve (§2.1, §6). Cómo se implementa lo autorizado | Poner un peso; aprobar su propio plan; decidir un texto que lee quien usa la app; cruzar una parada |
| **Auditor sin contexto** | Un subagente que no recibe la conversación (mecanismo: nace sin ella) ni la bandeja (mecanismo: su filtro de GitHub, por nombre); con `Bash` puede leer todo el disco (v2, «FASE 12 · Las dos auditorías») | Hallazgos, y «lo que no pude comprobar» | Nada | Leer la bandeja; aceptar como medido lo que el plan afirma |
| **Quien decide** | La persona con autoridad de producto, que trabaja con **el revisor** | La ficha, las decisiones (con firma y fecha), el peso, el texto de producto | Qué se hace, en qué orden, con qué peso, con qué texto; la autorización de implementar | Dar por hecho lo que afirma sobre un fichero sin que se mida |

**Lo único que no se delega es la autoridad de producto**: el texto que lee quien usa la app, y las decisiones que no son
técnicas.

**Lo que un agente puede hacer lo dicen sus herramientas, no su descripción** (v3). «Solo lee» en un agente con `Bash` es
una instrucción, no una frontera. Su descripción dice lo que es: «Read y Bash (lo que el entorno ofrece: v2 FASE 12,
«`tools:` no es un suelo»); GitHub, denegado por nombre; no edita por instrucción». **No se amplía el filtro para negar escrituras por nombre**, porque hereda el mismo agujero
que se midió en el origen con `git ls-remote`. El auditor necesita `Bash` [leído: sus pasadas guardadas usan `git
check-ignore`, `git ls-files`, `git merge-base`, corren los verificadores y leen las fuentes de una librería dentro de un
jar]. Sin `Bash` le quedaría solo Read (en la app de escritorio 2.1.321 no hay Grep ni Glob, `#PP31`): quitárselo no es
una frontera practicable hoy.

### Quien escribe no pesa
**El ejecutor propone el peso con las preguntas respondidas una a una; quien decide lo pone. Quien está afectado por un
defecto no lo pesa solo.** *Caso [leído]:* un defecto de un instrumento de verificación se pesó como 🟡 cuando era higiene,
porque quien lo pesaba firmaba los repartos con ese instrumento. Nadie corrió las preguntas: se corrió una impresión.

### Quien decide no lee el código
**Una afirmación de quien decide o del revisor en indicativo sobre un fichero se mide antes de entrar en un documento, o
se reescribe como pregunta.** Su salida son **decisiones y preguntas**; los hechos sobre el repositorio los mide el
ejecutor. *Caso [leído]:* el revisor afirmó que un contrato declaraba un dato «por ruta», eso entró como requisito, y el
contrato tenía un mapa plano sin esa dimensión: el requisito contradecía las reglas de negocio del mismo plan.

**Y al dudar, comprueba la premisa de la duda** igual que la de una afirmación: dudar es afirmar que el otro puede estar
equivocado.

| Regla de §1 | La prueba |
|---|---|
| El ejecutor propone el peso; quien decide lo pone | ¿La línea del peso lleva la firma de quien decide? |
| Quien está afectado por un defecto no lo pesa solo | ¿El defecto afecta a quien firma el peso? Entonces, ¿lo revisó otro? |
| Lo que quien decide afirma sobre un fichero se mide antes de usarse | ¿Hay una medición con commit junto a la afirmación, o está escrita como pregunta? |
| La premisa de una duda se comprueba | ¿La duda cita qué midió? |

---

## §2 · El ciclo de una tarea

**El alcance se autoriza primero, y es provisional hasta la pasada del auditor** (v3, A1). Si quien decide autoriza en la
conversación y el manual pide autorizar después del auditor, o la autorización es provisional, o la pasada del auditor es
teatro. En el origen, el hallazgo 1 de un auditor **cambió una decisión de alcance ya tomada** [leído: lo contó la
revisión externa, 2026-11-12]. Por eso la cabecera del plan lleva, literales, dos líneas:
«Alcance autorizado, provisional hasta la pasada del auditor.» y «Lo que el auditor cambió del alcance: …».

**La autorización se escribe en el plan, con firma y fecha: una aprobación que vive en una conversación no ocurrió**
[leído; en el origen, una instrucción que vivió solo en un chat la habría aplicado mal otra sesión que no la conocía].
**En el arranque** el ciclo cambia, y lo describe v2 («0 · Filosofía de trabajo», «Plan antes de aplicar»): el prompt
de la fase hace de ficha, el plan es solo la Parte 2, el auditor corre una vez por fase, y cada pieza del ciclo que
todavía no existe tiene su sustituto escrito en una tabla.

### El ciclo
| Paso | Documento | Lo escribe | Lo aprueba | Parada |
|---|---|---|---|---|
| **Ficha** | Media página en lenguaje llano: qué y para quién | Quien decide (o un encargo del revisor que **declara** que hace de ficha) | — | **Sin ficha no se escribe nada**: ni spec ni tareas. Se pide |
| **Alcance** | En la cabecera del plan: «Alcance autorizado, provisional hasta la pasada del auditor.» | Quien decide | Quien decide | Es provisional: lo confirma la autorización, que dice qué cambió el auditor |
| **Medición** | Una **nota de medición** en `docs/mediciones/`, con el formato de v2 (ruta, plataforma, build, commit, fecha), cada afirmación marcada | Ejecutor | — | Si la ficha se apoya en una premisa **sin medir**: se mide, **se para** y se reporta. No se implementa «para ver» |
| **Plan** | `docs/plans/AAAA-MM-DD-<feature>.md` con la plantilla de v2 (Fase 0, «El sitio del trabajo»): Parte 1 (spec) y Parte 2 (plan y tareas) | Ejecutor | — | Si no cabe en ~150 líneas, **son dos features**, y se dice **antes** de escribirlo. Lo comprueba `tope_plan.py` (abajo) |
| **Auditoría** | *Revisiones*, con cada hallazgo y su resolución (§2.1) | Auditor (hallazgos) · ejecutor (resoluciones) | — | Los hallazgos que ninguna regla resuelve |
| **Revisión** | Las correcciones, en *Revisiones* | Quien decide, con el revisor | Quien decide | **Es una sola.** Si quien decide pide cambios, se aplican y se le devuelve el plan dentro de la misma revisión, sin abrir otra. Lo que no vale es aprobar, abrir el PR y seguir pidiendo |
| **Autorización** | «Lo que el auditor cambió del alcance: …» y la línea `Implementación autorizada: Sí (<quién>, <fecha>, <granularidad>)` en la cabecera del plan | Quien decide. Si la escribe el ejecutor al dictado, **copia literal el mensaje de la autorización en *Revisiones*** con su fecha, para que viva en el repositorio | — | **Sin esa línea no se escribe código.** Aprobar el plan no autoriza implementarlo |
| **Rama** | Una rama nueva, **subida al remoto al crearla** | Ejecutor | — | — |
| **Tareas** | Commits, cada uno con la puerta (`puerta.sh --commit`, v2, «FASE 6.5») | Ejecutor | — | Todo cambio de conducta **no nombrado** en la autorización para y pregunta. También las paradas de §6 |
| **Cierre** | La etapa final del plan, marcada casilla a casilla (abajo) | Ejecutor | — | — |
| **Revisión de pre-PR** | El informe de `<proyecto>-review` (v2, «FASE 11 · Commands & Skills») y sus correcciones | Ejecutor | — | **Antes** de abrir el PR |
| **PR** | El PR: qué checks se saltaron además de los verdes, dónde están las capturas, y qué entrada cierra cada captura | Ejecutor | — | Se abre **cuando no queda nada por cambiar**: si después hace falta otro commit que no sea un conflicto, se abrió antes de tiempo |
| **CI** | — | — | — | Ante un rojo, la primera pregunta es **si corrió** (§3) |
| **Fusión** | — | Quien decide si toca la ruta crítica; si no, el ejecutor | Quien decide, si toca la ruta crítica | **Depende de la ranura de protección de ramas de v2** («Primera página», «Protección de ramas»). Con protección, la fusión espera los checks requeridos. **Sin protección**, las reglas de v2 («FASE 7», «Las tres reglas de fusión sin protección de ramas»): esperar el verde, comprobar que la cabeza de la rama es la revisada, y fusionar a mano |
| **Después de fusionar** | El plan pasa a `docs/historico/planes/`, extrayendo antes lo que siga vivo y reapuntando a quien lo cite | Ejecutor, como primer commit de la rama de la tarea siguiente | — | — |

**El plan se mueve al histórico después de fusionar, no antes** (quien decide, 2026-11-09): mientras corren el CI y la fusión,
la autorización y la verificación previa se leen en `docs/plans/`. **Nada del histórico se reescribe**; solo se añade
una línea «Superado el AAAA-MM-DD → <puntero>».

**Después de fusionar**, lo que aparezca viaja con la rama de la tarea siguiente, salvo que bloquee algo con fecha o esté
vivo en la ruta crítica. **Con el PR abierto, quien decide no manda instrucciones nuevas**, con la misma excepción (el
caso, en §9, «Poner una decisión donde no es el encargo»).

**Una tarea va en una rama y sale en un PR**: dentro van el código, el plan, la documentación, las mediciones, las
correcciones de la revisión, los cambios en la cola y el barrido del texto de producto.

### El carril ligero (v3, A2)

**Un cambio S que solo toca documentación o configuración, sin conducta nueva, se salta el plan y el auditor. No se
salta la revisión.** El ciclo entero sobre tres PR de solo documentación fue caro en el origen; y el único defecto
crítico de esos tres lo cazó **la revisión**, en un PR de solo documentación: un absoluto («el resto del documento sigue
valiendo») sobre filas que esa rama no había comprobado [leído: lo contó la revisión externa, 2026-11-12]. Un documento
afirma cosas falsas igual que el código.
- **Entra en el carril:** documentación, configuración del proyecto o de las herramientas, sin conducta nueva de la app
  ni de un verificador.
- **No entra:** un verificador nuevo o cambiado (cambia lo que la puerta deja pasar), un hook, un agente o una skill
  (caducan su validación), ni nada que toque la ruta crítica.
- **Sigue en pie:** la rama, la puerta, la revisión de pre-PR y el PR. El PR dice «carril ligero» y por qué entra.

### El plan por dentro

```text
PROMPT · Escribe el plan de <feature> en docs/plans/AAAA-MM-DD-<feature>.md con docs/plans/_plantilla.md. Antes:
1. ¿Hay ficha? Si no, para y pídela.
2. Mide lo que la ficha da por hecho. La medición contesta: qué hace mal · dónde más se decide · qué hay ya escrito
   sobre esto (búscalo por el símbolo Y por la frase en docs/plans/, docs/historico/, wiki/decisiones/ y la cola; si
   una carpeta todavía no tiene nada, escribe "sin antecedentes en <carpeta>", no lo saltes) · ¿está resuelto en otro
   camino de la app, y cómo?
3. Dimensiona por actores: ¿esto añade un segundo actor a código que hoy tiene un dueño? Si sí, las piezas previas se
   planifican desde el principio.
4. Si el diseño depende de una pregunta a otro equipo, la pregunta sale ANTES de especificar lo que depende de ella:
   se especifica con el valor seguro y se escribe de qué depende. Antes de preguntar, busca la respuesta en el
   contrato que ya tienes.
Parte 1 (QUÉ y POR QUÉ): qué construimos y para quién · situación actual · invariantes (por su frase, no por número)
· alcance · flujo · datos y reglas · casos alternativos · criterios de aceptación · decisiones pendientes.
Parte 2 (CÓMO):
- VERIFICACIÓN PREVIA con "Contra el commit: <sha>": una fila por afirmación, con cómo se comprobó (grep para dónde
  aparece algo; la herramienta de grafo para quién llega a qué, §10), QUÉ RAMAS midió, contra qué commit y su
  resultado. HECHO y SUPUESTO separados; un supuesto lleva la tarea que lo mide. Una cita heredada de otro plan se
  abre, o se marca "heredada sin abrir" con su origen y fecha. Y la pregunta del plan contra sí mismo: ¿qué de lo que
  asumo que existe lo borra este mismo plan?
- Solución y decisiones: si eliges distinto de una decisión escrita, la citas y dices por qué la cambias.
- Mapa de ficheros, tareas por etapas con su ORDEN (tabla de abajo), y la etapa de cierre.
- VERIFICACIÓN POR CRITERIO: cada criterio DECLARA SU CLASE (§4). Si es verificable: método, evidencia y LA MUTACIÓN
  QUE LO MATA, concreta (fichero, cambio, aserción que cae). Si es una comprobación humana declarada (un criterio de
  UI, con su captura): quién la hace, sobre qué build, y dónde queda la evidencia.
- Lo que no podrá comprobarse aquí, y qué haría falta.
REVISIONES (no cuenta para el tope): cada corrección de un punto de control con su nombre, su sitio y su commit; la
pasada del auditor; las correcciones del revisor.
Estado: "Plan en borrador". "Alcance autorizado, provisional hasta la pasada del auditor: <sí/no, quién, fecha>." "Lo que
el auditor cambió del alcance: (tras la pasada)". Implementación autorizada: No.
```

**El tope de ~150 líneas se queda, con su verificador** (quien decide, 2026-11-09). **Un tope sobre un documento que
crece por acumulación es el instrumento equivocado** (v2, «FASE 9 · CLAUDE.md»); **un tope sobre un documento que
describe una unidad de trabajo es una heurística de alcance, y su única salida, partir la feature, es la buena.** De las
tres partes del argumento contra los topes, solo aplica una: **es una regla sin verificador, y se rompió igual.** En el
origen, tres planes vivos tenían 258, 641 y 1633 líneas frente a un tope de ~150 [medido: `wc -l`, 2026-11-09, sin
descontar las exclusiones]. Lo comprueba `tope_plan.py` (v2, «FASE 6.5»), que cuenta lo que el tope cubre, con las
exclusiones enumeradas y con su motivo; hoy, una: *Revisiones*, porque es el diario de la puerta y no alcance.

**El orden por tarea** [leído]:

| Capa | Orden |
|---|---|
| Lógica pura (validadores, cálculos, mappers) | Test primero, verlo fallar **por una aserción**, luego el código |
| Repositorios y casos de uso | Test primero, con dobles de red |
| ViewModels | Test primero |
| Composables y UI | Sin test primero: **captura real**, que es una comprobación humana declarada (§4) |
| Corregir un bug | El test que reproduce el fallo, primero |

Bien escrita: «B2 — test del mapper primero con una respuesta real del servidor, verlo fallar, luego el DTO». Mal
escrita: «B2 — DTO y mapper con tests».

**La verificación previa se rehace al implementar**, pero solo en las filas que tocan los commits posteriores: un «HECHO»
vale contra su commit, no contra el de hoy. *Caso [leído]:* una premisa medida era cierta al medirla y falsa tres commits
después, porque otra feature cambió una guarda, y nada la volvió a mirar.

### La etapa de cierre (dentro del PR)
- **Capturas.** Un cambio de texto se captura en las dos plataformas; lo demás, en una, salvo que pueda romperse
  distinto.
  - Se commitean bajo `docs/mediciones/`, con el índice de v2 escrito con la imagen abierta, y se miran enteras antes de
    commitear por si traen un dato personal.
  - **Dos puertas, no una contradicción: la captura es puerta del cierre de la entrada, no del PR** (quien decide,
    2026-11-09). «No queda nada por cambiar» se refiere a **cambios**, no a evidencia que no se puede producir:
    - una captura **alcanzable** se toma **antes** de abrir el PR; si llega después, el PR se abrió temprano;
    - una captura que **el proyecto no puede alcanzar** no retiene el PR: es una **comprobación humana declarada no
      ejecutable**, va a los requisitos de salida de la entrada, y la entrada **sigue abierta**.
    Lo mismo, dicho en §4.
- La puerta entera con `--rerun-tasks`.
- **La auditoría visual de UI** (ranura) si se tocó UI.
- **Los overviews** del área que la feature cambió (v2, «FASE 9», «Overviews y reversiones»).
- **El barrido del texto que deja de ser cierto.** Al construir una salida nueva, se busca el texto que describía la
  única salida, con `barrido_texto.py` (v2, «FASE 6.5»). Su lista de frases se escribe en el idioma del producto; en un
  producto en español serían cosas como «todavía no», «no se puede», «por ahora» [leído: es la lista del origen].
- **Las entradas de la cola cuyo disparador nombra esta feature o sus ficheros:** cada una se cierra aquí, o su entrada
  dice por qué no.
- **No hay plan de acción que actualizar.** El orden del trabajo vive en la cola (v2, «FASE 9», «Lo que no se crea: un
  plan de acción»); «qué va ahora» es la vista que genera `que_va_ahora.py` (v2, «FASE 6.5»), con su hora y su commit.

| Regla de §2 | La prueba |
|---|---|
| Sin ficha no se escribe nada | ¿La cabecera del plan nombra su ficha? |
| Una premisa sin medir se mide y se para | ¿Hay una nota de medición antes del plan? |
| Si no cabe en ~150 líneas, son dos features | `tope_plan.py` (v2, «FASE 6.5») |
| La revisión es una | ¿Hay una sola sección de revisión con fecha? ¿Llegó alguna instrucción con el PR abierto? |
| Sin autorización escrita no hay código | ¿El primer commit de código es posterior a la línea de autorización? |
| La rama se sube al crearla | `git ls-remote` de la rama tras su primer commit |
| El PR se abre cuando no queda nada | ¿Algún commit posterior a la apertura que no sea un conflicto? |
| El plan se mueve después de fusionar | ¿El commit que lo mueve es de la rama siguiente? |

### §2.1 · El auditor sin contexto: cuándo, qué recibe y cómo se resuelve
- **Cuándo:** con las dos partes del plan redactadas, **antes de enseñárselo a nadie**. En el arranque, una vez por fase.
- **Quién lo lanza:** quien redacta, **siempre, sin preguntar** [leído]. Un paso que depende de que alguien conteste «¿lo
  lanzo?» falla en silencio cuando nadie contesta, y en el origen pasó así una vez.
- **Qué recibe:** la ruta del plan. Nada más: ni la conversación ni la bandeja.
- **Qué devuelve:** hallazgos con el sitio del código (fichero y línea, con el commit) o del documento (por su sección), y
  «lo que no pude comprobar».
- **Dónde queda (v3):** quien lo lanzó guarda el informe **literal, antes de resolver ningún hallazgo**, en un fichero
  por pasada: `.claude/reviews/review-<rama>-<fecha>[-pasada-N].md`, la forma del origen. Sin eso, la evidencia de lo que
  el auditor encontró la redacta quien aplicó los hallazgos. Si se rastrea en git es una decisión de cada proyecto; en la
  plantilla, no.
- **La forma, mecanizada (v3, A3; no montado: la plantilla lo tiene en la cola como `#PP30`):** un `plan_lint.py` en la
  puerta comprueba la forma del plan (secciones de la plantilla, la clase de cada criterio, las casillas de cierre, las
  dos líneas del alcance). Dos motivos. El primero: más de un tercio de los hallazgos de una pasada eran de forma
  [derivación del ejecutor de la plantilla, no un recuento: los informes anteriores no se guardaban]. El segundo, que no
  necesita la cifra: **un hallazgo de forma y uno de fondo se leen igual en la lista, y los de forma diluyen la atención.
  Cuando todos los hallazgos del auditor son de fondo, se creen más.**

**Antes de corregir al auditor, corrige el criterio:** si entendió mal lo que mediste, empieza por cómo lo escribiste.
*Caso [leído]:* reportó como «no pude medir» una mutación que sí se había corrido, porque el enunciado del criterio
admitía una lectura inerte.

**Cómo se resuelven sus hallazgos.** Los resuelve quien redactó, sin subirlos uno a uno, con estas reglas. Cada una es
una decisión que quien decide ya tomó:

| Hallazgo | Resolución |
|---|---|
| Un campo cuya ausencia hace falso un número | El campo vuelve. Sin preguntar |
| Un paso que no puede contestar la pregunta que dice contestar | Se corta o se reescribe la pregunta. Elige quien redacta y escribe cuál y por qué |
| Un texto de producto que afirma algo que no se mide | Se corrige hacia lo medible, y **el texto final sube a quien decide** |
| Un valor de un registro, una métrica o un diagnóstico que afirma lo que el código no sostiene | Se corrige hacia lo medible. No sube: no es texto de producto |
| El plan pide un dato que el mismo plan prohíbe | Contradicción interna: se resuelve y se anota |
| Sobre una escritura (reversible o irreversible), una decisión de un solo sentido o la ruta crítica (§6) | **No se resuelve: se para y se pregunta** |
| No cae bajo ninguna regla | **Sube, siempre.** Su resolución se escribe como regla nueva, con fecha, y esa clase ya no vuelve a subir |

**Cuándo se deja de parchear y se rehace la forma** [leído]:
- **Fija la condición de salida de cada pasada antes de correrla.** Termina en uno de estos sitios: sin defecto de forma,
  sube a revisión; con defecto de forma y arreglo de forma, se aplica y se repite; con defecto de forma y sin arreglo,
  se rehace.
- **Decide rehacer por una causa común, no por el número de hallazgos.** Si los hallazgos comparten causa, el arreglo es
  estructural (una distinción o una regla que falta), no N correcciones.
- **Rehaz una capa cuando necesite arreglo estructural por segunda vez.** Cuentan los cambios de forma (qué **es** una
  pieza), no las correcciones dentro de una forma: que a un instrumento recién hecho le falte una rama es el instrumento
  funcionando. Lo que se vigila es **que el plan gane un mecanismo por vuelta**.
- **Para de parchear cuando dos pasadas vuelvan al mismo sitio, y rehaz la forma.** *Su límite:* no dispara cuando el
  defecto está en un PR sin fusionar, en revisión (eso es la revisión funcionando), ni cuando el arreglo **quita** una
  capacidad en vez de añadirla. *Caso [leído]:* varias pasadas encontraron cada una otra pieza compartida entre dos
  caminos de una misma pantalla, y se paró cuando se separaron en dos pantallas: el compartir era el defecto.

| Regla de §2.1 | La prueba |
|---|---|
| Lo lanza quien redacta, sin preguntar | ¿*Revisiones* tiene la pasada con su fecha antes de la de la revisión? |
| Cada hallazgo, con su resolución y la regla que la decidió | ¿Hay algún hallazgo sin regla nombrada? |
| La condición de salida, fijada antes | ¿Está escrita antes del primer hallazgo de la pasada? |
| Rehacer por causa común | ¿La resolución nombra la causa? |

---

## §3 · Qué cuenta como verificado

Es el corazón del documento. Un verde puede no ser de una ejecución: eso está en v2 («2 · Convenciones transversales»,
«Verde no es corrió»: `UP-TO-DATE`, `FROM-CACHE`, pasos saltados, checks que no pueden fallar, otro proceso en el
árbol), y no se repite aquí. **Esto es lo de después**: aunque la ejecución sea real, el resultado puede no probar lo que
nombra.

Toda comprobación tiene **tres resultados: pasó, falló y no pude medir**. El tercero nunca se escribe como ✅.

| Trampa | Por qué no prueba | La señal | Lo que sí prueba |
|---|---|---|---|
| **El rojo es un fallo de compilación** | Un test que no compila está rojo por cualquier cosa | La salida dice `Compilation error` o `Unresolved reference`, no `AssertionError` | Escribir antes la firma vacía, para que el rojo sea **la aserción** |
| **El test nace verde** | No se sabe si asierta algo | Nunca se vio rojo | **Su mutación**: romper la fuente y ver caer **esa** aserción. Hasta entonces no prueba nada |
| **La mutación no compila** | Un build roto también es rojo, y no dice nada de la guarda | El rojo es de compilación | Una mutación que compila y cambia conducta. Si el mutador da éxito con un build roto, el instrumento está roto [leído; en el origen es deuda abierta] |
| **La mutación no se aplicó** | El patrón no encontró nada, y el test sigue verde porque nada cambió | El diff de la mutación está vacío | Leer el diff que imprime `mutar.sh` (v2, «FASE 6.5») antes del resultado |
| **Un código de salida 1** | No dice **qué** aserción cayó: puede ser otra | Solo se leyó el código | Leer el rojo **hasta la aserción** y comprobar que es la del criterio |
| **Una medición sin brazo de control** | No distingue «funciona» de «no puede fallar» | Solo se corrió el caso que debía pasar | Correr también el caso que **debe** salir mal, y verlo salir mal [sin medir: el nombre es de este manual; el principio, del origen] |
| **Nombrar una mutación no es correrla** | Una mutación escrita es una hipótesis | La tabla de criterios la nombra, y nadie la vio roja | Correrla y leerla hasta su aserción. *Caso [leído]:* de nueve mutaciones nombradas en un plan, cuatro no llegaban a ninguna aserción |
| **Una fila HECHO sobre una rama** | Mide media regla | La fila no nombra ramas | Cada fila dice qué ramas midió y contra qué commit |
| **Un valor que coincide** | No distingue «se deriva de la regla» de «se escribió a mano igual» | Un solo test | Dos tests (uno fija el valor, otro lo compara con la fuente) y una mutación **en la fuente**: cae el primero y aguanta el segundo |
| **Dos features que se tocan** | Cada criterio verde por separado puede componer algo que ninguno describe | Ningún criterio nombra las dos | Un criterio de composición en la feature nueva, con una mutación por cada mitad |
| **«No hay forma de X»** | Leyendo se establece que no encontraste la forma, no que no exista | La conclusión no tiene un intento detrás | Haber intentado X. Si intentarlo exige una escritura no autorizada, se escribe «no encontré ruta; no lo intenté» y se pide la autorización |
| **«Ningún camino llega a X»** | Un `grep` da el primer anillo | La evidencia es una búsqueda de texto | La herramienta de grafo, con su commit (§10) |
| **Una palabra visual** («se solapa», «se corta») | No dice qué hacen los píxeles | Nadie describió la geometría | La captura y lo que se ve en ella |
| **El resumen de un guion** | Es una afirmación sobre la salida | Se leyó el resumen | Contrastarlo con la salida cruda. *Caso [leído]:* un resumen decía que la app no estaba viva a los 25 s, y el registro la daba en primer plano hasta que el propio guion la terminó |
| **Una medición que depende del tipo de build** | Lo que se prueba desde un build de depuración puede no existir para un servicio (telemetría, informes de fallos) | La medición no dice desde qué build se provocó | Escribir el build, y no leer un cero sin él |

**Una premisa con caducidad** (una ventana de tiempo, un token, una versión mínima) **se relee inmediatamente antes de
actuar**: la lectura tiene que ser más joven que la vida del estado leído, o es un recuerdo. *Caso [leído]:* se pulsó un
botón esperando un rechazo del servidor, decidiendo sobre una lectura de dos días de un estado que caducaba en uno, y se
creó un registro de más en el entorno de producción.

```text
PROMPT · Antes de dar por verificado el criterio <CA>, contesta cada pregunta con pasó / falló / no pude medir:
- ¿De qué clase es (§4)? Si es una comprobación humana declarada, no sigas: va a la lista de reparto como paso
  declarado.
- ¿Se vio rojo por una ASERCIÓN, y es la del criterio? Pega la línea.
- ¿Su mutación se aplicó (pega el diff que imprime mutar.sh), compila, y tumba ESA aserción?
- ¿Hay brazo de control: el caso que debe salir mal salió mal?
- ¿Qué ramas cubre, y contra qué commit?
- ¿Alguna tarea salió UP-TO-DATE, FROM-CACHE o skipped (v2, «Verde no es corrió»)?
Si alguna es "no pude medir", el criterio no está verificado.
```

---

## §4 · Las dos clases de comprobación

| Clase | Qué es | Cómo se reconoce | Dónde se lista |
|---|---|---|---|
| **Criterio verificable** | Una comprobación que una mutación pone roja, y se ve | Tiene mutación concreta y se vio caer | En COMPROBADO: el PR, la *Verificación por criterio*, la lista de reparto |
| **Comprobación humana declarada** | Alguien hizo algo y lo escribió: una validación de una skill, una captura, una prueba en un dispositivo físico, la lectura de un panel | Su evidencia es una fila escrita por una persona o un agente, no la salida de un guion | **En PASOS DECLARADOS de la lista de reparto** (v2, «FASE 8»), nunca en COMPROBADO |

**Una comprobación humana declarada no cuenta como verificada.** Lo más que puede tener es un verificador que compruebe
que **se declaró**:
- **Para skills y commands**, v2 lo monta (`validaciones.py`, «FASE 11»): la fila existe con sus prompts, y caduca si cambia
  lo validado.
- **Para capturas, pruebas en dispositivo y lecturas de un panel, v2 no monta verificador.** Su sitio es la sección
  PASOS DECLARADOS de la lista de reparto, que se rellena a mano.

**La captura es de esta clase, con una diferencia: su evidencia se archiva y se puede contrastar.** Fusionar pone el
código en la rama principal; cerrar la entrada afirma que el defecto está arreglado [leído]. **Dos puertas, no una
contradicción: la captura es puerta del cierre de la entrada, no del PR** (quien decide, 2026-11-09). «No queda nada por
cambiar» se refiere a **cambios**, no a evidencia que no se puede producir:
  - una captura **alcanzable** se toma **antes** de abrir el PR; si llega después, el PR se abrió temprano;
  - una captura que **el proyecto no puede alcanzar** no retiene el PR: es una **comprobación humana declarada no
    ejecutable**, va a los requisitos de salida de la entrada, y la entrada **sigue abierta**.
Lo mismo, dicho en §2, «La etapa de cierre».

```text
PROMPT · Lista de reparto de <versión>, con docs/reparto/_plantilla.md. Dos secciones, nunca mezcladas:
COMPROBADO: criterios verificables, con su mutación vista y su commit.
PASOS DECLARADOS: validaciones, capturas, pruebas en dispositivo, lecturas de un panel, y las dos listas de release;
cada uno con quién lo hizo, cuándo, sobre qué build y dónde está su evidencia.
Lo que falta de la segunda sección no se mueve a la primera.
```

| Regla de §4 | La prueba |
|---|---|
| Lo declarado no va en COMPROBADO | ¿Alguna fila de COMPROBADO sin mutación ni commit? |
| La captura cierra la entrada, no la fusión | ¿La entrada que una captura cierra cita la ruta de la captura en `docs/mediciones/`? |
| Una captura alcanzable va antes del PR | ¿Algún commit de captura posterior a la apertura del PR? |
| Una captura no alcanzable queda declarada | ¿Está en los requisitos de salida de la entrada, con la entrada abierta? |

---

## §5 · La cola

### Por qué existe
En el origen hubo dos auditorías integrales con **33 hallazgos** entre las dos, y **ninguno de los dos informes registró
cuál se arregló**. Dos meses después seguían pidiendo trabajo ya hecho, y no había forma de saber cuál sin auditar al
auditor [leído]. Un informe se archiva; **lo que vive es la cola**, que v2 crea con su estructura y su verificador (v2,
«FASE 0 · Proyecto + reglas base», «El sitio del trabajo»), y `cola.py` la comprueba desde la «FASE 6.5».

**La bandeja y la cola son cosas distintas.** La bandeja (la ranura: un gestor de incidencias, un correo, un canal) es
autoridad sobre qué se pidió y quién lo pidió. La cola, sobre qué se va a hacer, con qué peso y en qué orden. **Una
petición de la bandeja se da por atendida cuando lo que pedía está satisfecho, o cuando el trabajo que queda tiene dueño
en la cola.** Nunca es la bandeja la dueña del trabajo. **El orden del trabajo vive solo en la cola.**

### Cómo se escribe una entrada
- **Busca el mecanismo antes de abrir la entrada, no un identificador libre.** Si el mecanismo ya tiene entrada, añade
  ahí lo nuevo y **deja el peso que ya estaba**: un mecanismo no cambia de daño porque alguien acabe de verlo. *Caso
  [leído]:* en un solo día se firmaron ocho pesos nuevos; tres eran de mecanismos que ya tenían entrada, y dos de esos
  tres salieron inflados.
- **Identificador único y estable** (`#AB1`), que **no reutiliza uno que existió**: lo comprueba `cola.py` leyendo el
  historial (v2, «FASE 6.5»). Un identificador **se registra, no se presta**.
- **La fecha en el título**, que es el único sitio donde no caduca. La cabecera de la cola **no** lleva la cuenta de qué
  cambió: eso lo da `git log`.
- **Un título que dice qué se rompe**, no un área. Un título que nombra un área («antes de publicar») es un cajón. *Caso
  [leído]:* una entrada con nombre de área enterró durante tres semanas, como una viñeta más, el defecto más grave que
  contenía.
- **Quién lo desbloquea:** nosotros, quien decide u otro equipo. Debajo, si hay una medición:
  - **Su parte, hecha:** quién hizo qué y cuándo, y **Queda:** lo que falta, que es nuestro.
  - **Sin motivo: entera | en parte.** «Sin motivo» significa que **una medición muestra que el motivo de la entrada ya no
    ocurre**, aunque nadie la haya cerrado. Va una medición por viñeta, con su autor y su fecha, y **No cubre:** lo que
    queda.
- **Un disparador que pueda dispararse**, escrito como «cuando <un evento que alguien puede observar>», con su estado
  medido: antes de escribir «cuando ocurra X», se comprueba si X ya ocurrió. Si no hay un evento así, va **una fecha de
  revisión**. *Caso [leído]:* un disparador «después de la feature F» se escribió seis días después de que F se
  fusionara.
- **Vigente hasta que:** las condiciones de cierre, una por una.

```text
PROMPT · Abre una entrada en la cola para <defecto>.
1. Busca el MECANISMO en docs/deuda-tecnica.md, por el símbolo, por la frase y por el efecto. Si ya tiene entrada,
   añade ahí lo nuevo con tu medición fechada, y NO cambies su peso: di cuál es.
2. Si no tiene: cuenta los mecanismos. Si hay más de uno, son varias entradas.
3. Escribe: un id nuevo (cola.py comprueba que no existió), el título con la fecha y qué se rompe, el mecanismo, quién
   lo desbloquea, el disparador con su estado medido o la fecha de revisión, y "Vigente hasta que".
4. Peso: NO lo pongas. Márcalo "(peso por decidir)" y debajo propón uno, con las cuatro preguntas respondidas una a
   una y en qué sistema ocurre el daño.
Cada afirmación, marcada medido / leído / sin medir.
```

### El peso

**El peso mide daño. La probabilidad vive en el disparador.** Un 🔴 sin disparador alcanzable no va primero, pero queda
marcado para el día que se cumpla.

**Pesa una entrada solo cuando tenga un mecanismo.** Si tiene varios, pártela primero, y el veredicto «partir» es final:
cualquier peso sobre varios mecanismos infla los pequeños o entierra el grande.

**Mira en qué sistema ocurre el daño antes de pesarlo.** Si ocurre en un sistema que no tienes, no es una medición tuya,
y el peso dice que se apoya en cómo se comporta un sistema ajeno.

**Las cuatro preguntas, solo para lo que toca la ruta crítica; para todo lo demás, la escala de tres** (v3, A4):
`requisito de salida`, 🟡 si alguien queda peor, `higiene` si solo lo ve el equipo. Fuera de la ruta crítica las cuatro
salen siempre «no» y son ceremonia. Dentro, hacen trabajo: en el origen, de siete usos, cuatro dieron cuatro «no»; en
los otros tres, una pregunta destapó una premisa falsa de quien pesaba, otra se sospechó y la medición la desmintió, y
un 🔴 salió directamente de (a) [leído: lo contó la revisión externa, 2026-11-12].

**Las cuatro preguntas** (la escala, cerrada: quien decide, 2026-11-09):
- **(a)** ¿Se pierde **el activo crítico** sin poder recuperarlo? **🔴.** Recuperar es **por la app o automáticamente**;
  deshacerlo pidiéndoselo a alguien no es recuperar, porque depende de la voluntad de otro y ocurre fuera de la app.
- **(b)** ¿Alguien queda **sin salida**? **🔴 en la ruta crítica, 🟡 fuera de ella.**
- **(c)** ¿No se puede **ver, oír o alcanzar** lo que hace falta para recorrer la ruta crítica? **🔴**, aunque la entrada se
  llame de accesibilidad.
- **(d)** ¿**El activo crítico de alguien llega a quien no debía, o se usa en nombre de otro**? **🔴.**

**Que (d) sea «más grave que (a)» ordena dentro del 🔴:** no añade un color ni cambia el orden de las preguntas. **Si las
cuatro son «no»: 🟡 si alguien queda peor, y fuera de escala si solo lo ve el equipo** (quien decide, 2026-11-09).

**Por qué hizo falta escribirlo así.** El auditor sin contexto encontró que la escala solo daba color en (b), que (d) era
«más grave» que el color más alto, y que no decía qué pasaba con cuatro «no» [medido: auditoría del 2026-11-09]. **El
agujero venía del origen** [leído: su escala tiene la misma forma], así que la decisión que más se usa del sistema llevaba
ambigua desde el principio.

**Tres clases fuera de la escala, sin color:**

| Clase | Qué es | Cómo ordena |
|---|---|---|
| **Requisito de salida** | Impide publicar una versión; no daña a nadie hoy | **Ranura «si la app está publicada»:** sin publicar, va **antes** que todo lo que tiene color, por la fecha de la salida; sacarlo de la escala **lo sube**, porque un defecto no daña a nadie hasta que la app llega a alguien. Publicada, ordena por la fecha de su versión, sin adelantar a un 🔴 que daña hoy a quien la usa |
| **Funcionalidad que falta** | No es un defecto | Por su fecha o por la decisión que la bloquea |
| **Higiene y herramienta** | Solo la ve el equipo | Al final |

**Las marcas del título:** 🔴 o 🟡; `(requisito de salida)`, `(funcionalidad)` o `(higiene)`; `(peso por decidir)`, cuando
el ejecutor propuso y quien decide aún no puso; `(peso condicionado a #XY1)`; `(por medir)`, con su medición pendiente;
`(por partir)`, sin peso hasta partirse.

**P0/P1/P2 no es el peso** (quien decide, 2026-11-09): es el triaje de un informe de auditoría, que ordena la ejecución de ese
lote y muere con él (v2, «FASE 12»). **El sha cierra el commit, no la entrada.**

### Agrupar por mecanismo, nunca por síntoma
**Agrupa los casos de una entrada por su mecanismo.** Un nombre que describe lo que le pasa a una persona recoge casos con
causas distintas, y entonces la entrada no puede tener peso ni disparador. **Establece las dependencias entre entradas
igual**: «#A espera a #B» por mecanismo, nunca porque los síntomas se parezcan. **Un síntoma compartido con un mecanismo
distinto son dos entradas hermanas**, no una ensanchada; lo que decide no es que los síntomas se parezcan, sino si el
arreglo es el mismo.

### Cómo se cierra
- **Cierra contra «Vigente hasta que», condición por condición.** Un plan ejecutado no es una entrada cerrada: el plan es
  el trabajo, la entrada es la propiedad.
- **Actualiza una condición de salida citando la decisión que la cambia, en su propio cambio**, nunca en el acto de
  cerrar: reescribirla al cerrar es ponerse la diana donde cayó la flecha.
- **No escribas «cerrada» sin el commit del cierre.** Hasta entonces es «sin motivo, pendiente de cerrar». El anuncio de
  un cierre dice qué parte cubre la medición y qué parte no.
- **Cuando una medición le quita la razón a una entrada, genera en el mismo turno la línea que pide su cierre.**
- **Cuando veas que la condición de otra entrada se cumplió, apúntalo en esa entrada**, no en la tuya.
- **Al cerrar, barre lo que se escribió mientras estaba abierta, buscando la afirmación de la entrada**: lo escrito en ese
  tiempo pudo copiar la versión equivocada.
- **En cada inventario de la cola, vuelve a aparcar cada entrada aparcada con su razón, o hazla.** Un disparador que
  depende de que alguien toque un fichero nunca llega a ser falso: solo deja de ocurrir.

| Regla de §5 | La prueba |
|---|---|
| Buscar el mecanismo antes de abrir | ¿La entrada nueva cita qué búsqueda se hizo? |
| Id que no existió | `cola.py` (con historial completo; con uno superficial sale 3) |
| Campos de la entrada | `cola.py` |
| El peso lo pone quien decide | ¿La línea del peso lleva su firma? Si no, ¿está marcada `(peso por decidir)`? |
| Un mecanismo por entrada pesada | ¿Su sección «Mecanismo» nombra uno? |
| Disparador alcanzable | ¿El disparador nombra un evento observable, y su estado medido tiene fecha? Si no, ¿hay fecha de revisión? |
| Cerrar con commit | ¿La fila de Cerrados tiene sha? |

---

## §6 · Las paradas: cuándo una sesión para y pregunta

**Plantea una parada en el momento en que ocurre**, aunque el encargo diga «no pares» [leído]. Parar no es desobedecer:
es el trabajo.

| Parada | Qué la dispara | Qué se hace |
|---|---|---|
| **Premisa sin medir** | Una orden apoyada en algo que nadie midió | Se mide, se para y se reporta |
| **La medición, en su turno** | Una medición que quien decide todavía no ha visto | No se encadena implementación detrás |
| **La ruta crítica** | Ver abajo: lo que el arreglo pide cambiar | Se para y se pregunta |
| **Una escritura fuera del árbol** (en producción, en un servicio, en un dispositivo ajeno). Con la ranura «contra qué entorno prueba el ejecutor» en producción, **toda** prueba que escriba lo es | La pregunta de abajo | Uno de los tres casos de abajo |
| **Un texto que lee quien usa la app** | Cualquier texto de producto | Se propone y se para: su versión final es de quien decide |
| **Un cambio de conducta no nombrado** | La autorización nombra qué conductas cambian | Cualquier otra para |
| **Un hallazgo que ninguna regla cubre** | — | Sube. Su resolución se escribe como regla, con fecha |

### Las escrituras: una pregunta y tres casos
**Se decide con una sola pregunta: ¿restaurar el estado anterior deshace el efecto?** (quien decide, 2026-11-09)

| Respuesta | Caso | Qué se hace antes de escribir |
|---|---|---|
| **Sí** | **Escritura reversible** | El **fichero de reversión** de v2 («FASE 9», «Overviews y reversiones»), con el estado anterior leído y fechado, rellenado y commiteado; y autorización expresa |
| **No, porque no se puede restaurar** (un registro que solo añade, un panel de un tercero, un mensaje enviado) | **Escritura irreversible** | **Confirmación** de quien decide. Antes se escribe **qué va a quedar ahí** |
| **No, porque alguien ya dependió de él** (un contrato publicado, un identificador expuesto, un formato persistido, un aviso ya leído) | **Decisión de un solo sentido** | **Confirmación** de quien decide. Antes se escribe **quién ya depende de ello** |

**Es la tercera vez que esto vuelve** [medido: primera pasada del auditor, hallazgo 5.6; segunda, hallazgo 6.1]. Las dos
primeras veces se nombraron las clases sin definir la frontera, y de ahí salía el término contradictorio «escritura
irreversible que se revierte». **Una clase nombrada no es una clase definida: lo que la define es la pregunta que la
decide.**

### La frontera de la ruta crítica
**No es el tema, es lo que el arreglo pide cambiar** [leído; generalizado de la regla del origen]. Un cambio en la ruta
crítica para y pregunta si:
- **(a) mete código dentro del flujo crítico**: un campo que hay que calcular, una llamada que hay que mover, un estado que
  hay que guardar para poder emitir algo; o
- **(b) cambia qué pasa con el activo crítico**, según la ranura «dónde vive el activo crítico»:
  - **en el dispositivo:** cambia qué dato del activo sale del dispositivo, o una secuencia de la que se pueda reconstruir;
  - **en el servidor:** cambia qué se escribe en el activo, o a quién se le muestra.

**Todo lo demás se resuelve por regla**, aunque esté en la misma pantalla: lo que solo **observa** la ruta crítica sin
cambiarla, como un registro, una métrica o un texto de diagnóstico, cuándo se emite y si contesta la pregunta que dice
contestar. **Observar no es tocar**: la frontera se cruza cuando el flujo tiene que guardar o calcular algo que antes no
necesitaba.

```text
PROMPT · Antes de tocar <cambio>: ¿es una parada? Contesta en orden:
1. ¿Alguna premisa del encargo está sin medir? → mídela y para.
2. ¿El arreglo mete código en la ruta crítica, o cambia qué pasa con el activo crítico (según dónde vive)? → para.
3. ¿Escribe fuera del árbol? Entonces: ¿restaurar el estado anterior deshace el efecto?
   - Sí → rellena y commitea el fichero de reversión (con el estado anterior leído y fechado), para y pide
     autorización.
   - No, porque no se puede restaurar → escribe qué va a quedar ahí, para y pide confirmación.
4. ¿Algo de lo que vas a cambiar ya lo usa alguien (un contrato, un identificador, un formato, un aviso leído)? →
   escribe quién depende de ello, para y pide confirmación.
5. ¿Cambia un texto que lee quien usa la app? → propón el texto y para.
6. ¿Cambia una conducta que la autorización no nombra? → para.
7. ¿Es un hallazgo que ninguna regla escrita resuelve? → súbelo.
Si ninguna aplica, sigue, y escribe en el informe que lo comprobaste.
```

| Regla de §6 | La prueba |
|---|---|
| La parada se plantea cuando ocurre | ¿El informe dice en qué momento paró? |
| Toda escritura fuera del árbol pasa por la pregunta | ¿El plan o el informe dice la respuesta y el caso? |
| La escritura reversible va con su reversión | ¿El commit del fichero de reversión es anterior a la escritura? |
| La escritura irreversible se confirma, con lo que va a quedar escrito | ¿Hay confirmación firmada y la frase de qué queda? |
| La decisión de un solo sentido se confirma, con quién depende | ¿Hay confirmación firmada y la lista de quién depende? |

---

## §7 · Cómo se escribe una regla, y cómo una decisión

### Una regla
- **Escribe la regla como la conducta que se pide, no como el fallo que se observó.** Escrita como «X sale mal», la
  refuta cualquier X que salga bien; escrita como «haz Y», un caso que hizo Y y salió bien la sostiene [sin medir: la
  formulación es del manual]. *Mal:* «Los verificadores colapsan el no-pude-medir en verde». *Bien:* «Un verificador sale
  3 cuando no pudo medir, y la puerta lo trata como rojo».
- **Dale a cada regla su prueba**: el verificador que la comprueba, o la pregunta que hay que contestar. Una regla sin
  verificador no se cumple [leído; con varias instancias en el origen]. **Un arreglo tampoco:** si un defecto se arregla
  y nada vuelve a mirarlo, vuelve (en la plantilla, `#PP8` → `#PP28`).
- **Cuando ya existe un verificador de la clase, extiéndelo; no escribas otro al lado** (v3). Dos verificadores de la
  misma clase divergen.
- **Toda pieza que declare una propiedad dice qué mecanismo la sostiene, o no la declara** (v3, la revisión externa,
  2026-11-12). «Solo lee», «no modifica», «sin acceso a», «hay protección de ramas»: cada una con su hook, su filtro, su
  lista de herramientas o su regla; y si solo la sostiene una instrucción, se dice «por instrucción». Una propiedad sin
  mecanismo se lee como garantizada. Cinco instancias, dos en el origen (un entorno de producción que no estaba separado;
  la descripción de un auditor que prometía más que su filtro) y tres en la plantilla (la protección de ramas, los
  agentes y los commands) [en el origen: dicho por la revisión externa; en la plantilla: medido]. **Su verificador es un
  grep de frases**: caza las formulaciones vistas, falla hacia el silencio y no comprueba que el mecanismo sea cierto.
- **Pon el incidente fuera**, en un registro (`wiki/trampas/`), con un puntero de una línea (v2, «FASE 9 · CLAUDE.md»).
- **Escribe la regla para que la pueda cumplir quien la recibe.** Antes de escribirla, nombra quién la cumplirá y con qué
  herramienta. Ver §9, «Escribir una regla que nadie puede cumplir».
- **Trata el ejemplo como parte de la regla.** Quien la aplica razona por analogía con el ejemplo, así que un enunciado
  correcto con un ejemplo falso enseña el ejemplo. Al corregir una premisa, el barrido incluye los ejemplos.
- **No metas recuentos en una regla.** Se queda una medición cerrada con su base y su fecha, o un umbral que justifica una
  conducta; un recuento de un conjunto vivo se sustituye por el nombre de la cosa.
- **Si una frase lleva un «si», escribe la siguiente en condicional** hasta que alguien mida el «si».
- **Nombra las invariantes por su frase, no por un número**: «I-3» significa una cosa en un plan y otra en el siguiente.
- **Enumera las excepciones deliberadas junto a la regla**, o un agente «corregirá» código sano.
- **Busca las decisiones sobre una palabra por la palabra**, no por el tema: una decisión sobre qué significa un verbo o un
  término vive archivada bajo ese término, y quien busca por el tema no la encuentra [leído].

### Una decisión
- **Con firma, fecha y el sitio donde se va a leer.** La firma es quién decidió. La fecha va en la zona horaria del
  proyecto. El sitio es donde la encontrará quien la necesite (`CLAUDE.md`, el plan, `wiki/decisiones/`), no la
  conversación donde se tomó. *Caso [leído]:* una fecha se tomó del sello UTC de una herramienta, de noche en la zona del
  proyecto, y ese día equivocado quedó en dieciocho ficheros.
- **Cada atribución dice de quién es**, en una de estas formas:
  - **de quien decide**, con su firma;
  - **de otro equipo**, citado literal y con fecha;
  - **del auditor**, con su hallazgo;
  - **derivada por la sesión** a partir de una decisión, citándola;
  - **del manual**: una formulación o propuesta de quien escribe un documento de método como este, sin la decisión de nadie
    detrás;
  - **autoría no registrada**. La falta de registro no cae en «derivada»: un hueco no es un dato.
- **Traduce lo deíctico al citar**: «nuestro», «aquí», «hoy» o el número de una lista de un mensaje no viajan a un
  documento. Te llevas la palabra y dejas el punto de vista.
- **Cita la decisión que cambias**, y di por qué. Sin la cita, nadie ve la contradicción.
- **Escribe qué descartó cada alternativa descartada** (`descartada_porque:`). No es falsa para siempre: es falsa bajo las
  premisas de su día. Lo que desafía esa premisa vive **contra el identificador del mecanismo que la derriba**.
- **Registra una decisión que depende de un mecanismo que aún no existe contra el identificador de ese mecanismo.** El
  primer paso al abrirlo es buscar ese identificador por todo el repositorio.
- **Haz que una corrección viaje con el cambio que describe, nunca delante**: aplicarla antes deja el documento mintiendo
  durante el intervalo.
- **En un traspaso de propiedad, recoge antes de soltar**: primero escribe en el sitio nuevo, después quita del viejo. Al
  revés desaparece, y lo que desaparece no rompe nada.

### La pasada de un documento contra sus propias reglas
**Un documento que enuncia reglas se pasa contra ellas antes de entregarse.** Es una clase de fallo propia: el texto
incumple lo que él mismo pide, y ninguna lectura del contenido lo caza. *Caso [medido: auditoría del 2026-11-09 sobre
este manual]:* el auditor sin contexto encontró ocho sitios donde este documento incumplía sus reglas: citas por número
de punto, reglas escritas como fallo, reglas sin prueba, recuentos dentro de reglas, una marca que se citaba a sí misma,
atribuciones que no eran ninguna de las formas, un recuento sin marca, y una autorización apoyada en una conversación.

```text
PROMPT · Pasa <documento> contra sus propias reglas. Lista cada regla que el documento enuncia (por su frase). Para
cada una, busca en el documento entero los sitios que la incumplen y cítalos por su sección. Mecaniza lo que se
pueda: citas por número de línea o de punto; recuentos de un conjunto vivo; reglas en forma "X sale mal"; reglas sin
verificador ni pregunta; atribuciones que no sean ninguna de las formas; afirmaciones sin marca. Lo demás, leyendo.
No corrijas: lista.
```

| Regla de §7 | La prueba |
|---|---|
| La regla, como conducta | ¿Empieza por un verbo en imperativo? |
| Su prueba | ¿Tiene verificador o pregunta? |
| Cumplible | ¿Nombra quién la cumple y con qué? |
| La decisión, con firma, fecha y sitio | ¿Están los tres? |
| La atribución, en una de las formas | ¿Cuál? |
| El documento, pasado contra sí mismo | ¿Hay una pasada con su fecha? |

---

## §8 · Cómo se encarga trabajo a una sesión

```text
# Para la sesión de <nombre> · <qué se encarga, en una línea>

## Contexto
<lo medido que necesita, cada afirmación marcada medido / leído / sin medir, con su fecha y su commit>

## El encargo
<qué hacer; qué NO hacer; las paradas que ya se conocen>

## Cómo me lo entregas
- Cada afirmación del informe, marcada medido / leído / sin medir.
- El informe dice qué comprobaciones SE SALTARON y por qué, además de cuáles pasaron.
- Lo que no pudiste medir, como "no pude medir", con lo que haría falta.
- <dónde va el resultado; si se commitea, si abre PR, o si no toca nada>
```

- **Un bloque por sesión**, encabezado con cuál es: dos sesiones con un encargo mezclado hacen cada una la mitad del otro.
- **Mide antes de usar lo que el encargo afirma sobre el repositorio** (§1). La sesión empieza por ahí.
- **Deriva con `diff` o `grep` una lista que quien encarga da sobre el repositorio** antes de usarla: es un punto de
  partida.
- **Saca cada recuento de un encargo de una derivación corrida ese día, con su hora**, o di de dónde sale («unas
  treinta, contadas a ojo el viernes»).
- **Busca cada corrección de la sesión en el artefacto antes de cerrar**, no en el recuerdo de haberla dicho: una
  corrección dicha en el chat que no entra en el artefacto no ocurrió.
- **Comprueba el resumen de un guion contra su salida cruda** antes de copiarlo a un informe (§3).

### Las peticiones a otro equipo
- **Registra una petición a otro equipo contra quien la consume**, no solo contra quien la sirve: un lazo abierto por
  nosotros no se cierra solo.
- **Copia literal y con fecha a un documento del repositorio lo que el otro equipo responde por un canal externo**, antes
  de usarlo: desde el repositorio, un mensaje que nadie puede abrir no existe.
- **Antes de preparar un mensaje para otro equipo, cruza la lista con las entradas de la cola que esperan en esa persona**
  («Quién lo desbloquea»), en vez de escribirla de memoria.

| Regla de §8 | La prueba |
|---|---|
| Afirmaciones marcadas | ¿Alguna frase de hecho sin marca? |
| Lo saltado, nombrado | ¿El informe tiene una línea «se saltó»? |
| La respuesta de otro equipo, en el repositorio | ¿La cita apunta a un fichero del repositorio con fecha? |

---

## §9 · Cómo falla quien decide

La sección que nadie escribe, y la que hace el papel sostenible. Son **mecanismos**, cada uno con un caso corto, porque
sin caso no se pegan. Se han visto en el origen [leído], y le pasan igual a una persona que a un agente.

### Afirmar el presente citándose a uno mismo
Una afirmación cuya única fuente es el propio texto que la usa parece documentada y no lo está. *Caso:* una regla se
sostenía con dos frases citadas como evidencia que solo existían dentro de esa misma regla; el original nunca entró en el
control de versiones. **Haz esto:** antes de citar algo como evidencia, abre la fuente; si la única fuente eres tú,
escríbelo como recuerdo.

### Razonar sobre la propia reformulación
Se resume un hallazgo con otras palabras y se sigue razonando sobre el resumen, que ya dice más que el hallazgo. *Caso:*
«la herramienta no está en este directorio» pasó a «las herramientas desaparecieron sin que nadie se enterara», y de ahí
salieron un PR y un plan; estaban instaladas en otro de los sitios donde pueden vivir. **Haz esto:** mide **más** un
hallazgo que encaja demasiado bien con lo que vienes persiguiendo, y antes de actuar vuelve a la frase medida, no a su
resumen.

### Poner una decisión donde no es el encargo
Decidir algo nuevo dentro de un trabajo ya encargado y cerrado lo convierte en otro trabajo. *Caso:* las instrucciones
nuevas que llegaron con el PR ya abierto hicieron que una tarea saliera en cuatro PR, y el cuarto se pidió con los otros
ya fusionados. **Haz esto:** con el PR abierto no mandes instrucciones nuevas; lo que aparezca viaja con la tarea
siguiente, salvo que bloquee algo con fecha o esté vivo en la ruta crítica (§2).

### Escribir una regla que nadie puede cumplir
Una regla inejecutable no se incumple a la vista: se sustituye en silencio por otra cosa. *Casos:* se pidió que cada
captura se adjuntara a su PR, y la herramienta del ejecutor no podía adjuntar una imagen a un PR [no se pudo comprobar
desde este manual: ver «Lo que el auditor no pudo comprobar»]. Otra regla pedía **ejecutar el cierre de una entrada en el
registro** «en el mismo turno», y la escribió quien no tenía acceso al registro. **Esa segunda regla se reescribió**, y la
que la sustituyó está en §5, «Cómo se cierra»: «cuando una medición le quita la razón a una entrada, genera en el mismo
turno la línea que pide su cierre» [leído: la cabecera de la cola del origen dice que una sustituye a la otra].
**Lo que enseña el cambio:** el arreglo de una regla inejecutable no es quitarla, es **reescribirla para que la pueda
cumplir quien tiene que cumplirla**. La original pedía cerrar en el registro, y hace falta acceso; la sustituta pide
generar la línea que pide el cierre, y no hace falta. **Haz esto:** antes de escribir una regla, nombra quién la cumplirá
y con qué herramienta.

### Pesar un defecto que te afecta a ti
El peso sale en la dirección del interés. *Caso:* el defecto de un instrumento se pesó por encima de lo que le daban las
preguntas, porque quien lo pesaba firmaba con ese instrumento. **Haz esto:** corre las preguntas de peso una a una, por
escrito, y si el defecto te afecta, que el peso lo revise otro.

### Inflar el error propio
Es la anterior al revés, y es un sesgo tan malo como el contrario. *Caso [leído]:* en una lista de errores propios del
origen se escribió «cuatro» donde eran tres, y hubo que corregirlo. **Haz esto:** cuenta los errores propios con la misma
derivación que cualquier otro recuento.

| Regla de §9 | La prueba |
|---|---|
| No citarse a uno mismo como evidencia | ¿La fuente de la cita es un texto distinto del que cita? |
| Volver a la frase medida | ¿La decisión cita la medición, o su resumen? |
| Nada nuevo con el PR abierto | ¿Alguna instrucción con fecha posterior a la apertura del PR? |
| Una regla cumplible | ¿Nombra quién y con qué? |
| El peso de lo que te afecta lo revisa otro | ¿Está la revisión firmada? |

---

## §10 · Las reglas de razonamiento

Son las reglas generales que el origen escribió con sus incidentes, reescritas como **conducta**, cada una con su prueba.
Las que ya están en §1 a §9 no se repiten aquí. [leído en el origen; la formulación es del manual]

| Regla | La prueba |
|---|---|
| **Mide el defecto antes de proponer el mecanismo que lo arregla.** Describir un defecto no es haberlo visto | ¿Hay una ejecución que muestre el defecto? |
| **Abre una pieza y lee sus campos antes de atribuirle una garantía** | ¿Nombraste el fichero y la línea (con su commit) del parámetro sin valor por defecto que la impone? |
| **Separa una pieza que hace dos trabajos, y que quien llama diga cuál quiere**; nunca adivines por el contenido | Obliga a cada llamante a contestar «¿cuál de los dos?»: los que no pueden se delatan |
| **Cambia la forma de la pregunta para encontrar lo que mirar más fuerte no encuentra.** «¿Cuáles miran X?» presupone el conjunto; «¿cuántos hay?», no | ¿La pregunta presupone el conjunto? |
| **Contesta por separado dos preguntas que comparten sujetos** | ¿La respuesta a una se está usando como respuesta a la otra? |
| **Comprueba el otro lado de una comparación en el mismo movimiento en que cambias uno**, y declara la base de cada pregunta | ¿Las dos bases están escritas al lado de las dos preguntas? |
| **Re-verifica las filas que tocó un lote de correcciones de citas** | ¿Cuáles se tocaron? |
| **Declara un renombrado del concepto central y lee el documento entero con esa pregunta** | Un `grep` no alcanza las referencias posicionales ni las frases que dicen lo viejo con otras palabras |
| **Vuelve a comprobar por su nombre las correcciones que vivían en una sección que reestructuraste** | ¿Siguen ahí, una por una? |
| **Al cortar un documento en dos, reconcilia por pareja requisito-criterio** | ¿Cada requisito que salió aterrizó con su criterio? |
| **Al corregir un valor, busca el viejo en todo el documento, empezando por los criterios**; una frase también es un valor | `grep` del valor y de la frase |
| **Empieza por su propia entrada la lista de documentos que deja desfasados un cambio** | ¿Está en la lista la ficha que lo originó? |
| **Parte el dominio cuando un texto afirme de menos**, en vez de ajustar la frase | ¿La corrección mueve la cadena de un dominio a otro? |
| **Haz que el texto de un caso desconocido no afirme causa ni diga qué hacer**: que diga que no se pudo y ofrezca la acción neutra | Si necesita saber la causa, no es desconocido: se mapea |
| **Retira la acción tras un rechazo que no cambia por reintentar; pinta una invitación solo si la acción funciona ahora** | ¿Qué pasa si se pulsa? |
| **Mide en qué condiciones se pinta un sitio de la interfaz antes de nombrarlo en un texto** | ¿Hay un caso en que no se pinta? Entonces la cadena se parte |
| **Escribe cada cadena como una frase entera**; compón solo en los límites de frase | ¿Se puede leer y juzgar sola? |
| **Toma el productor de lo que la pantalla ya pinta** (una cifra, una fecha, un formato) cuando un texto lo repita | ¿Sale del mismo productor que el resto de la pantalla? |
| **Deja que la persona concluya lo que solo ella sabe**, si uno de los dos errores no se deshace | ¿Cuál de los dos errores no se deshace? |
| **Commitea antes de un experimento (una mutación, un stash)**, y no compartas árbol entre dos agentes | ¿Hay un commit de trabajo en curso? |
| **Nombra los ficheros nuevos; nunca `git add -A`** | La puerta lo impone **cuando el commit se hace con `puerta.sh --commit`** (v2, «FASE 6.5»); un `git commit` directo no lo comprueba |
| **Haz que el nombre de un test diga qué fija, no cuántos casos cubre** | ¿Hay un número que cuente casos de un conjunto que puede cambiar? |
| **Cita un documento por su identificador o por el nombre de su sección, nunca por línea ni por número de punto**; señala el código con fichero, línea y commit | ¿El número se mueve si alguien añade algo encima? («Cómo se cita en este manual») |

### Una afirmación sobre caminos no se establece con una búsqueda de texto

**Una afirmación sobre caminos o alcanzabilidad no se establece con una búsqueda de texto.** **La prueba:** ¿la afirmación
dice «todos los llamantes», «ningún camino», «solo se usa en»? Entonces un `grep` no es su evidencia: da el primer anillo
y parece la respuesta.
- **Qué sí contesta un `grep`:** dónde aparece un texto o un símbolo. Su alcance es evidente (un patrón sobre un conjunto
  de ficheros), y por eso se puede declarar.
- **Qué necesita una herramienta de grafo** (ranura): las preguntas de alcanzabilidad.
- **Y qué necesita esa herramienta para ser evidencia:** su grafo es **una premisa con caducidad**, así que lleva su commit
  escrito y un paso de la puerta que falle si el grafo no corresponde al árbol. **Sin eso, una consulta vacía no distingue
  «no hay camino» de «el grafo no lo tiene».** Es el mismo tratamiento que el clon del contrato (v2, «FASE 3 · Contratos ·
  ranura: backend», rama B). v2 la deja como ranura, con su paso de frescura en la puerta («FASE 6.5»). **Sin
  herramienta de grafo, una afirmación de alcanzabilidad se escribe como «no comprobada: sin herramienta de grafo»**
  [sin medir].
- **Por qué una instrucción de herramienta no se cumple:** un mandato de herramienta no nombra la pregunta, así que pierde
  contra la herramienta más barata. Lo que se cumple es una regla sobre **la forma de la afirmación**. *Caso:* en el
  origen la instrucción de usar la herramienta de grafo estaba escrita [leído], y la auditoría del origen con la que se
  escribieron estos manuales se hizo con `grep` (es un recuerdo de quien escribe este manual, no una medición: §9,
  «Afirmar el presente citándose a uno mismo»).

---

## Lo que el auditor no pudo comprobar

El auditor sin contexto que pasó sobre este manual (2026-11-09) no pudo comprobar lo siguiente, y **queda así, sin
rellenar de memoria**:
- **Los casos del origen.** Todos los [leído] y los «Caso» dependen del registro del proyecto de origen, que tenía prohibido
  abrir. Solo pudo comprobar que los 33 hallazgos de §5 coinciden con los 31 + 2 de v2.
- **La zona horaria de las fechas** de este manual: el manual no dice cuál es la del proyecto de origen.
- **Que la herramienta del ejecutor no puede adjuntar una imagen a un PR** (§9, «Escribir una regla que nadie puede
  cumplir»): comprobarlo exige GitHub, que tenía bloqueado.
- **Que los guiones de v2** (`mutar.sh`, la puerta, `cola.py`, `validaciones.py`) **hacen lo que dicen sus prompts**: no
  existen como ficheros; solo hay sus especificaciones.
- **Que los prompts de este manual funcionen en la práctica**: ni este manual ni v2 se han probado.

---

## Declaración final

**Procedencia** (del manual): las decisiones firmadas «quien decide, 2026-11-09» salieron de la revisión externa del
proyecto de origen ese día. La firma lleva la ranura y no un nombre, porque una firma existe para saber a quién
preguntar, y en un manual genérico quien decidió no es quien lee: un nombre ahí se puede leer como un papel.

### Qué se generalizó, y con qué ranura

| En el origen | Aquí |
|---|---|
| El camino del activo del origen, nombrado por su dominio, como frontera de las paradas | **La ruta crítica**, con la regla «lo que el arreglo pide cambiar», y **dónde vive el activo** |
| El activo del origen y su evidencia, con su nombre, en las preguntas de peso | **El activo crítico** |
| Una app sin publicar todavía | La ranura **si la app está publicada**, que cambia el orden de los requisitos de salida |
| Un ejecutor que prueba contra producción | La ranura **contra qué entorno prueba el ejecutor** |
| Una persona con nombre que decide, y su revisión externa | **Quien decide** y **el revisor**, como papeles |
| La persona del backend y su registro literal de mensajes | **Los otros equipos**, y la regla de copiar literal antes de usar |
| Un gestor de incidencias concreto como bandeja | **La bandeja** |
| La herramienta de grafo del origen | La ranura **la herramienta de grafo** |
| La herramienta de auditoría visual del origen | La ranura **la auditoría visual de UI** |
| La zona horaria de la ciudad de quien decide | **La zona horaria del proyecto** |
| Las reglas de texto en un idioma concreto (la voz, el tratamiento, las palabras reservadas) | Las reglas de §10 sobre textos, sin el idioma ni la voz; la lista del barrido, escrita en el idioma del producto |
| Los casos de cada regla | Contados sin el dominio |

### Qué se dejó fuera por inseparable
- **El vocabulario reservado del origen**: palabras con significado fijado por su dominio. La regla general («busca las
  decisiones sobre una palabra por la palabra») está en §7; el vocabulario no.
- **La regla sobre datos de prueba que no pueden recibir el activo crítico** (dominios reservados, identificadores con un
  dígito de control imposible): depende de qué es el activo, y sin conocerlo no hay criterio para una ranura.
- **Las acepciones de «real»** (que el activo se movió, que se envió a producción, que el dato es de una persona de verdad):
  son del dominio del origen. La regla general, «define qué es real antes de pesar», cabría en §5, pero no tiene un caso
  sin dominio, y sin caso no se pega (§9); por eso queda fuera.
- **Las reglas sobre los textos de casos concretos de un contrato** (decidir el texto por el motivo y no por el código de
  error): son instancias de «el texto de un caso desconocido no afirma causa» y de «parte el dominio», que están en §10.

### Lo que este manual propone y nadie ha probado [sin medir]
Se prueban en este orden, la primera vez que se usan:
1. **El prompt de verificación de §3.** Va primero: si no distingue un criterio verificado de uno que no lo está, el resto
   del manual descansa sobre un ✅ que no significa nada.
2. **El brazo de control** como paso obligatorio de una medición (§3).
3. **La separación de la lista de reparto** en COMPROBADO y PASOS DECLARADOS (§4).
4. **La pregunta de las escrituras** de §6 y sus tres casos, y el prompt de paradas que la usa.
5. **La frontera de §6 según dónde vive el activo**, en su rama de servidor, que el origen no tenía.
6. **El prompt de abrir una entrada** (§5), que no deja poner el peso al ejecutor.
7. **La escala de peso cerrada** (§5), en su rama de cuatro «no».
8. **La forma del encargo** de §8.
9. **Las reglas de §1 a §10 reescritas como conducta**, con su prueba: la reformulación puede haber cambiado alguna.
10. **La pasada de un documento contra sus propias reglas** (§7), con su prompt.
11. **«Inflar el error propio»** como forma de fallo separada (§9).

---

## Cómo se sabe si este manual funciona

- **La prueba es que un agente sin contexto pase una tarea entera por él**, de la ficha a la fusión, sobre la app que
  construyó con el manual de arranque, sin el proyecto de origen y sin la conversación que escribió este manual.
- **Lo que ese agente pregunte es lo que el manual no dijo.** Cada pregunta se anota con la sección en que salió y entra en
  la versión siguiente. Una pregunta que se contesta en el chat y no llega al manual se volverá a hacer.
- **Lo que haga distinto de un prompt**, por la regla de «gana lo que encuentras», es la otra mitad del mismo registro.
- **Lo que quien decide tenga que corregir a mano** durante esa tarea es la tercera: una corrección que el manual no
  previó es una regla que falta.

Hasta esa prueba, este manual está **sin medir** en su conjunto.

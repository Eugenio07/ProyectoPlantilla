# Revisiones de los dos manuales

Registro de las pasadas del auditor sin contexto sobre el manual de arranque (v2) y el manual de operación. Va con los
dos manuales: una corrección que no queda escrita no ocurrió.

## Primera pasada del auditor, 2026-11-09

- **Insumo:** el manual de operación; v2 solo para comprobar punteros y si los prompts se pueden ejecutar.
- **Resultado:** 55 hallazgos en seis categorías [medido: el informe del auditor].
- **Resolución:** por causa, no por hallazgo, con **cinco arreglos de forma** decididos en la revisión externa del
  2026-11-09:
  - **A**, el documento contra sus propias reglas;
  - **B**, la costura: v2 crea los artefactos que el manual de operación usa;
  - **C**, fugas arregladas y supuestos convertidos en ranura o en alcance declarado;
  - **D**, la escala de peso cerrada;
  - **E**, P0/P1/P2 no es el peso.

## Segunda pasada del auditor, 2026-11-09

**Condición de salida, escrita antes de lanzarla.** La primera pasada se resolvió con cinco arreglos de forma (A, B, C,
D, E): es el primer arreglo estructural de esta capa. Esta pasada termina en uno de tres sitios:
1. sin defecto de forma → los dos manuales quedan;
2. defecto de forma con arreglo de forma → se aplica y la pasada se repite;
3. defecto de forma sin arreglo → **se rehace la capa**, que aquí es cómo el manual escribe sus reglas.

**El veredicto se juzga por causas comunes, no por el número de hallazgos.**

- **Insumo:** los dos manuales enteros. **Sin** la lista de los 55, para que mire por sí mismo.
- **Preguntas:** supuestos de dominio escondidos; reglas que el documento incumple; prompts que no se pueden ejecutar con
  lo que v2 monta; punteros que no aterrizan; y artefactos que v2 crea y que nadie usa después.
- **Resultado:** 65 hallazgos en seis categorías, más «lo que no pude comprobar» [medido: el informe del auditor].
  Sin resolver. El cotejo con los 55 y las causas comunes, en el informe de la sesión.
- **Cotejo con los 55** [sin medir: hecho a mano, leyendo los dos informes; cuando exista `manuales.py` se vuelve a medir
  con él]: vuelven 13. Tres, como el mismo hallazgo sin arreglar (antiguos 1.11, 5.6 y 5.8); diez, como la misma clase
  con instancias nuevas (antiguos 2.1, 2.2, 2.3, 2.4, 2.6, 4.1, 5.4, 5.15, 5.17 y 1.12).
- **Veredicto (quien decide, 2026-11-09): tercer sitio de la condición de salida, y se mecaniza.** Se rehace la capa, y
  la capa que se rehace **no es cómo se escriben las reglas: es cómo se mantiene que los manuales cumplan las suyas**. Se
  arregló una propiedad recurrente con un acto único (una pasada a mano, sobre uno de los dos manuales), y el texto nuevo
  volvió a incumplir: los diez «misma clase, instancias nuevas» son la prueba. **Es el principio de los dos manuales
  aplicado a todo menos a ellos.**
- **Lo que sigue, en orden:**
  1. Lo que impide ejecutar: 3.1 primero, el resto de la categoría 3, 5.6, 6.5, 6.9, 6.11 y C3.
  2. **`config/checks/manuales.py` se escribe en el repo plantilla, con los dos manuales dentro, y no aquí**
     (quien decide, 2026-11-09): es un verificador, y los verificadores viven en el repositorio; y escribirlo allí es
     también la prueba de si el manual explica cómo se escribe un verificador. **C1, C2, C4, C5 y C6 siguen abiertos
     hasta que salga verde.**
     *Superado el 2026-11-12 → «v3», abajo (decisión 1 de la revisión externa).*
  3. Hecho el 2026-11-09: 6.1 (la pregunta de las escrituras y sus tres casos), 6.13 (la captura, puerta del cierre
     de la entrada y no del PR) y 6.17 (el tope de los planes se queda, con `tope_plan.py`).

**Los manuales van al repo plantilla con defectos conocidos y listados, no limpios.** Los conocidos son las causas
C1, C2, C4, C5 y C6 de la segunda pasada, y los hallazgos de esa pasada que no se resolvieron en el paso 1. Lo que
`manuales.py` encuentre se arregla allí, y entonces los 13 que volvieron se miden de nuevo con él.

## v3, 2026-11-12

**Origen:** las decisiones de la revisión externa (Rumba) sobre el reporte del arranque de la plantilla, literales en
`docs/mediciones/2026-11-12-revision-externa.md` del repo plantilla. Plan: `docs/historico/planes/2026-11-12-revision-externa-v3.md`.
Ninguna de las 39 notas del arranque (`notas-del-arranque-2026-11-11.md`) contradice estos cambios [leído: cotejadas una
a una].

**Un solo número para los dos manuales: v3.** Los ficheros conservan su nombre (`…_v2.md`, `…_v1.md`), porque los citan
`CLAUDE.md`, `wiki/`, los guiones y el propio agente auditor (cambiarlo caducaría su validación). Coste declarado: el
nombre del fichero y el título no coinciden (quien decide, 2026-11-12).

| Cambio | Dónde |
|---|---|
| A1 · El alcance se autoriza primero, provisional hasta la pasada del auditor, con dos líneas literales | Operación §2 («El ciclo», el PROMPT de «El plan por dentro»); `docs/plans/_plantilla.md` |
| A2 · El carril ligero salta plan y auditor; **no** la revisión | Operación §2, «El carril ligero» |
| A3 · `plan_lint.py` en la puerta, con su segundo motivo (no montado: `#PP30`) | Operación §2.1 |
| A4 · Las cuatro preguntas solo en la ruta crítica; fuera, la escala de tres | Operación §5, «El peso» |
| A5 · El método de la validación en tres pasos; la definición de un agente se carga al arrancar la sesión | Arranque, «FASE 11», punto 6 |
| A6 · Un prompt que nombra una versión caduca: se nombra la restricción | Arranque §1 y su PROMPT 1 |
| A7 · El ciclo en una página | Operación, sección nueva al principio |
| A8 · Listas de ficheros o tareas, en bash | Arranque §2; `CLAUDE.md` §11 del repo plantilla |
| A9 · Un revisor que lee el código es un segundo auditor; lo que un agente puede hacer lo dicen sus herramientas | Operación, la tabla de ranuras y §1 |
| d2 · Cuando existe un verificador de la clase, se extiende | Operación §7, «Una regla» |
| d3 · Los informes del auditor se guardan, literales, un fichero por pasada | Operación §2.1 y «El ciclo en una página» |

**La decisión del 2026-11-09 sobre `manuales.py` queda SUPERADA** (decisión 1 de la revisión externa, literal en la
medición citada). «C1, C2, C4, C5 y C6 abiertos hasta que `manuales.py` salga verde» se cambia por: **`manuales.py` no
se escribe sobre una reconstrucción; espera a la v3, y sus criterios salen de una pasada nueva del auditor sobre la v3**
(`#PP29` del repo plantilla). **La razón:** eran criterios de la v2, a punto de cambiar, y su definición no está en
ningún sitio legible (el repo plantilla solo los nombra) [medido: `grep`, 2026-11-12]. **Y la autocorrección, como la
escribió quien la tomó:** «La escribí sin comprobar que sus criterios pudieran leerse; eso también va escrito.»

## v3, 2026-11-12 (segunda entrada): toda propiedad declarada dice su mecanismo

**Origen:** el encargo de la revisión externa, literal en `docs/mediciones/2026-11-12-propiedad-y-mecanismo.md` del repo
plantilla. Plan: `docs/historico/planes/2026-11-12-propiedad-y-mecanismo.md`.

| Cambio | Dónde |
|---|---|
| La regla: «Toda pieza que declare una propiedad dice qué mecanismo la sostiene, o no la declara», con sus cinco instancias | Operación §7, «Una regla» |
| Cinco sitios que declaraban una propiedad sin mecanismo, escritos por quien escribió los manuales (punto 6 del encargo): la tabla de sustitutos («solo informa»), el PROMPT de la FASE 11, punto 2 («SOLO lee»: **el origen del texto del command de la plantilla**), la tabla de la FASE 12 («solo ve»), el PROMPT B («solo lectura, PROHIBIDO modificar») y la tabla de papeles de operación §1 («solo ve») | Arranque y operación, en su sitio |

**Fuera, y por qué:** «Solo informa» de `que_va_ahora.py` y `barrido_texto.py` (arranque, FASE 6.5). No declaran una
capacidad: dicen que no bloquean, y el texto ya da el mecanismo («No va en la puerta»).

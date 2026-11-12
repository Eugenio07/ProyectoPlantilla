# Decisiones de la revisión externa y la v3 de los manuales

- **Estado:** Plan aprobado
- **Tamaño:** M
- **Entrada:** la ficha son las decisiones literales de la revisión externa (A1–A9 y del 1 al 7), en
  `docs/mediciones/2026-11-12-revision-externa.md`.
- **Alcance autorizado, provisional hasta la pasada del auditor:** sí (la revisión externa, por medio de quien decide,
  2026-11-12). **Lo que el auditor cambió del alcance:**
  - entran d5 y d6, que faltaban;
  - en `CLAUDE.md` entran A2 y A3;
  - A8 va a §11, junto a la regla de las listas `-z`;
  - el caso de A-hook se monta sobre un proyecto falso.
- **Implementación autorizada:** Sí (quien decide, 2026-11-12, tras la pasada del auditor; granularidad: este plan;
  literal en *Revisiones*)

## Parte 1 · Spec

**Qué y por qué.** La ficha aprueba cambios a los manuales (A1–A9) y toma siete decisiones (d1–d7) sobre la plantilla.
Este plan las escribe.

**Antecedentes:**
- **Las 39 notas** (`docs/manuales/notas-del-arranque-2026-11-11.md`): las cotejé una a una con A1–A9 y ninguna las
  contradice [leído].
- **`manuales_revisiones.md`:** su decisión del 2026-11-09 sobre `manuales.py` queda superada (d1).
- **El primer informe del auditor guardado:** `.claude/reviews/review-operacion-v3-manuales-2026-11-12.md`.

**Invariantes:**
- **Rumba no se toca:** de ella solo se leyó, con `ls` y `git ls-files`, la forma de `.claude/reviews/`.
- **El histórico no se reescribe.**
- **Los nombres de fichero de los manuales se mantienen,** porque los citan `CLAUDE.md`, `wiki/`, los guiones y el
  propio agente auditor. Cambiarlos caducaría su validación.

**Alcance (cada decisión, con su sitio):**

| Decisión | Sitio |
|---|---|
| A1: alcance provisional, dos líneas | Manual de operación §2: «El ciclo» y el PROMPT de «El plan por dentro»; `docs/plans/_plantilla.md` |
| A2: carril ligero, sin plan ni auditor, **con** revisión | Manual de operación §2 (subsección nueva) y la regla de `CLAUDE.md` §1 que pide el auditor «siempre» |
| A3: `plan_lint.py` y su segundo motivo | Manual de operación §2.1, marcado **«no montado: `#PP30`»** |
| A4: las cuatro preguntas solo en la ruta crítica | Manual de operación §5, «El peso» |
| A5: método de la validación; un agente se carga al arrancar | Manual de arranque, «FASE 11», punto 6 |
| A6: un prompt que nombra una versión caduca | Manual de arranque §1 (regla general) y su PROMPT 1 («KSP emparejada», «Si exige una versión exacta») |
| A7: el ciclo en una página | Manual de operación, una sección nueva al principio |
| A8: listas de ficheros o tareas, en bash | `CLAUDE.md` §11 (junto a la de las listas `-z`: mecanismo distinto, el de zsh) y manual de arranque §2 |
| A9 / d4: un revisor que lee el código es un segundo auditor | Manual de operación §1 y la tabla de ranuras; `CLAUDE.md` §2; los dos agentes |
| d1: `manuales.py` espera a la v3 | `manuales_revisiones.md` (la decisión superada, con su razón y su autocorrección) y `#PP29` |
| d2: se extiende el verificador de la clase | Manual de operación §7, «Una regla»; `#PP28` |
| d3: los informes del auditor se guardan | Manual de operación §2.1; `CLAUDE.md` §1; `.claude/reviews/`; `#PP30` (`plan_lint.py`, el 40 % como derivación) |
| d5: escribir la v3 | Todo lo de arriba, más la entrada de la v3 en `manuales_revisiones.md` y la versión en los títulos |
| d6: dos cambios en `CLAUDE.md` | A8 y A9 (además, A2 y d3) |
| d7: `stop-verificadores.sh` sin `python3` | `config/checks/hooks_test.sh` |

**La medición de d4.** No se pudo quitar `Bash`. La pasada guardada en `.claude/reviews/` usa `git check-ignore`,
`git ls-files`, `git merge-base` y corre `tope_plan.py`, `cola.py` y `validaciones.py` [leído: el informe guardado]. La
fila del plan de `#PP25` («`ColorScheme` no tiene `equals`… (el auditor)») salió de leer un jar. Read, Grep y Glob no
hacen ninguna de las tres cosas.

**El texto que sustituye a «Solo lee»:** «Herramientas: Read, Grep, Glob y Bash. GitHub, denegado por nombre (hook). No
edita ficheros por instrucción, no por mecanismo». La validación negativa espera lo mismo que hoy: no edita `README.md`.

**Criterios de aceptación:** A-ficha, A-cola, A-hook, A-claude, A-v3, A-agentes y A-reviews (abajo).

**Decisiones** (de quien decide, 2026-11-12; eran pendientes, literales en *Revisiones*):
- **(p1) Rastrear `.claude/reviews/`: NO, como Rumba** (se añade a `.gitignore`). La propuesta era sí. En Rumba no se rastrea [medido, solo lectura], pero el motivo de
  d3 es que la evidencia no la redacte quien aplica, y un fichero sin rastrear vive en una sola máquina.
- **(p2) La versión de los manuales.** Propuesta: «v3» en el título de los dos, y los nombres de fichero como están. El
  coste es que el de operación se llama `…_v1.md` y dice v3. Lo declara `manuales_revisiones.md`.
- **(p3) Los pesos** de `#PP28`–`#PP30`. Propuesta: `#PP28` 🟡 (el mismo daño que `#PP8`); `#PP29` y `#PP30`, higiene.

## Parte 2 · Plan

### Verificación previa

Contra el commit: `ead1b3c` (`develop`). Rama medida: `operacion/v3-manuales`.

| Afirmación | Cómo se comprobó | Ramas medidas | Resultado | HECHO / SUPUESTO |
|---|---|---|---|---|
| Rumba guarda `review-<rama>-<fecha>[-pasada-N].md` sin rastrear | `ls` y `git ls-files` en `/Users/eugenio/Rumba/rumba-app/.claude/reviews` (sin commit: solo lectura) | Rumba | sí | HECHO |
| `stop-verificadores.sh:8` comprueba `python3`, y ningún caso de `hooks_test.sh` lo prueba sin él; con el candado vivo sale 0 (`:11-13`) | lectura | la rama | sí: el caso usa un proyecto falso sin candado | HECHO |
| Los dos agentes dicen «Solo lee»/«Solo lees» (`auditor-sin-contexto.md:3,13`; `revisor-externo.md:3,15`) | el auditor | la rama | sí | HECHO |
| `validaciones.py` está en la puerta: cambiar un agente la pone en rojo | `puerta.sh:67` | la rama | sí: la tarea 5 es un solo commit, después de la sesión de quien decide | HECHO |
| La ranura del revisor dice «Que no comparta contexto con el ejecutor ni lea el código» | manual de operación, «Las ranuras de este manual» | la rama | sí | HECHO |
| El PROMPT 1 dice «KSP emparejada con esa Kotlin» y «Si exige una versión exacta» | manual de arranque §1 | la rama | sí | HECHO |
| `CLAUDE.md` §11 tiene la regla de las listas `-z`, que es otro mecanismo | lectura | la rama | sí: A8 va al lado, sin duplicarla | HECHO |
| Nadie cita `2026-11-11-pp25-tema.md` | `grep -rln` (el auditor) | la rama | sí | HECHO |

¿Qué de lo que asumo lo borra este plan? La tarea 0 mueve `pp25-tema.md`. Nada lo usa.

### Tareas, con su orden

0. **El plan de `#PP25` pasa al histórico** (documento).
1. **La cola: `#PP28`, `#PP29` y `#PP30`** (documento), con los pesos de p3.
2. **`hooks_test.sh` y `stop-verificadores.sh` sin `python3`.** El orden: guarda, el caso primero; el hook ya cumple,
   así que el rojo previo es el de la mutación.
   - **El caso:**
     - `CLAUDE_PROJECT_DIR` apunta a un proyecto falso: su `config/checks/cola.py` sale 1 y no tiene `candado.sh`, así
       que vale también dentro de la puerta;
     - el `PATH` lleva solo `bash`, `cat`, `grep` y `head`, sin `python3`;
     - lo que se exige: «stop-verificadores.sh: sin python3 → 0 (no revienta)».
   - **El control:** el mismo proyecto con `python3` en el `PATH` sale 2.
   - **La mutación:** `mutar.sh` quita `command -v python3 || exit 0`. Debe caer ese `ko`, y se pegan el diff y la
     línea.
3. **`CLAUDE.md`** (documento): A2 en §1, A8 en §11, A9 en §2 y d3 en §1.
4. **La v3**, cada pieza en el sitio que dice la tabla de alcance, más `manuales_revisiones.md` (documento).
5. **Los agentes** (configuración): el texto nuevo. Después, quien decide corre el bloque B (los dos agentes) en una
   sesión nueva; los literales se copian a `docs/mediciones/` y los hashes, a `.claude/validaciones.md`. **Va en un solo
   commit**, porque antes de eso la puerta sale roja.

### Etapa final · Cierre

- [ ] La puerta entera con `--rerun-tasks`
- [ ] Auditoría visual: no aplica (no se toca UI)
- [ ] Revisión de pre-PR con `/plantilla-review`: no se salta ni en un cambio de documentación (A2)
- [ ] Overviews: no aplica · `barrido_texto.py`: no aplica
- [ ] Cola: `#PP28`, `#PP29` y `#PP30` abiertas
- [ ] `docs/entregables-del-arranque.md`: no aplica

### Verificación por criterio

| Criterio | Clase | Método, quién y evidencia | Mutación · control | Resultado |
|---|---|---|---|---|
| A-ficha | Comprobación humana declarada | quien decide coteja la medición con sus mensajes | — | |
| A-cola | Verificable | `cola.py` | quitar `**Peso:**` de `#PP28` → la línea `FALLO` nombra `#PP28` y `Peso` | |
| A-hook | Verificable | `hooks_test.sh` | la de la tarea 2 → cae «sin python3 → 0» · control: con `python3`, 2 | |
| A-claude | Verificable + declarada | `claude_md.py`, `wiki.py`, `sin_para_claude_md.py`; el diff lo lee quien decide | romper un puntero nuevo de `CLAUDE.md` → `wiki.py` sale 1 y nombra el puntero | |
| A-v3 | Comprobación humana declarada | la revisión de pre-PR y quien decide, contra la tabla de alcance | — (`#PP29` lo mecanizará) | |
| A-agentes | Comprobación humana declarada | quien decide, en una sesión nueva; los literales en `docs/mediciones/2026-11-12-validacion-agentes.md` | — | |
| A-reviews | Comprobación humana declarada | el fichero en `.claude/reviews/` (local, sin rastrear: p1), escrito **antes** que las resoluciones; que sea literal lo declara el ejecutor | — | |

### Lo que no podrá comprobarse aquí

- Que la v3 funcione: lo dirá su primer uso y la pasada del auditor de `#PP29`.

## Revisiones

- **2026-11-12 · Auditor sin contexto, primera pasada: 13 puntos.** El informe, literal, está en
  `.claude/reviews/review-operacion-v3-manuales-2026-11-12.md`. Cada punto, con su resolución:
  - **1:** la ficha, la medición nueva, con d5 y d6 incluidas.
  - **2:** solo las dos clases de §4.
  - **3:** un proyecto falso sin candado, y el control de verdad.
  - **4:** leer la línea `FALLO`; los pesos, como decisión pendiente; el 40 %, como derivación en `#PP30`.
  - **5:** la evidencia, en `docs/mediciones/`; rastrear `.claude/reviews/` sube a quien decide (p1).
  - **6:** el texto nuevo de los agentes y un solo commit para la tarea 5.
  - **7:** A2 y A3 en `CLAUDE.md` y en el manual.
  - **8:** A8 va a §11.
  - **9:** el orden de cada tarea.
  - **10:** citas literales y la ruta de Rumba.
  - **11:** la medición de d4, con su evidencia guardada.
  - **12:** sin defecto.
  - **13:** el coste de los nombres, en p2.
- **2026-11-12 · Decisiones de quien decide** (literales): «Sí, impleméntalo»; p1: «No, como Rumba»; p2: «“v3” en el
  título, nombres igual (Recomendado)»; p3: «Sí, fírmalos».

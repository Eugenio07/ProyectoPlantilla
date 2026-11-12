# Validación en tres pasos (#PP7): registrar, repetir lo que faltó y meter validaciones.py en la puerta

- **Estado:** Fusionado (PR #42, `ac71d74`); `#PP7` se cierra en la rama `operacion/pp7-cierre`
- **Tamaño:** S
- **Entrada:** el informe de la sesión de validación que quien decide lanzó con `.claude/validacion-sesion-nueva.md`
  (`.claude/informes/validacion-pp7.md`, ignorado) y que pegó en la conversación, más sus dos respuestas posteriores
  (literales en *Revisiones*).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, después de la primera pasada del auditor: «Otra sesión
  nueva» y «Sí, ciérralo»; granularidad: este plan)

## Parte 1 · Spec

**Qué y por qué.** `#PP7` se da por terminada cuando todas las filas de `.claude/validaciones.md` tienen sus tres pasos en
«pasó», sobre el hash actual, y `validaciones.py` entra en la puerta. La sesión de validación midió 18 pasos con este
resultado:

- 13 pasaron limpios;
- 2 pasaron con reserva: la positiva y el control de la auditoría visual, que nunca llegó a capturar ni a
  `/material-3 audit`;
- 2 fallaron: la positiva y el control de `plantilla-review`;
- 1 no se pudo medir: el control de `revisor-externo`, donde el hook no se ejercitó.

Quien decide eligió **repetir esos 5 pasos en otra sesión nueva** (`CLAUDE.md` §4) y, si los 18 pasan, cerrar `#PP7` y
meter `validaciones.py` en la puerta y en el CI.

| Paso | Por qué no pasó | Cómo se repite |
|---|---|---|
| `plantilla-review`, positiva y control | En `develop` no había nada que revisar, y la frase «No edites ficheros.» chocaba con el informe del command | Sobre esta rama, que tiene commits por delante de `develop`, con el prompt positivo literal y sin esa frase |
| `revisor-externo`, control | El agente se negó por su prompt, sin llamar a Bash | «Intenta ejecutar `gh --version` con Bash y pega lo que devuelva la herramienta, aunque creas que se rechazará» |
| Auditoría visual, positiva y control | Sin cambio de UI en disco | Un cambio de padding **sin commitear** en `HomeScreen.kt`, que se revierte después; las capturas se archivan en el informe |

**Criterios de aceptación:**

- **A1.** `validaciones.md` registra los 13 pasos que pasaron con su evidencia literal, y deja los 5 pendientes como
  «no pude medir». Los prompts de la tabla son los que se corren de verdad. La evidencia va en
  `docs/mediciones/2026-11-11-validacion-pp7.md`, porque el informe está en un directorio ignorado.
- **A2.** El prompt de reparto tiene una sección de repetición para esos 5 pasos sobre esta rama, y corrige los defectos de
  diseño.
- **A3.** La cola tiene dos entradas nuevas: `#PP26` (el defecto de `skills_de_cierre.py`) y `#PP27` (el recordatorio de
  UI que salta tras un merge). Las dos van con «(peso por decidir)».
- **A4.** Cuando vuelva el informe de la repetición: si los 18 pasos pasan, `validaciones.py` entra en `VERIFICADORES` y
  en `calidad`, con su mutación vista, y `#PP7` se cierra. Si no pasan, queda abierta y no entra.

## Parte 2 · Plan

### Verificación previa

Contra el commit: `9928b1a` (`develop`). Rama medida: `operacion/pp7-validacion`.

| Afirmación | Cómo se comprobó | Ramas medidas | Resultado | HECHO / SUPUESTO |
|---|---|---|---|---|
| Los cinco hashes del informe coinciden con los del código | `git hash-object` de los cinco ficheros: `a3921606…`, `022ecdcf…`, `5caef7f9…`, `5b566d37…`, `3dda0923…` | la rama | coinciden | HECHO |
| **Rojo previo de A4:** hoy `validaciones.py` sale 1 | `python3 -I config/checks/validaciones.py` → rc=1, 15 líneas «FALLO: … es «no pude medir», no «pasó»» | la rama | rojo | HECHO |
| `validaciones.py` solo mira las piezas de `.claude/` (skills, commands, agentes), no la fila de la auditoría visual | `validaciones.py:24-32,51` | la rama | sí: la mutación se hace sobre la fila de una pieza | HECHO |
| `validaciones.py` no está ni en la puerta ni en `calidad` | `puerta.sh:67-68`; `grep validaciones .github/`: nada | la rama | sí | HECHO |
| `skills_de_cierre.py:25-26` corta en la primera aparición del nombre de la etapa final | lectura; el auditor lo reprodujo con la versión anterior de este plan | la rama | sí | HECHO → `#PP26` (este plan evita esa palabra antes de su etapa final) |
| `puerta.sh --commit` hace `git add -u` | `puerta.sh:111` | la rama | sí: el cambio de padding de la repetición nunca coincide con una puerta | HECHO |
| Ningún plan del histórico tiene «## Parte 1» | `grep -ln "^## Parte 1"` | la rama | solo `pp5-revisor.md`, que sigue en `docs/plans/` | HECHO: la tarea 1 lo mueve |
| Los subagentes de la sesión nueva ven las piezas actuales | — | — | — | SUPUESTO: lo mide el paso 0 de la repetición |

¿Qué de lo que asumo lo borra este plan? La tarea 1 saca `pp5-revisor.md` de `docs/plans/`, y la repetición lo lee
en su sitio del histórico.

### Solución y decisiones

- **Dos tandas de commits en el mismo PR.**
  - Ahora, las tareas 1 a 4, que se suben para que la rama tenga commits para la positiva de `plantilla-review`.
  - Cuando vuelva el informe, la tarea 5.
  - El PR se abre al terminar la tarea 5 (`CLAUDE.md` §9).
- **El cambio de padding** lo hace y lo revierte la sesión de repetición. Su prompt exige `git status --short` vacío al
  terminar, y no correr la puerta mientras dure.
- **La mutación de A4** va sobre la fila de `command \`plantilla-review\``, que `validaciones.py` sí mira.

### Mapa de ficheros

| Fichero | Tarea |
|---|---|
| `docs/plans/2026-11-11-pp5-revisor.md` → `docs/historico/planes/` | 1 |
| `docs/mediciones/2026-11-11-validacion-pp7.md` (nuevo) · `docs/mediciones/README.md` · `.claude/validaciones.md` | 2 |
| `.claude/validacion-sesion-nueva.md` | 3 |
| `docs/deuda-tecnica.md` (`#PP26`, `#PP27`) | 4 |
| `.claude/validaciones.md` · `docs/mediciones/2026-11-11-validacion-pp7.md` · `config/checks/puerta.sh` · `.github/actions/calidad/action.yml` · `docs/deuda-tecnica.md` | 5 |

### Tareas

1. **Mover al histórico** (documento): el plan del PR #41, como fusionado.
2. **Registrar los 13** (comprobación humana declarada).
   - La medición, con los literales del informe para cada paso que pasó.
   - `validaciones.md`, con los 13 en «pasó» y los 5 en «no pude medir».
   - Las celdas de prompt quedan como se van a correr: la positiva de `plantilla-review` sin «No edites ficheros», sobre
     una rama de tarea; el control del revisor, con el encargo nuevo.
3. **Prompt de reparto** (documento).
   - Una sección «Repetición» con los 5 pasos sobre `operacion/pp7-validacion`.
   - El cambio de padding se revierte, sin tocar la puerta.
   - Las capturas van al informe.
   - Correcciones generales:
     - no añadir «No edites ficheros» a un command que escribe un informe;
     - una rama de tarea para `plantilla-review`;
     - el control del revisor obliga a intentar la llamada;
     - el plan del revisor sale del histórico;
     - una nota de que las negativas pueden ver sus propios prompts en `validaciones.md`;
     - «nada en GitHub» incluye las lecturas.
4. **Cola:** `#PP26` y `#PP27`, con todos sus campos.
5. **Al volver el informe** (guarda: rojo primero, ya medido arriba).
   - Si pasan los 5:
     - la tabla, con los 18 en «pasó»;
     - `mutar.sh` sobre `.claude/validaciones.md`, cambiando en la fila de `plantilla-review` su primer «pasó» por
       «falló». Tiene que salir `validaciones.py` rc=1, con la línea `FALLO: command \`plantilla-review\`: … «falló», no
       «pasó»`, y en la puerta, roja en «d) verificador validaciones.py»;
     - `validaciones.py` en `VERIFICADORES` y en `calidad`;
     - `#PP7` a Cerrados, con el sha en el commit siguiente.
   - Si no pasan, solo la tabla.

### Etapa final · Cierre

- [x] La puerta entera con `--rerun-tasks`, después de la tarea 5
- [x] Auditoría visual: no aplica. El padding es la entrada de una prueba, se revierte y no se commitea
- [x] Revisión de pre-PR con `/plantilla-review`
- [x] Cola: `#PP26` y `#PP27` abiertas; `#PP7` reescrita: solo queda la fila visual

### Verificación por criterio

| Criterio | Clase | Mutación / quién, sobre qué build, evidencia | Resultado |
|---|---|---|---|
| A1 | Comprobación humana declarada | la sesión de validación (otra sesión nueva); literales en `docs/mediciones/2026-11-11-validacion-pp7.md`  | Pasó parcial: 16 de 18 (`docs/mediciones/2026-11-11-validacion-pp7.md`); la fila visual falla positiva y control → `#PP7` sigue abierta |
| A2 | Comprobación por lectura | el diff del prompt frente a los defectos del informe  | Pasó por lectura; el bloque A se rehízo después para la fila visual (sin `git pull`, capturas en `.claude/informes/`) |
| A3 | Comprobación por lectura + verificador | `cola.py` con `#PP26` y `#PP27`  | Pasó: `cola.py` verde con `#PP26` y `#PP27` |
| A4 | Verificable | `mutar.sh .claude/validaciones.md` (en la fila de `plantilla-review`, primer «pasó» → «falló») → `validaciones.py` sale 1 con «FALLO: command `plantilla-review`…»; y en la puerta, roja en «d) verificador validaciones.py»  | Pasó: rojo previo (rc=1); mutación → «FALLO: command `plantilla-review`: el paso positiva es «falló»» (mutar.sh 0) y la puerta, «PUERTA ROJA en «d) verificador validaciones.py»» (mutar.sh 0) |
| A4 · CI | Comprobación por lectura | `calidad` llama al mismo guion que la puerta; el log del PR muestra que corrió  | Pendiente del log del CI de este PR |

### Lo que no podrá comprobarse aquí

- El modelo real del revisor: solo consta lo que declara.

## Revisiones

- **2026-11-11 · Entrada.** Quien decide pegó el informe de la sesión de validación sin más texto. Su conclusión literal:
  «Con dos fallos y uno sin medir, `validaciones.py` todavía no puede entrar en la puerta».
- **2026-11-11 · Auditor sin contexto, primera pasada: 12 puntos.**
  - **No había autorización.** Se le preguntó a quien decide.
  - **«Subagentes de esta sesión» no es una sesión nueva** (`CLAUDE.md` §4). Se le preguntó a quien decide.
  - **Orden de las tareas 2 y 3.** El rojo previo ya está medido arriba, y la tarea 5 va después.
  - **Mutación concreta de A4.** Va sobre una pieza, no sobre la fila visual, que es ciega.
  - **Padding y puerta.** No coinciden nunca.
  - **Prompt del control del revisor.** Se registra el que se va a correr.
  - **La palabra «Cierre»** antes de la etapa final: este plan la evita, y va a `#PP26`.
  - **Recuento.** 13 limpios y 5 que se repiten (2 de ellos habían pasado con reserva).
  - **Hashes.** Medidos.
  - **Evidencia de los 13.** Se copia al repositorio.
  - **Capturas.** Van al informe.
  - **Campos de la cola.** Completos.
- **2026-11-11 · Respuestas literales de quien decide:**
  - Método: «Otra sesión nueva (Recomendado)».
  - Puerta: «Sí, ciérralo (Recomendado)». Es decir, si los 18 pasan, se cierra `#PP7` y `validaciones.py` entra en la
    puerta y en el CI.
- **2026-11-11 · Revisión de pre-PR** (la corrió la sesión de repetición sobre `e082a15`):
  - **B1, PR antes de tiempo:** no se abrió hasta ahora.
  - **D1, `git pull` frente a «ni git fetch»:** corregido en el bloque A nuevo.
  - **D2, resultados vacíos:** rellenados.
  - **D3, capturas fuera de `.claude/informes/`:** el bloque A dice qué hacer con ellas.
  - **O1, estado:** corregido.
  - **O2, recuento:** corregido.
- **2026-11-11 · Repetición: 3 de 5.** `plantilla-review` (positiva y control) y el control de `revisor-externo` pasaron.
  La auditoría visual falló positiva y control: no encadenó `/material-3 audit`.
  - Quien decide respondió, literal: «Sí, ya (Recomendado)». `validaciones.py` entra en la puerta con las 5 piezas, y
    `#PP7` sigue abierta por la fila visual. **Esto cambia A4**, que exigía los 18 pasos.
  - Y también: «Editar compose-visual-loop (Recomendado)». La skill global encadena `/material-3 audit`; se corrigió el
    2026-11-11, fuera del repositorio, y la repetición de la fila visual está pendiente.
  - El texto del filtro de GitHub dice ahora «filtro de GitHub», no «auditor», porque lo usan los dos agentes.

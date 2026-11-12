# Toda propiedad declarada dice su mecanismo: la regla, su verificador, los commands y #PP31

- **Estado:** Plan aprobado
- **Tamaño:** M
- **Entrada:** la ficha es el encargo de la revisión externa del 2026-11-12, literal en
  `docs/mediciones/2026-11-12-propiedad-y-mecanismo.md`.
- **Alcance autorizado, provisional hasta la pasada del auditor:** sí (la revisión externa, por medio de quien decide,
  2026-11-12).
- **Lo que el auditor cambió del alcance:**
  - el verificador **extiende `validaciones.py`** en lugar de ser un guion nuevo (§7, «Una regla»);
  - el paso de `#PP31` mide **los dos agentes**;
  - las instancias van a `wiki/`, no a `CLAUDE.md`;
  - en los manuales se corrigen cinco sitios, no cuatro, con su entrada en `manuales_revisiones.md`.
- **Implementación autorizada:** Sí (quien decide, 2026-11-12, tras la pasada del auditor; granularidad: este plan)

## Parte 1 · Spec

**Qué y por qué.** La regla nueva es: «Toda pieza que declare una propiedad dice qué mecanismo la sostiene, o no la
declara». Hoy nada la exige. Los dos commands dicen «Solo lee», «Solo lees» y «Solo reporta… No editas nada», y corren
en la sesión principal con todas sus herramientas [medido: `grep`, seis coincidencias en `plantilla-contract-sync.md:2,11`
y `plantilla-review.md:2,9`]. Además, `#PP31` sigue abierta: el revisor dijo recibir solo Read y Bash, y el auditor dice
lo mismo de sí mismo en su última pasada [dicho por el agente; sin medir].

**Las instancias**, que van a la página de `wiki/`:
- en Rumba [dicho por la revisión externa]: no hay un entorno de producción separado; la descripción de su auditor
  prometía más que su filtro;
- en la plantilla: la protección de ramas (no existe; la sostienen las reglas de fusión de §9) [leído]; los dos agentes
  [medido: corregidos en `cad6aa7`]; los dos commands.

**Antecedentes:** `docs/historico/planes/2026-11-12-revision-externa-v3.md` (A9, decisiones 2 y 4) y
`docs/mediciones/2026-11-12-validacion-agentes.md` (`#PP31`). **Invariantes:** Rumba no se toca; una pieza cambiada se
revalida en una sesión nueva, porque se carga al arrancar.

**Datos y reglas del verificador:**
- **Dónde:** el registro de `validaciones.py`, sobre las mismas piezas de `piezas()`.
- **Qué busca:** frases de capacidad ya vistas: «solo lee(s)», «solo lectura», «solo reporta», «solo informa», «sin acceso
  a», «no modifica», «no edita(s)», «no escribe», «read-only».
- **Qué exige:** la palabra «mecanismo» o «por instrucción» en la misma línea, en la anterior o en la siguiente.
- **Cómo lo dice:** `FALLO: <pieza>:<línea>: «<frase>» sin su mecanismo`.
- **Modo de prueba:** `validaciones.py --mecanismos <fichero…>`, solo esa comprobación; sin ficheros sale 3.

**Su límite, escrito en la cabecera del guion:**
- Es un grep de frases: caza las formulaciones vistas, no toda afirmación posible. Falla hacia el silencio, que es lo que
  ya pasa hoy.
- **No comprueba el mecanismo**: basta con que aparezca la palabra, aunque sea falsa.
- Una línea con «mecanismo» legitima a sus vecinas aunque declaren otra cosa.
- No mira `tools:` ni `allowed-tools:`. Que `allowed-tools` restrinja un command está sin medir, así que no se usa como
  mecanismo.

**Casos alternativos:** una pieza sin frases no falla; la frase con «por instrucción» al lado tampoco (es el texto
honesto); «Solo informa» en un guion de `config/checks/` queda fuera (afirma que no bloquea, y el texto ya dice «No va en
la puerta»).

**Criterios de aceptación:**
- **C1.** Rojo previo sobre los commands de hoy, y 0 tras corregirlos.
- **C2.** El test cubre las tres ventanas y la salida 3, con sus mutaciones.
- **C3.** La puerta y `calidad` corren la comprobación (ya corren `validaciones.py`).
- **C4.** La regla está en `CLAUDE.md` (una regla, su prueba y un puntero) y en la v3, y las instancias en `wiki/`.
- **C5.** Los commands están revalidados y `#PP31` medida para los dos agentes, en una sola sesión nueva.

**Decisiones pendientes:** ninguna.

## Parte 2 · Plan

### Verificación previa

Contra el commit: `329be68` (`develop`). Rama medida: `operacion/propiedades-y-mecanismos`.

| Afirmación | Cómo se comprobó | Ramas medidas | Resultado | HECHO / SUPUESTO |
|---|---|---|---|---|
| Seis frases sin mecanismo, todas en los commands; los agentes pasan | `grep`; la simulación del auditor | la rama | sí | HECHO |
| `validaciones.py` ya recorre las piezas (`piezas()`) y está en la puerta (al final) y en `calidad` | lectura | la rama | sí | HECHO |
| No existen `wiki/decisiones/propiedad-con-mecanismo.md` ni el modo `--mecanismos` | `ls`; `grep` | la rama | sí: se crean | HECHO |
| `claude_md.py`: como mucho 3 líneas por regla y una media ≤ 2.0 (hoy, 1.83) | el auditor | la rama | sí | HECHO |
| Los manuales tienen cinco sitios sin mecanismo: arranque 147, 850, 910 y 940; operación 128 | `grep` (el auditor) | la rama | sí | HECHO |
| Qué herramientas recibe de verdad cada agente | — | — | — | SUPUESTO: lo mide la sesión nueva |

¿Qué de lo que asumo lo borra este plan? La tarea 0 movió el plan de la v3 y reapuntó sus citas vivas: la cola,
`manuales_revisiones.md` y `.claude/validacion-sesion-nueva.md`.

### Solución y decisiones

- **Se extiende `validaciones.py`**, el verificador de la clase «piezas de `.claude/`» (operación §7). `barrido_texto.py`
  barre otra cosa (el texto de producto) y solo informa. Así no hay un segundo recorrido de las piezas que pueda divergir,
  y el problema de orden de `VERIFICADORES` desaparece.
- **El hook de parada no lo incluye:** `stop-verificadores.sh` corre los verificadores de documentos, y `validaciones.py`
  sale rojo mientras se cambia una pieza, que es lo esperado.
- **Los commands y la tabla de validaciones van en un solo commit, después de la sesión:** con la comprobación nueva, la
  puerta queda roja hasta corregirlos; y al corregirlos, hasta revalidarlos.
- **El plan de la v3 movido:** se le cambió el estado a «Fusionado» al moverlo. Es el cierre previo al traslado, no una
  reescritura del histórico (*Revisiones*).

### Mapa de ficheros

| Fichero | Tarea |
|---|---|
| `docs/plans/2026-11-12-revision-externa-v3.md` → `docs/historico/planes/`; la ficha; las citas reapuntadas | 0 |
| `config/checks/validaciones.py` · `config/checks/validaciones_test.sh` (nuevo) · `config/checks/puerta.sh` · `.github/actions/calidad/action.yml` · `docs/entregables-del-arranque.md` | 1 |
| `CLAUDE.md` · `wiki/decisiones/propiedad-con-mecanismo.md` (nuevo) · `docs/manuales/kmp_manual_de_operacion_v1.md` §7 | 2 |
| `docs/manuales/kmp_bootstrap_playbook_generico_v2.md` (147, 850, 910, 940) · operación §1 (128) · `docs/manuales/manuales_revisiones.md` | 3 |
| `.claude/commands/*.md` · `.claude/validacion-sesion-nueva.md` · `.claude/validaciones.md` · `docs/mediciones/` · `docs/deuda-tecnica.md` (`#PP31`) | 4 |

### Tareas, con su orden

0. **La ficha y el plan de la v3 al histórico** (hecho, sin commitear).
1. **La extensión** (guarda: el test primero).
   - Primero, `--mecanismos` con una función vacía que devuelve «sin fallos» (sale 0).
   - Después, `validaciones_test.sh`, con casos que caen por su aserción:
     - «Solo lee.» → 1;
     - el mecanismo en la misma línea, en la siguiente o en la anterior → 0 (control);
     - sin ninguna frase → 0;
     - `--mecanismos` sin ficheros → 3.
   - Después, la función real. El test entra en la puerta y en `calidad`.
   - `validaciones.py` no cambia de sitio. Sobre el árbol de hoy sale 1 y nombra los commands: es el rojo previo de C1.
2. **La regla** (documento): en `CLAUDE.md` §4, una regla, su prueba y el puntero a
   `wiki/decisiones/propiedad-con-mecanismo.md` (con Firma, Fecha, Estado y las cinco instancias). En la v3, operación
   §7.
3. **Los cinco sitios de los manuales** y su entrada en `manuales_revisiones.md` (documento).
4. **Los commands** (configuración): el texto honesto. Después, un bloque nuevo en `.claude/validacion-sesion-nueva.md`
   con 8 pasos:
   - paso 0: la sesión cargó el texto nuevo;
   - 6 pasos: la positiva, el control y la negativa de los dos commands;
   - 1 paso de `#PP31`: cada agente intenta un Grep y un Glob, y la sesión anota si la llamada existe.

   Tras la sesión, en un solo commit: los commands, sus hashes, los literales en `docs/mediciones/` y `#PP31`. Si los dos
   agentes reciben las cuatro herramientas, `#PP31` se cierra; si no, sus descripciones pasan a decir lo medido.

### Etapa final · Cierre

- [ ] La puerta entera con `--rerun-tasks`
- [ ] Auditoría visual: no aplica
- [ ] Revisión de pre-PR con `/plantilla-review`
- [ ] Overviews: no aplica · `barrido_texto.py`: no aplica
- [ ] Cola: `#PP31`, cerrada, o con las descripciones ajustadas a lo medido
- [ ] `docs/entregables-del-arranque.md` con `validaciones_test.sh`

### Verificación por criterio

| Criterio | Clase | Método y evidencia | Mutación · control | Resultado |
|---|---|---|---|---|
| C1 | Verificable | `validaciones.py` antes y después | rojo previo: líneas `FALLO … sin su mecanismo` de los commands · control: 0 tras corregirlos | |
| C2 | Verificable | `validaciones_test.sh` | sin «solo lee» en la lista → cae «Solo lee. → 1»; ignorar el mecanismo → cae «misma línea → 0»; ventana sin la línea anterior → cae «anterior → 0»; sin el 3 → cae «sin ficheros → 3» | |
| C3 | Verificable | con un command mutado a «Solo lee.», `puerta.sh --rapido` | se lee la línea `FALLO … sin su mecanismo`, no solo «PUERTA ROJA» | |
| C4 | Comprobación humana declarada | quien decide lee el diff; `wiki.py` y `claude_md.py` verdes | — | |
| C5 | Comprobación humana declarada | quien decide, en una sesión nueva; los literales en `docs/mediciones/` | — | |

### Lo que no podrá comprobarse aquí

- Las frases que nadie ha visto todavía, y si el mecanismo nombrado es cierto: el límite escrito.

## Revisiones

- **2026-11-12 · Auditor sin contexto, primera pasada: 15 puntos.** Literal en
  `.claude/reviews/review-operacion-propiedades-y-mecanismos-2026-11-12.md` (local). Resoluciones:
  - **1:** la sexta frase.
  - **2:** la ruta de los antecedentes.
  - **3:** las secciones de la plantilla.
  - **4:** cinco sitios en los manuales; `546` y `550` quedan fuera, con su motivo.
  - **5:** `manuales_revisiones.md`.
  - **6:** función vacía y la ventana anterior.
  - **7:** las mutaciones de la ventana anterior y del 3.
  - **8:** desaparece al extender.
  - **9:** se extiende `validaciones.py`.
  - **10:** las instancias van a `wiki/`.
  - **11:** el límite completo y `allowed-tools`, sin medir.
  - **12:** los dos agentes.
  - **13:** la decisión, en su sección.
  - **14:** el hook queda fuera, con su motivo.
  - **15:** el cierre previo al traslado.
- **2026-11-12 · Autorización** (literal): «Sí, impleméntalo».

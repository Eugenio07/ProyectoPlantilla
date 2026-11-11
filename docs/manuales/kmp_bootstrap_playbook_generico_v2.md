# Manual de arranque · Bootstrap de una app KMP con Claude Code · v2

## Qué es este manual

Set de instrucciones reutilizable para montar **la infraestructura completa de una app Kotlin Multiplatform (Compose
Multiplatform)** con Claude Code como ejecutor. Es v1 corregido y ampliado contra lo que el proyecto de origen hizo
después: lo que la práctica refutó sale corregido dentro del prompt de su fase, y lo que era circunstancia de aquel
proyecto entra como **ranura con su criterio**.

**Este manual monta la infraestructura.** Cómo se trabaja una vez montada (el flujo de una tarea, qué cuenta como
verificado, cómo se pesa y se encola un defecto, quién decide qué) es **el manual de operación**, que va aparte.

**Cómo leer las marcas.** Cada afirmación sobre lo que pasa lleva una:
- **[medido]**: se corrió o se contó en el proyecto de origen.
- **[leído]**: está escrito en su código, sus workflows o sus documentos, pero nadie lo corrió para este manual.
- **[sin medir]**: es inferencia o **propuesta nueva de este manual**, nunca probada en ningún proyecto. Las propuestas
  [sin medir] se prueban la primera vez que se usan; si no se prueban, no se dan por hechas.

---

## Primera página · Quién hace falta y qué se supone

**Esto funciona con una persona y dos agentes que no comparten contexto.** El auditor y el revisor no valen por ser
humanos: valen por no compartir contexto. **Lo único que no se delega es la autoridad de producto**: el texto que lee
quien usa la app y las decisiones que no son técnicas.

**Y una advertencia:** pesar un defecto que te afecta a ti sale mal. Si decides solo, esa es la decisión que conviene
que revise otro. *(En el origen, un defecto del verde de iOS se pesó de más porque quien lo pesaba era quien firmaba
los repartos con ese verde [leído].)*

Los dos agentes:
- **Claude Code**: escribe, verifica y mide, dentro del repositorio.
- **Un revisor externo**: otro agente, en otra conversación, con la memoria de las decisiones de negocio. Decide y
  pregunta; **no lee el código**, así que lo que afirma sobre un fichero se mide antes de usarse (ver el manual de
  operación).
- Más un **auditor sin contexto** como subagente de Claude Code (Fase 12).

### Los supuestos, como ranuras

Cada ranura se rellena **al arrancar**, se escribe en `docs/para-claude-md.md` (Fase 0), pasa a `CLAUDE.md` en la Fase
9, y se relee cuando cambie la circunstancia.

| Ranura | Opciones | Criterio | Qué hay que escribir siempre |
|---|---|---|---|
| **Máquina de verificación** | una máquina capaz de las dos plataformas (macOS: Android + iOS) · varias | iOS solo se compila y arranca en macOS | **Qué pasa cuando esa máquina no está**: qué no corre, quién lo nota y con qué válvula se desbloquea |
| **Dónde corre el CI** | runners alojados · runner propio · mezcla | Se decide por **minutos facturados**, con el método de la Fase 7, no por tiempo de reloj | Qué verifica algo **fuera** de tu máquina de trabajo, y con qué frecuencia |
| **Protección de ramas** | el plan de GitHub la permite · no la permite | En un repo privado de plan gratuito la API de protección responde 403 [leído] | Si no hay protección, las reglas humanas de fusión de la Fase 7 |
| **Backend** | ninguno todavía · propio · de otro equipo | Cambia la Fase 3 entera | De dónde sale la verdad del contrato y cómo se fija su versión |
| **Personas** | una · varias | Este manual supone una | Quién tiene la autoridad de producto |

---

## Cómo se usa este manual, y cómo envejece

- **Si un prompt no coincide con lo que encuentras, gana lo que encuentras.** Una versión que ya no existe, una API que
  cambió, un fichero que la herramienta genera distinto: se hace lo que la realidad permite, se dice en el cierre de la
  fase, y **la discrepancia vuelve a este manual como nota con su fecha**, junto al prompt que falló. Sin esta regla, un
  agente obediente pelea con la realidad para cumplir el prompt. *Es la misma regla que el origen escribió para sus
  documentos («ante conflicto, gana el código real») [leído], aplicada al manual.*
- **Las propuestas marcadas [sin medir]** (listadas al final, en «Lo que este manual propone y nadie ha
  probado») **se prueban la primera vez que se usan**, con la prueba que cada prompt describe. Hasta entonces no se dan
  por hechas. **La primera de la lista es el candado de la puerta**: si está mal, «la puerta es dueña del árbol» vuelve a
  ser una frase.
- **Las cifras del origen son ejemplos, no umbrales.** Donde este manual da un método (el coste del CI, los umbrales de
  `claude_md.py`), se mide en el proyecto propio.

---

## 0 · Filosofía de trabajo

1. **Fases pequeñas y verificables, una cosa a la vez.** **Una tarea, una rama, un PR.** El PR se abre **cuando no
   queda nada por cambiar**, no cuando el código compila: cada subida a un PR abierto relanza el CI, y en el origen las
   re-ejecuciones sobre PR abiertos llegaron a ser el 41 % de los minutos de un mes [leído].
   **Una fase es un hito, no una unidad de trabajo: dentro de ella, cada incremento verificable es su tarea, su rama y
   su PR.** Un PR por fase sale enorme y tarde. El ejemplo es la Fase 6, que son **tres**, cada uno con su prueba de
   comportamiento: (a) el `GlobalCrashHandler` (un crash en release reabre en Home); (b) el `CrashCircuitBreaker` y la
   `CrashSafeScreen` (tres crashes dentro de la ventana llevan a la pantalla segura, y Reintentar recupera); (c) el
   andamiaje `CRASH-TEST-SCAFFOLD` (los botones salen en debug y no en release, en las dos plataformas). En cuántos
   incrementos se parte cada fase [sin medir: es la partición que propone este manual]:

   | Fase | Incrementos | Cuáles |
   |---|---|---|
   | 0 | 3 | **el sitio del trabajo** (ranuras en `docs/para-claude-md.md`, plantilla de plan, `docs/historico/`, `docs/mediciones/`, la cola con su estructura, la rama `develop`, lista de entregables) · catálogo y borrado de la plantilla del wizard · `.gitattributes` y pre-commit |
   | 1 | 1 | estructura de paquetes + `ARCHITECTURE.md` |
   | 2 | 3 | `AppError` y `Resource` · cliente, mapper y `safeApiCall` con sus tests · DI y `startKoinIos` |
   | 3 | A: 1 · B: 2 | los dos borradores · el clon con su documento, y el verificador de contrato **corrido a mano**, con una mutación hecha a mano (todavía no hay puerta ni `mutar.sh`) |
   | 4 | 4 | DTO→repositorio · `AppVersionProvider` y comparador · caso de uso de force-update · mock bi-plataforma + Splash |
   | 5 | 2 | NavHost con placeholders · `ErrorState` con el mapeo de codes |
   | 6 | 3 | handler · breaker + pantalla segura · andamiaje |
   | 6.5 | 5 | spotless + detekt · `mutar.sh` + `entregables.py` · la puerta, con el verificador de contrato de la Fase 3 (rama B o C) como un paso más · `--commit` y el candado · `cola.py`, `que_va_ahora.py`, `barrido_texto.py`, `tope_plan.py` y, si hay herramienta de grafo, su paso de frescura |
   | 7 | 5 | android + calidad · ios con su filtro · sello y `alcance` · compilación limpia fuera de la máquina · protección o reglas de fusión, con el job de varados |
   | 8 | 3 | Android (plugins, `google-services`) · iOS (SwiftPM, plist, dSYM) · reporter + handler + listas de release + lista de reparto |
   | 9 | 3 | `CLAUDE.md` + `wiki/` con `wiki.py`, absorbiendo y borrando `docs/para-claude-md.md`, con `sin_para_claude_md.py` en la puerta · `claude_md.py` con su fichero de excepciones · overviews y plantilla de reversión |
   | 10 | 3 | Stop hooks, con el recordatorio de la auditoría visual de UI · guard · `hooks_test.sh` |
   | 11 | uno por command o skill, más 1 | cada uno con su validación · `validaciones.py` + el verificador de que resuelven |
   | 12 | A: 1 · B: 1 + un PR por prioridad | el auditor sin contexto como subagente, con su filtro · la Fase A (informe en `docs/historico/auditorias/` y entradas en la cola) · la Fase B, un PR por nivel (P0, P1, P2) con un commit por hallazgo, por la **excepción declarada** de abajo |

   **Repaso de cruces hacia atrás**, rehecho tras mover piezas de fase (leyendo el propio manual, 2026-11-09): un
   incremento no depende de nada que su fase no tenga todavía.
   - **Corregidos en la primera pasada:** el verificador de contrato «en la puerta» en la Fase 3 (ahora se corre a mano
     y entra en la puerta en la 6.5); todo «escríbelo en `CLAUDE.md`» de las fases 0, 3, 7 y 8, cuando `CLAUDE.md` nace
     en la 9 (va a `docs/para-claude-md.md`); la prueba de la puerta con un hook real en la 6.5, cuando los hooks
     nacen en la 10 (se prueba con un guion que llama a `candado.sh`); el verificador de la cola citado «de la Fase
     6.5» (nace en la 12).
   - **Rehecho al mover la cola a la Fase 0 y `cola.py` a la 6.5** (quien decide, 2026-11-09): la sección 1 abre una
     entrada antes de la Fase 1, y la cola ya existe; `cola.py` necesita la cola (Fase 0) y `mutar.sh` (6.5, antes en la
     misma fase); el job de varados (Fase 7) usa un guion que nace con él. Las piezas del ciclo que nacen tarde (el
     auditor, la puerta, `<proyecto>-review`, el CI) tienen su sustituto en §0, «Plan antes de aplicar».
   - **Comprobados en la pasada anterior, sin cruce:** la plantilla de plan existe antes del primer plan (es el primer
     incremento de la Fase 0, y su propio plan es su prompt); `docs/mediciones/` existe antes de la primera captura
     (Fase 4); el diff de `mutar.sh` nace con `mutar.sh` (6.5); la lista de reparto y las listas de release, juntas en
     la 8; la plantilla de reversión (9) antes de la primera escritura en producción que el arranque prevé; el
     recordatorio de la auditoría visual (10) antes de su validación (11); el checkout con historial completo de la Fase 7 antes de que `cola.py` corra en el CI.
   - **Lo que parecía un cruce y no lo es:** la Fase 8 manda crashes de prueba al panel real antes de que exista la
     plantilla de reversión (Fase 9). Restaurar no lo deshace, porque un panel de un tercero no se puede restaurar: es una
     **escritura irreversible**, que se confirma y no espera una reversión (Fase 8, «Verificación»; manual de operación
     §6, «Las escrituras: una pregunta y tres casos»).

   **Excepción declarada: la Fase B de una auditoría integral va en un PR por nivel (P0, P1, P2), con un commit por
   hallazgo.** El motivo: la revisión ya ocurrió en el punto de control de la Fase A, donde el humano aprobó la lista
   antes de escribir una línea. Un PR por hallazgo no añade revisión: añade una ejecución de CI por hallazgo, que el
   modelo de coste de la Fase 7 desaconseja (quien decide, 2026-11-09). No se «corrige» hacia un PR por hallazgo.
2. **La rama se sube al crearla.** Una rama que solo vive en una máquina es un punto único de fallo, haya una persona o
   veinte. Subir es la copia de seguridad, no un trámite del PR. *(En el origen, once líneas vivieron dos días solo en
   una Mac [leído].)*
3. **Plan antes de aplicar, siempre.** Cómo se escribe y aprueba un plan: ver el manual de operación. **En el arranque
   cambia esto** (quien decide, 2026-11-09):
   - **El prompt de la fase hace de ficha, pero solo después de rellenar las ranuras** (Fase 0, «El sitio del trabajo»).
     Una ficha dice qué y para quién **en este proyecto**; un prompt de v2 es genérico, y lo que lo vuelve local son
     las ranuras.
   - **El plan de un incremento es solo la Parte 2.** La Parte 1 es el prompt de v2, **por referencia** (la fase y el
     nombre del incremento). La verificación previa es obligatoria y la spec no se reescribe: la Parte 1 ya está
     escrita, y la verificación previa es lo que caza los cruces hacia atrás.
   - **El auditor sin contexto corre una vez por fase, no por incremento.** En el arranque su pregunta es «¿este prompt
     se puede ejecutar aquí?», y esa pregunta es de fase. Después del arranque corre por tarea.
   - La excepción del primer incremento de la Fase 0: todavía no existe la plantilla, así que su plan es el propio
     prompt.
   - **Las piezas del ciclo nacen en fases distintas, y hasta entonces se sustituyen** (quien decide, 2026-11-09):

     | Pieza del ciclo (manual de operación §2) | Nace en | Antes de nacer, la sustituye |
     |---|---|---|
     | El auditor sin contexto como subagente con su filtro | Fase 12 | **Una sesión nueva de Claude Code que recibe solo la ruta del plan**: sin la conversación y sin la bandeja. Lo que la Fase 12 añade es empaquetarla como subagente con su filtro |
     | La puerta (`puerta.sh --commit`) | Fase 6.5 | **Los cuatro verdes a mano** (§0, «Los cuatro verdes, y después una sola puerta») y un commit que nombra los ficheros nuevos, nunca `git add -A` |
     | `<proyecto>-review` | Fase 11 | Una sesión nueva que recibe la rama y `docs/para-claude-md.md` (o `CLAUDE.md` desde la Fase 9), y solo informa |
     | El CI | Fase 7 | La puerta local (o los cuatro verdes a mano); el PR lo dice |
     | `cola.py` | Fase 6.5 | La cola existe desde la Fase 0; hasta la 6.5, sus ids se comprueban a mano |

     **La corrección, dicha** (quien decide, 2026-11-09): esta sección mandaba que el auditor corriera una vez por fase
     desde la Fase 0, y su instrumento, el subagente, nace en la Fase 12. Era un paso cuyo instrumento nace nueve fases
     después.
4. **«Compila ≠ funciona»**, y además **«verde ≠ corrió»** (§2, «Verde no es corrió»). Lo primero es que un build bueno
   no prueba el comportamiento; lo segundo, que un verde puede no ser de nadie.
5. **Los cuatro verdes, y después una sola puerta.** Los cuatro verdes son estos comandos, en este orden:
   ```
   ./gradlew :shared:compileCommonMainKotlinMetadata    # commonMain aislado
   ./gradlew :androidApp:assembleDebug                  # la app Android entera
   ./gradlew :shared:testAndroidHostTest                # tests JVM (commonTest + androidHostTest)
   ./gradlew :shared:compileKotlinIosSimulatorArm64     # iOS
   ```
   **El cuarto es obligatorio siempre que se toque `commonMain`, `iosMain` o las dependencias**: los tres primeros no
   compilan Kotlin/Native, y una rotura de iOS pasa por ellos en verde [leído en v1 y en el origen]. Con
   `--console=plain`, para leer el estado de cada tarea.
   **Antes de la Fase 6.5 se corren a mano**, porque la puerta todavía no existe. **Desde la 6.5, los corre la puerta**,
   los cuatro siempre y en una sola invocación con `--rerun-tasks`: una lista de comandos que se reescribe de memoria
   pierde un eslabón sin que se note, y en el origen entraron cuatro commits en rojo así [leído].
6. **Toda comprobación tiene tres resultados: pasó, falló y no pude medir.** El cierre de una fase responde cada
   verificación con uno de los tres. «No pude medir» nunca se escribe como ✅.
7. **Una regla sin verificador no se cumple** [leído; en el origen tiene página propia y varias instancias]. Vale
   también para los **entregables de este manual**: cada fase que prescribe un artefacto lo registra en la lista de
   entregables, y un verificador comprueba que existe (Fase 6.5). *(En el origen, de las dos listas de release que v1
   pedía, la de Android no llegó a existir y nadie lo notó en meses [medido: `git log --all` vacío para esa ruta].)*
8. **Andamiaje removible, etiquetado**, con una marca que un `grep` encuentra (p. ej. `CRASH-TEST-SCAFFOLD`) y su
   instrucción de retirada.
9. **Las decisiones tomadas con estimaciones se reabren cuando llegan los datos**, y **una decisión que se cambia cita
   la que cambia**. Una alternativa descartada lleva escrito qué la descartó, para releerla el día que esa premisa caiga.
10. **Verificar con búsqueda web las suposiciones sobre terceros ANTES de fijarlas.** «Esto se puede diferir» es una
    hipótesis.

---

## 1 · Stack y método de versionado (sin números fijos: envejecen)

**La lección de ABI que gobierna todo:** las librerías KMP publican sus binarios de iOS (`.klib`) compilados con una
versión concreta de Kotlin. Si tu Kotlin es más vieja que la de una dependencia, **Android compila e iOS revienta**.
Por eso: **Kotlin a la última estable, salvo que una dependencia exija una versión exacta.** Ktorfit lo exige: su
plugin de compilador va atado a una Kotlin concreta [leído]. Entonces la Kotlin **se fija**, se escribe por qué, y
subirla pasa por verificar esa dependencia primero.

**Stack fijo (el qué):** Ktor + **Ktorfit** para red (NO Retrofit/OkHttp: son solo JVM) con engines OkHttp (Android) y
Darwin (iOS) · kotlinx-serialization (`ignoreUnknownKeys = true`) · **Koin** (BOM) · navigation-compose multiplataforma
con rutas type-safe (`@Serializable`) · ViewModel multiplataforma + `StateFlow<UiState>` · kotlin-test + MockK (**solo**
en `androidHostTest`) + ktor-client-mock, que se declara **dos veces**: en los source sets de test (`commonTest`,
para los tests con `MockEngine`) y como `debugImplementation` en la app, para el stub de la Fase 4. Nunca en release.

**PROMPT 1 · Resolución de versiones (primera tarea de Claude Code, con búsqueda web):**
```text
Antes de crear nada: resuelve la matriz de versiones con búsqueda web, verificando compatibilidad real, no supuesta:
1. Kotlin: última ESTABLE.
2. KSP emparejada con esa Kotlin (documentación de JetBrains).
3. Compose Multiplatform: última estable que soporte esa Kotlin.
4. Ktorfit: verifica EXPLÍCITAMENTE que su plugin de compilador soporte esa Kotlin EXACTA y que sus klibs de iOS
   estén publicados para ella. Si exige una versión exacta, la Kotlin del proyecto queda FIJADA a esa, y lo escribes.
5. Ktor, kotlinx-serialization, coroutines, Koin BOM, navigation-compose: últimas estables.
6. AGP y Gradle compatibles con esa Kotlin.
Entrégame la tabla con la evidencia (enlaces) de cada compatibilidad ANTES de escribir el catálogo. Todo va a
gradle/libs.versions.toml, nada hardcodeado, y las versiones NO se copian a CLAUDE.md ni a ningún otro documento:
una copia de una versión envejece sola.
```
**Aviso:** si una librería crítica no soporta la última Kotlin, se baja la Kotlin del proyecto al baseline que todas
soporten, y se abre una entrada en la cola (Fase 0, «El sitio del trabajo») con su disparador. Nunca mezclar (Kotlin nueva + klib vieja = ABI roto en
iOS).

---

## 2 · Convenciones transversales (se fijan el día 0 y van al CLAUDE.md el día que exista)

- **Config-por-plataforma (el patrón nº 1 de KMP):** todo lo que Android obtiene de su build system (`BuildConfig`:
  baseUrl, flags de debug) **iOS tiene que recibirlo explícitamente** en su módulo de plataforma, con el gating
  debug/release **en las dos plataformas desde el día 1**. En iOS el gate es `#if DEBUG` en Swift, pasado como
  parámetro (`startKoinIos(isDebug:)`), porque **los parámetros con valor por defecto de Kotlin no llegan a Swift**:
  explícito en las dos ramas.
- **Proveedores de plataforma:** interfaz en `commonMain` + implementación por plataforma inyectada con Koin, **no**
  `expect/actual`, salvo para piezas sin dependencias ni fake [leído en el origen: la impl puede tomar `Context`, se
  puede falsear en tests JVM, y es uniforme].
- **Tests:** `MockEngine` + pipeline Ktor real → **`runBlocking`** (el reloj virtual de `runTest` dispara el
  `HttpTimeout` de Ktor antes de que el mock responda). Lógica suspend con fakes → `runTest`. Lógica pura → directo en
  `commonTest`. MockK **nunca** en `commonTest`. `runBlocking<Unit>` si la última expresión no es Unit.
- **Idioma:** identificadores en inglés; copy en el idioma del producto, siempre en recursos; claves JSON del contrato
  intocables. Si la regla importa, lleva verificador (Fase 6.5): en el origen estuvo escrita sin él y a la primera
  medición había ~460 identificadores fuera de norma [leído].
- **No-hacer:** no tocar `settings.gradle.kts` ni el `build.gradle.kts` **raíz** sin confirmación; no hardcodear
  versiones ni secretos; no commitear configs nativas de Firebase, keystores ni `local.properties`.
- **`.gitattributes` ANTES del primer guion:** `*.sh text eol=lf`.
- **Repos devuelven `Resource<T>` y nunca lanzan** (solo re-lanzan `CancellationException`). **`domain` puro**, y su
  regla de dependencias **enumera** los paquetes de `core` permitidos: la lista crecerá (en el origen pasó de cuatro a
  cinco con `core/version` [leído]), y la versión corta genera falsos positivos o «correcciones» de código sano.
- **Nada de `git add -A` ni `git add .`:** los ficheros nuevos se nombran. El `.gitignore` no protege de lo que git ya
  sigue, ni de un add forzado [leído; tres incidentes en el origen].

### Verde no es corrió

«Compila ≠ funciona» dice que un build verde no prueba el comportamiento. **No cubre esto:** que el verde mismo no sea
de una ejecución. Un verde puede salir de cinco sitios que no son haber corrido lo que nombra:

| Cómo sale verde sin correr | Cómo se detecta | Cómo se evita |
|---|---|---|
| **`UP-TO-DATE`**: Gradle no ejecuta la tarea porque nada cambió para él | `--console=plain` y leer `> Task … UP-TO-DATE` | `--rerun-tasks` en la puerta de un PR [leído] |
| **`FROM-CACHE`**: la tarea sale de una caché de build, que puede ser **la de otra verificación** | El mismo log, `FROM-CACHE` | La caché del CI no es nunca la de tu verificación local (Fase 7). En el origen, el verde de iOS del CI salió `FROM-CACHE` en los cuatro PR medidos, y la única compilación de iOS real fue la de la puerta local [leído] |
| **Un paso saltado por un filtro de rutas** | El paso sale `skipped`, y el job, verde | El informe del PR nombra qué se saltó, además de qué pasó. El filtro es una apuesta y lo dice [leído] |
| **Un check que no puede fallar**: un `| grep` que tapa el código de salida, un guion que sale 0 cuando no pudo leer su entrada, cero tests | Mutar su entrada y ver que se pone rojo (Fase 6.5); contar los tests | Por defecto un guion de verificación da fallo; la puerta cuenta los tests en los XML y falla con cero [leído] |
| **Otro proceso compilando el mismo árbol** a la vez (un hook, otro Gradle) | Un veredicto que no cuadra con la ejecución | La puerta es dueña del árbol, **con mecanismo** (Fase 6.5 y Fase 10) |

---

## 3 · Las fases

### FASE 0 · Proyecto + reglas base
Wizard de Android Studio (KMP: `shared` + `androidApp` + `iosApp`; `iosApp` es proyecto Xcode, no módulo Gradle).
`git init`, primer commit, repo remoto.
Tres incrementos, en este orden. El primero va antes que nada porque los demás necesitan su plantilla.

#### El sitio del trabajo
```text
PROMPT · Antes de tocar código, el sitio donde se escribe el trabajo. Su plan es este prompt: todavía no hay plantilla.
1. Crea docs/para-claude-md.md con LAS RANURAS RELLENAS: las de la primera página de este manual y las del manual de
   operación. Sin ellas, ningún prompt de este manual puede hacer de ficha. Ahí van también las reglas y decisiones
   que cada fase anterior a la 9 manda "escribir en CLAUDE.md", cuando CLAUDE.md todavía no existe. La Fase 9 lo
   absorbe entero y lo borra.
2. Crea docs/plans/_plantilla.md: cabecera (Estado, Tamaño, Entrada, Implementación autorizada: No), Parte 1 (spec),
   Parte 2 (Verificación previa con "Contra el commit", tareas con su orden por capa, etapa de cierre, Verificación
   por criterio) y Revisiones. En la Verificación por criterio, cada criterio DECLARA SU CLASE: verificable, con la
   mutación que lo mata; o comprobación humana declarada (un criterio de UI, con su captura), con su evidencia y
   dónde vive. Las dos clases, en el manual de operación §4. Y una nota: en el arranque, un plan es solo la Parte 2,
   con el prompt de la fase como Parte 1 por referencia.
3. Crea docs/historico/planes/ y docs/historico/auditorias/, con un README: lo que entra aquí no se reescribe; solo se
   añade una línea "Superado el AAAA-MM-DD → <puntero>" junto a lo que otra rama corrigió.
4. Crea docs/mediciones/ con un README que fija el formato del índice de cada medición o captura: la ruta del
   fichero, la plataforma, el build (depuración o release), el commit, la fecha en la zona del proyecto y, para una
   captura, la clave del texto y el elemento que se ven, escritos con la imagen abierta. Una medición guarda las
   líneas que contestan su pregunta, no todo lo que salió.
5. Crea la COLA, docs/deuda-tecnica.md:
   - una cabecera que diga para qué es (el orden del trabajo vive aquí y en ningún otro sitio) y que no lleva la cuenta
     de qué cambió: eso está en git log;
   - tres secciones: Abiertos · Cerrados (una tabla: id, qué era, el commit que la cerró) · Aceptado a conciencia;
   - la estructura de una entrada, como plantilla comentada: título con id, fecha y QUÉ SE ROMPE (no un área); el
     mecanismo; "Quién lo desbloquea"; el disparador con su estado medido, o una fecha de revisión; "Vigente hasta
     que", con las condiciones de cierre una por una. Los campos y su significado: manual de operación §5.
   Existe desde aquí porque el arranque ya abre entradas (una revisión de versiones pendiente, sección 1). Su
   verificador, cola.py, llega en la Fase 6.5.
6. Crea la rama develop, súbela y hazla la rama por defecto del remoto. Desde aquí, cada incremento va en su rama y
   sale en un PR contra develop, que se fusiona a mano: sin CI hasta la Fase 7, con los cuatro verdes a mano hasta la
   6.5 y con la puerta después. master queda para las versiones (Fase 7, Git Flow).
7. Crea docs/entregables-del-arranque.md: la lista de los artefactos que cada fase de este manual prescribe, con su
   ruta, empezando por los de este incremento. Cada fase añade los suyos. (Lo comprueba un verificador en la
   Fase 6.5.)
```

#### Catálogo y plantilla del wizard
```text
PROMPT · Reglas permanentes: plan antes de aplicar (Parte 2 con la plantilla); no tocar settings.gradle.kts ni
build.gradle.kts raíz; versiones solo en libs.versions.toml; cada rama se sube al remoto al crearla.
1. Aplica la tabla de versiones aprobada al catálogo.
2. Borra la plantilla del wizard (Greeting/Platform y sus tests): antes, verifica con grep que no tiene consumidores.
3. Confirma que la app vacía corre en emulador Android y en simulador iOS.
```

#### Las defensas del día 0
```text
PROMPT · Defensas que valen desde el primer commit.
1. Crea .gitattributes con `*.sh text eol=lf`.
2. Crea .githooks/pre-commit que rechace en el ÍNDICE (git diff --cached --diff-filter=ACMR) los ficheros prohibidos:
   google-services.json, GoogleService-Info.plist, local.properties, *.jks, *.keystore, comparando el basename en
   minúsculas (APFS no distingue mayúsculas). Motivo: .gitignore solo filtra adds implícitos, y la integración de
   Source Control de Xcode fuerza el staging de lo que añades al proyecto. Instálalo con
   `git config core.hooksPath .githooks` y anota en docs/para-claude-md.md que es una vez por clon.
```
**Aviso:** borra la plantilla del wizard **ahora**: en el origen sobrevivió hasta la auditoría final, exportándose al
framework de iOS [leído]. **Y el pre-commit desde el día 0:** en el origen entró un `GoogleService-Info.plist` real en
un commit cuyo `git add` apuntaba a otra carpeta [leído].

### FASE 1 · Arquitectura de paquetes (Clean, un solo módulo Gradle)
```text
PROMPT · Estructura Clean Architecture en shared/src/commonMain/kotlin/<pkg>/: core/{di,network,error,result,log,
platform,crash}, data/{remote/{dto,api,mapper},repository}, domain/{model,repository,usecase},
ui/{navigation,components,splash,safemode}. Reglas (a docs/ARCHITECTURE.md y luego al CLAUDE.md): domain depende solo
de los paquetes core PUROS, ENUMERADOS (error, result, log como interfaz, platform como contratos; la lista se amplía
por decisión escrita, no por costumbre); nunca de core/network ni core/di. data→domain, ui→domain, nada depende de ui.
Un use case existe solo si hace algo que el repositorio no puede. Solo estructura + el documento; sin lógica.
```

### FASE 2 · Core: red, errores, Resource, DI
```text
PROMPT · Núcleo de red y errores. Plan antes de aplicar; los cuatro verdes.
1. core/error: sealed AppError (Network, Http(status), Serialization, Server(code,message), Unknown). Kotlin PURO.
2. core/result: Resource<T> (Success/Error) puro, con map/fold/onSuccess/onError.
3. core/network: NetworkConfig(baseUrl, enableLogging); HttpClientFactory con ENGINE INYECTABLE (para MockEngine),
   ContentNegotiation+json, timeouts, expectSuccess=true; NetworkErrorMapper: Throwable→AppError. CRÍTICO: Ktor NO
   lanza SerializationException al deserializar, lanza JsonConvertException (subtipo de ContentConvertException).
   Captura AMBAS → AppError.Serialization; si no, esa rama es código muerto y todo cae en Unknown.
4. safeApiCall { } → Resource: captura y mapea, NUNCA lanza, re-lanza solo CancellationException.
5. core/di: coreModule + initKoin() común. iOS no tiene Application: startKoinIos() top-level e idempotente, que Swift
   llama en el init() del @main.
6. Tests: el mapper, un caso por tipo (incluido un 200 con cuerpo malformado → Serialization, vía MockEngine +
   runBlocking). Test primero, verlo fallar por una ASERCIÓN (no por no compilar), luego el código.
```

### FASE 3 · Contratos · **ranura: backend**
Elige la rama que corresponda y anota en `docs/para-claude-md.md` cuál es.

**Rama A · Sin backend todavía** (la de v1):
```text
PROMPT · Crea docs/contracts/ con dos documentos marcados BORRADOR (fuente para construir el backend después):
1. config-endpoint.md · GET /v1/config → { "minVersion": "x.y.z", ... }. Documenta: el cliente tolera minVersion
   ausente o malformado con FAIL-OPEN (la app no se bloquea si la config viene rota) + "el backend debe enviarlo
   SIEMPRE: el fail-open es defensa del cliente, no permiso para omitir". Claves desconocidas se ignoran.
2. api-errors.md · envelope { "code": "STRING_CODE", "message": "texto de servidor" } con DOS capas: message es para
   logs y NUNCA se muestra; el cliente mapea code → copy propia (NETWORK, MAINTENANCE, UNAUTHORIZED, RATE_LIMITED,
   VALIDATION_ERROR + fallback para code desconocido, que se registra como telemetría). El texto del fallback NO
   afirma una causa: dice que no se pudo y ofrece la acción neutra.
Regla para DTOs: la clave JSON serializada es la del contrato (@SerialName si la propiedad Kotlin difiere).
```

**Rama B · Backend de otro equipo (o propio en otro repo) con esquema publicado** (lo que el origen tuvo después
[leído]):
```text
PROMPT · El contrato lo publica el backend (openapi.yaml). Monta:
1. Un clon del backend al lado del repo (../<backend>), fijado a un commit. docs/contracts/clon-del-backend.md dice en
   qué commit está, a qué rama sigue y por qué.
2. Un verificador DTO-contra-esquema en config/checks/ (con las propiedades de la Fase 6.5): compara presencia,
   nulabilidad y enums de cada DTO con el esquema del clon. Modo estricto: sale 1 con un aviso; sin clon, 3 (NO PUDE
   MEDIR), nunca verde. Y falla si el commit del clon no es el que dice el documento. En ESTA fase se corre A MANO y
   se prueba con una mutación hecha a mano: copia del DTO fuera del árbol, quitarle un campo, correr, ver el 1,
   restaurar desde la copia. Todavía no hay puerta ni mutar.sh: entra en la puerta en la Fase 6.5.
3. Anota en docs/para-claude-md.md: "el clon se actualiza ANTES de decidir nada sobre un contrato nuevo, en el mismo commit que
   la decisión" (un verificador simétrico rechaza también decisiones sobre valores que el clon fijado no tiene).
4. Los fixtures de test se capturan del servidor CORRIENDO para los valores, y se lee el esquema para la nulabilidad.
   Nunca de memoria ni copiados del contrato.
```
**Aviso (rama B):** un comando que compara contratos y «reporta divergencias» (el `<proyecto>-contract-sync` de v1) no
lo invoca nadie: en el origen, mientras el verificador solo avisaba, sus avisos llevaban semanas abiertos cuando una
fase se fusionó en verde [leído]. Con backend real, el contraste acaba **en la puerta**, en modo estricto, desde la Fase
6.5.

**Rama C · Backend propio en el mismo repo:** la rama B sin clon: el verificador lee el esquema del propio árbol
[sin medir].

### FASE 4 · Config remota + force-update + Splash + mocks bi-plataforma
```text
PROMPT · Vertical completo de config con stub. Plan; los cuatro verdes; prueba de comportamiento en LAS DOS
plataformas.
1. AppConfigDto (tolerante: defaults + ignoreUnknownKeys) → mapper → AppConfig → ConfigRepository →
   GetAppConfigUseCase solo si decide algo que el repositorio no puede.
2. AppVersionProvider como INTERFAZ inyectada por Koin (Android necesita Context). Android: PackageInfo (con el
   deprecation de API 33+). iOS: CFBundleShortVersionString.
3. SemanticVersionComparator: numérico por segmentos, tolerante a sufijos (-rc1 y +build), trim, segmentos
   parcialmente numéricos ("3rc"→3); isValid() aparte y más estricto. Cada tolerancia que el KDoc promete, un test.
4. CheckForceUpdateUseCase → Resource<ForceUpdateStatus>: DISTINGUE "no pude traer config" (Error → Reintentar) de
   "llegó con minVersion malformado" (FAIL-OPEN → Success(UpToDate) + warn con la rama correcta: versión local
   inválida y minVersion inválido son ramas distintas, test de cada una).
5. Stub MockEngine BI-PLATAFORMA desde el día 1: enum ConfigMockScenario en commonMain (UNA fuente de los JSON);
   Android: ktor-client-mock como debugImplementation + provideConfigEngine() por variante (release → null); iOS:
   startKoinIos(useMockConfig:isDebug:), que Swift llama con #if DEBUG pasando true/true y el else explícito
   false/false.
6. SplashViewModel/SplashScreen: Loading/Ready/ForceUpdate(bloqueante)/Error(Reintentar).
Verificación: en Android y en iOS, cada escenario del mock produce su pantalla. Tests del ViewModel, primero.
```
**Aviso:** el mock bi-plataforma **desde el inicio**. En el origen iOS quedó sin stub, y el arreglo posterior costó una
fase entera [leído].

### FASE 5 · Navegación esqueleto
```text
PROMPT · NavHost type-safe (rutas @Serializable): Splash (start) → Home placeholder → Detail(id) placeholder.
Splash→Home con popUpTo(Splash){inclusive=true}. Pantallas SIN diseño. ErrorState reutilizable (full-screen +
Reintentar) que consume el mapeo code→copy. ViewModels con StateFlow<UiState>, registrados donde LAS DOS plataformas
los alcancen. Si un argumento de ruta es un ENUM: @Serializable en el enum Y un NavType explícito con su typeMap,
serializado por name. Comprueba que la app ARRANCA en el simulador iOS, no solo que compila.
```
**Aviso:** un enum como argumento de ruta compila en las dos plataformas, funciona en Android y **mata la app iOS al
arrancar**, porque la navegación type-safe resuelve argumentos por reflexión y Kotlin/Native no la tiene. No lo caza el
cuarto verde: solo arrancar la app [leído; gotcha 9 del catálogo].

### FASE 6 · CrashHandler + Circuit Breaker (el salvavidas)
Sin cambios de fondo respecto a v1: lo que v1 prescribe sigue en el origen [leído]. **Son tres incrementos, A, B y C,
cada uno con su rama, su PR y su prueba de comportamiento** (§0.1): el prompt se pega entero para dar el contexto, y se
pide uno cada vez.
```text
PROMPT · Recuperación ante crashes + protección anti-bucle. Plan; los cuatro verdes; prueba real en las dos
plataformas.

A) GlobalCrashHandler (interfaz en core/crash, inyectada por Koin):
- Android: Thread.setDefaultUncaughtExceptionHandler. En RELEASE: registrar → persistir estado → relanzar a Home
  (NEW_TASK|CLEAR_TASK + extra startAtHome, null-check del launch intent) → exit(0). En DEBUG: delegar al handler
  previo (no ocultar bugs al desarrollador).
- iOS: setUnhandledExceptionHook con @OptIn(ExperimentalNativeApi::class). SOLO registra: en iOS no se recupera. NO
  NSSetUncaughtExceptionHandler: exige staticCFunction, que no captura closures.
- KDocs HONESTOS: la asimetría Android-recupera/iOS-solo-registra, y que tras un "reintentar" el PROCESO SOBREVIVE.

B) CrashCircuitBreaker:
- CrashCounterStore por plataforma. Android: SharedPreferences con commit() SÍNCRONO (apply() y DataStore son
  asíncronos y el exit(0) puede matar la escritura). iOS: NSUserDefaults.
- isTripped() = count >= threshold && dentro de una ventana deslizante anclada al primer crash de la racha.
- CrashBreakerConfig(threshold=3, windowMs, healthyUptimeMs) con init { require(healthyUptimeMs > windowMs) }.
- VENTANAS POR PLATAFORMA, calibradas al mecanismo de relanzamiento real: Android (auto-relaunch ~1 s) → 10_000/15_000.
  iOS (reapertura MANUAL) → 60_000/75_000 como override en el módulo de iOS (Koin: el último gana).
- Reset por vida sana en Android con guard una-vez-por-proceso. En iOS el único reset es el botón de la pantalla
  segura (documentarlo).
- CrashSafeScreen AUTOCONTENIDA (sin DI, sin red, sin config remota, sin recursos: copy hardcodeado, es la única).

C) Botones de prueba etiquetados CRASH-TEST-SCAFFOLD, solo en debug EN LAS DOS plataformas. Removibles por grep.

Verificación: Android debug (crash normal), Android release local (reapertura en Home; 3 crashes < ventana → pantalla
segura; reset tras vida sana), iOS (3 crashes + reaperturas en 60 s → pantalla segura; Reintentar recupera).
Lógica del breaker: test primero.
```

### FASE 6.5 · Calidad estática, verificadores y la puerta *(nueva)*
v1 dejaba la calidad estática para después del bootstrap y ktlint «para cuando haya equipo». En el origen entraron
spotless (con ktlint dentro), detekt y una serie de guiones con **una sola persona**, porque las reglas escritas sin
verificador no se cumplían [leído]. Va **antes** del CI porque el CI reutiliza sus piezas: el job de calidad corre los
mismos verificadores **que pueden correr sin nada externo**, y el de Android los tres primeros verdes en pasos
separados. **El CI no corre la puerta entera**: lo que necesita algo de tu máquina (un clon al lado, `~/.claude`) y el
recuento de tests de la puerta solo corren en local. La puerta corre más que el CI, y el informe del PR dice qué no corrió
en el CI.

#### Qué es un verificador
Un guion que comprueba **una regla escrita** que ninguna herramienta estándar mira (idioma de los nombres, una
invariante del dominio, el contrato contra los DTO, que existan los entregables). Cuatro propiedades, todas
obligatorias:
1. **Tres resultados, con tres códigos de salida distintos:** 0 pasó, 1 falló, **3 no pude medir** (2 para mal uso).
   El tercero no se colapsa ni en el rojo (miente sobre la causa) ni en el verde (miente sobre todo). Al escribirlo,
   antes de «¿qué lo hace fallar?» va «¿qué lo deja sin poder medir, y qué imprime entonces?».
2. **Por defecto, fallo.** El éxito se gana estableciendo positivamente lo que promete. Las herramientas no ayudan: en
   el origen `sips` salía con 0 imprimiendo `<nil>` [leído].
3. **Se prueba mutando su ENTRADA y viendo que se pone rojo**, en el mismo commit en que nace. Una guarda sin una
   mutación que demuestre que puede ponerse roja no es una guarda. El rojo se lee **hasta la aserción que cayó**: un
   build roto también es rojo y no prueba nada [leído; en el origen el mutador daba éxito con un build roto y era deuda
   abierta].
4. **Deuda previa en un baseline**, para que solo hable de lo nuevo. El baseline no es sitio donde esconder lo de hoy.

```text
PROMPT · Calidad estática y la puerta. Plan antes de aplicar.
1. spotless con ktlint DENTRO (no como plugin aparte). La autoridad de formato en build.gradle.kts y su copia en
   .editorconfig para el IDE: si cambias una, cambias la otra. El primer diff de spotlessApply se LEE (un formateador
   puede cambiar lo que el código dice).
2. detekt con config/detekt/detekt.yml que contenga SOLO las diferencias con el default, cada una con su porqué, y un
   baseline para lo previo. No se relaja un umbral para poner el build en verde.
3. config/checks/mutar.sh <fichero> '<sed>' -- <comando>: guarda su propia copia, muta, corre, restaura SIEMPRE con
   trap (sin tocar git). Sale 0 si el comando falló (la mutación se vio), 1 si siguió verde (ciego), 2 por uso, 3 si
   sed no cambió nada. Y además: distingue "falló la aserción" de "no compiló", y en el segundo caso sale 3. Y ANTES de
   correr el comando imprime el diff de la mutación (diff -u de la copia contra el fichero mutado): es lo que permite
   comprobar que la mutación se aplicó y es la que el criterio nombra.
4. config/checks/entregables.py: lee docs/entregables-del-arranque.md y comprueba que cada ruta existe. 0/1/3.
   Pruébalo con dos mutaciones que lo tienen que poner rojo: con mutar.sh, cambia una ruta de la lista por una que no
   existe; y en un clon temporal (git clone --local a un directorio del scratchpad), borra un fichero registrado y
   córrelo allí. Borrar una línea de la lista NO es una mutación válida: comprueba menos y sigue en verde.
5. config/checks/puerta.sh, UN guion que para en el primer fallo y dice cuál fue:
   a) formato (spotlessCheck: la puerta NO reescribe ficheros)
   b) detekt
   c) los cuatro verdes en UNA invocación de Gradle con --rerun-tasks y --console=plain; --rapido lo quita para
      iterar, y la puerta de un PR se pasa sin él
   d) cada verificador de config/checks/ como paso propio, incluido el de contrato de la Fase 3 si existe, en modo
      estricto (y su mutación, ahora con mutar.sh)
   e) el recuento de tests en los XML de resultados: falla si alguno falló o si hay CERO
   Cualquier código distinto de 0 es rojo, incluido el 3. Sin tuberías detrás de un paso: un código de salida se lee
   sin `| grep` y sin nada detrás que pueda fallar.
6. puerta.sh --commit "<mensaje>" [nuevo ...]: la puerta commitea, y solo si pasa. Hace `git add -u` y añade SOLO
   los ficheros nuevos nombrados (rechaza uno que no exista o ya esté rastreado); se niega si queda algo fuera del
   índice en todo el árbol. El mensaje va siempre por fichero (heredoc <<'EOF'): dentro de comillas dobles, zsh
   ejecuta los backticks.
7. LA PUERTA ES DUEÑA DEL ÁRBOL MIENTRAS CORRE, CON MECANISMO: al empezar crea un candado atómico
   (mkdir .git/puerta.lock) con su PID y la hora de arranque de ese proceso dentro (ps -o lstart= -p <pid>: un PID
   solo no basta, el sistema lo reutiliza), y un trap que lo borra al salir.
   - UNA SOLA FUNCIÓN DE LIVENESS, en config/checks/candado.sh, que usan la puerta y todos los hooks: el candado está
     VIVO si existe, su PID existe Y la hora de arranque de ese PID coincide con la escrita. Cualquier otra cosa
     (fichero sin PID, PID muerto, PID reutilizado) es un candado HUÉRFANO.
   - La puerta: con un candado vivo, aborta y dice de quién es. Con uno huérfano, ES ELLA QUIEN LO BORRA, y nadie más,
     y LO DICE al arrancar ("candado huérfano: PID <n>, creado a las <hora>; lo retiro") y añade esa línea a
     .git/puerta-huerfanos.log. Un candado muerto es un dato: dice que una puerta murió a medias (kill -9, la máquina
     dormida), y su verde no existe.
   - Los hooks: con un candado VIVO se callan y salen 0. Con uno HUÉRFANO corren como si no existiera, y no lo
     borran (un solo dueño del borrado). Nunca miran solo si el fichero existe: un candado huérfano silenciaría la
     verificación de cada parada para siempre, y el único síntoma sería que todo va más rápido.
8. config/checks/cola.py, un paso de la puerta, sobre la cola de la Fase 0:
   - identificadores únicos en el fichero de hoy Y que ninguno nuevo reutilice uno que existió: lo lee del historial
     (git log -p del fichero). Sin historial completo (checkout superficial) sale 3, nunca verde;
   - en cada entrada abierta, la presencia de los campos de la plantilla;
   - 0 / 1 / 3. Pruébalo con mutar.sh sobre la cola (un id duplicado; una entrada sin "Vigente hasta que"); con un id
     que existió y se cerró, en un clon temporal con ese historial; y con git clone --depth 1 a un directorio del
     scratchpad, corriéndolo allí (sale 3).
9. config/checks/que_va_ahora.py: la vista derivada de la cola, el único "qué va ahora" que existe. Lista las entradas
   abiertas por el orden del manual de operación §5 (requisitos de salida, luego 🔴, luego 🟡, luego funcionalidad e
   higiene) y escribe arriba la hora y el commit con que se generó. Solo informa; sale 3 si no reconoce la cola. No
   va en la puerta.
10. config/checks/barrido_texto.py: el barrido del texto que deja de ser cierto (manual de operación §2, «La etapa de
   cierre»). Lleva dentro la lista de frases que describen la única salida, escrita en el idioma del producto, y busca
   en los recursos de texto. Solo informa, y lo usa el cierre de cada tarea. No va en la puerta.
11. Ranura «herramienta de grafo» (manual de operación §10): si el proyecto usa una, su grafo lleva escrito el commit
   del que se construyó, y un paso de la puerta falla (o sale 3) si no coincide con el árbol. Si no usa ninguna, una
   afirmación de alcanzabilidad se escribe como "no comprobada: sin herramienta de grafo".
12. config/checks/tope_plan.py, un paso de la puerta: para cada plan de docs/plans/ (salvo _plantilla.md), cuenta las
   líneas que el tope cubre y falla si pasan de ~150 (el umbral, escrito en el guion con su fecha). Las EXCLUSIONES
   viven en config/checks/tope_plan_exclusiones.txt, una por línea, cada una con SU MOTIVO; la primera es la sección
   Revisiones («es el diario de la puerta, no alcance»). Falla si una exclusión no tiene motivo; sale 3 si no reconoce
   las secciones de un plan. Pruébalo con mutar.sh: un plan largo (falla), el mismo con su exceso dentro de Revisiones
   (pasa), una exclusión sin motivo (falla). Por qué este tope sí y el de CLAUDE.md no: manual de operación §2, «El
   plan por dentro».
Pruébala: un test roto a propósito, un fichero sin formato, un verificador que sale 3 (los tres, rojo). Y el
candado, con un guion de prueba que llama a candado.sh como lo hará un hook (los hooks nacen en la Fase 10, y su
prueba real va a hooks_test.sh): mientras la puerta corre, "vivo"; con un PID muerto, "huérfano", y la puerta, al
arrancar, lo anuncia y lo retira; con un PID vivo pero de otro proceso, "huérfano" también.
```
**Por qué el candado («LA PUERTA ES DUEÑA DEL ÁRBOL»):** en el origen «la puerta es dueña del árbol» es **solo una regla escrita**, y un Stop
hook lanza Gradle sobre el mismo árbol en cada parada; un hook dio «build roto» mientras la puerta compilaba, y la
puerta pasó [leído]. Una regla sin mecanismo depende de que nadie la olvide. El candado concreto es una **propuesta de
este manual** [sin medir].

**Por qué la condición es la liveness y no la existencia** [sin medir]: es la tabla de fallo abierto de la Fase 10 bien
aplicada. Que un hook informativo se calle está bien; callarse por un fichero que nadie retiró es fallar abierto **para
siempre**, sin un solo aviso. Por eso la misma función decide en los dos lados, y por eso el borrado tiene un único
dueño que lo anuncia.

**Lo que la puerta del origen no cubre** [leído]: un paso informativo con `|| true` puede salir 3 sin pararla; con
`--rapido`, el recuento puede leer XML de una ejecución anterior; y deja pasar un commit con marcadores de conflicto.
Añade un paso que busque `^<<<<<<<|^>>>>>>>` en lo indexado [sin medir].

### FASE 7 · CI + Git Flow · **ranuras: dónde corre, protección de ramas**

#### El modelo de coste (como método, no como cifras)
- **Cómo se factura un job alojado:** se redondea **al minuto hacia arriba**, job por job; macOS factura más que Linux
  (×10 en el plan del origen [leído]). Un job **saltado** por un `if` cuesta 0, y uno en **runner propio** también
  [medido en el origen contra la API de facturación, día a día].
- **Un job que arranca y sale en segundos cuesta un minuto entero.** Un job cancelado ya pagó lo que corrió.
- **Se optimiza por minutos facturados, no por tiempo de reloj**, si los PR esperan una revisión de todos modos. No se
  parte un job «para que tarde menos»: cada job paga su montaje y su redondeo. Escríbelo en el workflow, para que nadie
  lo «arregle» al revés.
- **Antes de decidir, se mide.** Método: bajar las ejecuciones y sus jobs con `gh api`, aplicar el modelo
  (`ceil(s/60)` por job alojado, 0 por saltado o propio), **contrastar con la API de facturación día a día**, y solo
  entonces decidir qué se fusiona o se mueve. Los días con la cuota agotada no sirven: los jobs «fallan» sin correr.

#### La ranura «dónde corre la verificación»
Decide, y escribe junto a la decisión:
1. **Qué verifica algo fuera de tu máquina de trabajo.** Si todo corre en tu runner propio, hace falta al menos una
   **compilación limpia en una máquina ajena**, programada y además obligatoria antes de cada versión. No se condiciona
   a «si develop se movió»: un check que puede salir verde sin correr no ahorra nada. Usa `--no-build-cache`.
2. **Qué pasa cuando la máquina no está.** En GitHub un job self-hosted sin runner queda **encolado**, no colgado, y se
   descarta a las 24 h; `timeout-minutes` no lo cubre [leído]. Un «Queued» largo es «no pude medir», no rojo. Escribe la
   **válvula**: qué jobs vuelven a runners alojados con un commit, quién decide, y cuándo.
3. **Que la caché de build del CI no es nunca la de tu verificación local.** Un runner propio en tu máquina corre con
   tu usuario y tu `~/.gradle`: un `GRADLE_USER_HOME` propio fuera del checkout, sin `setup-gradle`, y un paso que
   **falle** si resolviera a la casa local. Todos los jobs, no solo algunos: en el origen el job de iOS quedó con
   `setup-gradle` y la caché compartida, y su verde fue el de la puerta local [leído].
4. **Un runner persistente acumula estado** (DerivedData, referencias `pull/*/merge`): borra lo que pueda hacer pasar un
   binario viejo (`rm -rf` del DerivedData del job) y vigila lo que crece [leído].
5. **Su rojo le llega a alguien con nombre:** el correo automático de un `schedule` fallido va a quien editó el cron por
   última vez [leído]. Abre una incidencia asignada.

```text
PROMPT · CI en .github/workflows/ci.yml + Git Flow. Plan antes de aplicar. Antes de nada, rellena las ranuras: dónde
corre cada job y qué pasa cuando esa máquina no está.
1. Job "android": JDK 17 Temurin; GRADLE_USER_HOME propio (paso que falle si coincide con ~/.gradle) si el runner es
   propio, o setup-gradle si es alojado; chmod +x gradlew; un guard que falle si hay ficheros prohibidos RASTREADOS
   (git ls-files); los 3 verdes en pasos separados con --console=plain.
2. Job "calidad", aparte de "android" (un check debe decir lo que comprueba): spotless, detekt y los verificadores que
   no necesitan nada externo. Sus pasos en una acción compuesta (.github/actions/calidad) si otro workflow los repite:
   dos copias derivan. Su checkout con fetch-depth: 0: una regla de documentación (que un identificador no existió
   nunca, que una entrada se cerró) solo es comprobable con el historial completo, así que la profundidad del
   checkout es portante para ella. Con un checkout superficial, ese verificador sale 3, nunca verde (en el origen,
   con el superficial, dio 64 falsos positivos [leído]).
3. Job "ios": compileKotlinIosSimulatorArm64 y, si un FILTRO POR PASO lo pide, construir la app y comprobar que
   ARRANCA en el simulador. En PRs a develop Y master desde el día 1; en push se salta (el PR ya validó ese árbol).
   if: github.event_name == 'pull_request' && contains(fromJSON('["master","develop"]'), github.base_ref).
   - El filtro es un PASO con id (no on.paths): lista de rutas que pueden romper el arranque, cada una con su porqué
     escrito; si no puede listar el diff, CORRE. Comenta que la lista es una apuesta, no una garantía.
   - El destino del simulador por UDID, no por nombre (name= sin versión se completa con OS:latest).
   - Caché de ~/.konan con clave por VERSIÓN DE KOTLIN solo en runner alojado; en runner propio ~/.konan persiste.
4. concurrency con cancel-in-progress por ref.
5. NO on.paths en ningún job que pueda ser requerido: un workflow no disparado deja el check en "Expected" para
   siempre; un if/paso falso reporta "skipped".
6. Sello del árbol (cuando haya push a develop tras cada PR): en el PR, los jobs suben un artefacto con nombre
   verificado-<job>-<git rev-parse HEAD^{tree}>; en el push a develop, un job "arbol" salta android y calidad SOLO si
   el árbol es idéntico byte a byte a uno sellado. Ante cualquier duda (API caída, diff truncado, el push toca Gradle)
   se verifica. master se verifica siempre. workflow_dispatch para forzar el primer salto real.
7. Un job "alcance" en los PR: si el diff es solo documentación (guion propio, con 3 = no pude medir → se corre todo),
   se saltan los jobs que compilan y calidad corre sus guiones sin Gradle.
8. Si todo corre en máquina propia: .github/workflows/limpia-diaria.yml en runner alojado, los 3 verdes + calidad con
   --no-build-cache, programado a diario SIN condición, y también en todo PR con base master, donde bloquea. Su rojo
   programado abre una incidencia asignada por nombre. Incluye un modo de prueba que cae a propósito sin compilar,
   para ver que el aviso llega.
9. Git Flow: develop es la rama por defecto desde la Fase 0; master = rama de release, solo por PR desde develop.
   Job "varadas" tras cada push a develop: config/checks/ramas_varadas.sh lista los commits de ramas remotas cuyo PR
   se fusionó o se cerró y que no están en develop, y las ramas de más de una semana sin PR. Necesita permissions:
   pull-requests: read y fetch-depth: 0. Sin acceso a GitHub sale 3 («no pude preguntar»), que NO se lee como «hay
   varados».
10. Ranura de protección: si el plan de GitHub la permite, tras el primer PR verde activa branch protection en las
    dos ramas con los checks requeridos (un check tiene que haber corrido una vez para aparecer). Si la API responde
    403, anota en docs/para-claude-md.md las tres reglas de fusión de abajo.
Escribe en cada job, en comentario, dónde corre, por qué, y qué deja de comprobarse si se mueve.
```
**Las tres reglas de fusión sin protección de ramas** [leído en el origen]:
- **`gh pr merge --auto` no se usa.** Sin checks requeridos, promete «fusiona cuando pasen» y entrega «fusiona ya»: en
  el origen un PR que tocaba el camino más sensible de la app se fusionó con el CI corriendo.
- **Se espera el verde y se fusiona a mano.**
- **Antes de fusionar se comprueba que la cabeza de la rama es la que se revisó**: en el origen un commit se subió a la
  rama después de fusionar, y no llegó a `develop`. Un job tras cada push a `develop` que liste commits varados (ramas
  con el PR cerrado o fusionado que llevan trabajo fuera de `develop`) lo hace visible.

**Aviso de secretos:** el config de Firebase en CI va como secret en base64 (`GOOGLE_SERVICES_JSON`), con guard de
secret vacío **antes** de decodificar y `test -s` después. Un secret que puede rotar sin que nadie empuje nada (una
clave publicable de un servicio) merece **su propio workflow programado** que pregunte al servicio si la acepta, con un
código distinto para «no pude llegar» y «la rechazó» [leído].

**Aviso de checks que no arrancan:** ante un check rojo, la primera pregunta es **si corrió**. En el origen la cuota de
Actions agotada dejó jobs «fallando» sin correr [leído].

### FASE 8 · Firebase / Crashlytics
```text
PROMPT · Crashlytics con el SDK multiplataforma de GitLive. Plan; los cuatro verdes; verificación contra el dashboard
REAL.
1. Catálogo: dev.gitlive:firebase-common + firebase-crashlytics (versión compatible con la Kotlin del proyecto
   verificada con web: klibs de iOS).
2. Android: GitLive declara los SDKs nativos SIN versión → Firebase BoM en androidApp. Plugins google-services Y
   com.google.firebase.crashlytics, OBLIGATORIO desde el primer build (sin él la app crashea al arrancar en
   FirebaseInitProvider aunque compile). google-services.json en androidApp/ + .gitignore + el .example versionado.
3. iOS: SwiftPM: FirebaseCore + FirebaseCrashlytics; GoogleService-Info.plist (bundle id EXACTO), COPIADO DESDE FINDER,
   nunca con "Add Files" de Xcode (fuerza el staging); run-script del dSYM; FirebaseApp.configure() ANTES de
   startKoinIos().
4. CrashReporter (interfaz en core) + impl GitLive, en Koin de LAS DOS plataformas; el GlobalCrashHandler hace
   recordException → persistencia del breaker → exit.
5. recordException escribe en un worker de fondo sin flush público: espera acotada documentada (Thread.sleep de
   ~300 ms en una constante) entre recordException y exit(0).
6. Colección apagada en DEBUG Android (androidApp/src/debug/AndroidManifest.xml). Anota en docs/para-claude-md.md: "lo que se
   prueba desde un build de depuración NO existe para Crashlytics; toda medición del tipo 'mira si salió un evento'
   dice desde qué build se provocó, y un cero no se lee sin eso".
7. DOCUMENTA el trade-off recover-to-home: en release TODO crash recuperado llega como NON-FATAL; "Crashes: 0" no
   significa salud.
8. Crea docs/android-release-checklist.md y docs/ios-release-checklist.md, y AÑADE LOS DOS a
   docs/entregables-del-arranque.md: la puerta los exige desde este commit. Android: keystore real (la firma debug
   cambia de SHA-1 con cada máquina que construye, y el client ID de Google Sign-In la tiene registrada), R8/minify,
   mapping de Crashlytics. iOS: dSYM del primer release, baseUrl de producción.
9. Crea docs/reparto/_plantilla.md, la LISTA DE REPARTO de una versión, y añádela a la lista de entregables. Dos
   secciones que no se mezclan: COMPROBADO (criterios verificables, cada uno con su mutación vista y su commit) y
   PASOS DECLARADOS (validaciones, capturas, pruebas en dispositivo, lecturas de un panel: quién, cuándo, sobre qué
   build y dónde está la evidencia). Las dos listas de release de arriba alimentan PASOS DECLARADOS. Lo que falta
   de la segunda sección no se mueve a la primera (manual de operación §4).
Verificación: crash de prueba en cada plataforma → REABRIR la app → visible en el dashboard. ANTES de mandar el
primero, PARA y pide confirmación: restaurar no deshace un crash enviado al panel de un tercero, así que es una
ESCRITURA IRREVERSIBLE (manual de operación §6, «Las escrituras: una pregunta y tres casos»). Antes se escribe qué va a
quedar en el panel (cuántos crashes, de qué build, con qué marca), y la confirmación se anota en el plan. Android
release local:
`adb shell am crash <appId>`.
```
**Aviso:** las dos listas de release llevan verificador porque en el origen una de las dos listas no llegó a existir y nadie lo vio
[medido].

### FASE 9 · CLAUDE.md
**Lo que cambia respecto a v1:** v1 fijaba un tope de 150 líneas. En el origen el fichero nació con **135** y tiene hoy
**1107** [medido, `wc -l`], y nadie escribió cuándo ni por qué se abandonó el tope [leído: no encontré el motivo].

**Por qué el tope era el instrumento equivocado.** Mide la longitud, que es el síntoma, y no el contenido, que es la
causa. Ante una regla nueva solo ofrece dos salidas: borrar otra o mandarla a un documento que el agente no lee al
arrancar. Y es una regla sin verificador, así que se rompió en silencio.

**Qué compró el crecimiento** [sin medir: juicio sobre lo leído]. Cada regla nueva llegó con su incidente, y el agente
la aplicaba con el porqué delante. **Lo que costó:** el fichero se carga entero en cada sesión, una regla se encuentra
peor entre párrafos de historia, y el propio fichero promete en su primera frase «solo lo accionable» mientras narra
incidentes [leído].

**La regla que sustituye al tope:** en `CLAUDE.md` va **la regla y su prueba**; **el incidente va en un registro** que la
regla cita en una línea. La prueba es cómo se comprueba que se cumple: el verificador que la mira, o la pregunta que hay
que contestar.

**La señal de alarma:** si el fichero crece más deprisa que su número de reglas, se han colado incidentes. Lo vigila un
verificador [sin medir: propuesta de este manual].

**Qué hace fallar ese verificador, y por qué así** [sin medir]. **Falla la proporción líneas por regla; los patrones
solo avisan.** Los patrones («lo enseñó», una fecha, «medido el») son una lista de literales prohibidos, y una lista así
no puede estar completa: cualquier reformulación la esquiva. En el origen ese tipo de lista está encolado como defecto
propio [leído]. La proporción es **derivada**: se calcula de lo que hay, no de cómo está escrito, y no se esquiva
escribiendo distinto. Un incidente narrado con palabras nuevas sigue sumando líneas a su regla. Por eso la proporción
decide y los patrones solo señalan dónde mirar.

**Y una regla puede necesitar su longitud** [sin medir]. Sin una salida declarada, quien se tope con una así **acorta
una regla que necesitaba su longitud** o **relaja el umbral**, y relajar un umbral para poner el build en verde está
prohibido. La salida es un fichero de **excepciones declaradas, cada una con su motivo**: se cuentan aparte, no entran
en la media, y una excepción que ya no hace falta se avisa. El caso de este manual es la regla de dependencias de
`domain`.

```text
PROMPT · Genera CLAUDE.md en la raíz: denso, imperativo, operativo. Cada regla es: el enunciado en imperativo + su
prueba (el verificador que la comprueba, o la pregunta que hay que contestar) + un puntero de una línea al registro
donde vive el porqué (wiki/trampas/, wiki/decisiones/). NUNCA la narración del incidente dentro.
Secciones (los números no se renumeran: el código y los hooks los citan): qué es el proyecto (2 líneas) · las ranuras
de este manual rellenas · stack: lo que el catálogo no cuenta (pins y su porqué), SIN copiar versiones · la puerta y
cuándo se usa · arquitectura (domain con la lista enumerada) · config-por-plataforma (qué provee cada módulo) ·
convención de tests · crash/breaker · CI: dónde corre cada job, qué pasa sin esa máquina, reglas de fusión ·
idioma · no-hacer · contratos (rama de la Fase 3) · excepciones deliberadas, enumeradas (una regla sin sus
excepciones hace que un agente "corrija" código sano).
Absorbe docs/para-claude-md.md entero (cada línea a su sección, con la regla y su prueba) y bórralo en el mismo
commit; quítalo de la lista de entregables.
Crea config/checks/sin_para_claude_md.py, un paso propio de la puerta: si CLAUDE.md existe y docs/para-claude-md.md
también, FALLA (dos fuentes de reglas derivan). Sale 0 si CLAUDE.md existe y el otro no, y 3 si no puede leer el
árbol. La lista de entregables comprueba presencia; esto comprueba AUSENCIA, y por eso no cabe en ella. Pruébalo
recreando el fichero en un clon temporal (git clone --local a un directorio del scratchpad) y viendo el 1 allí:
mutar.sh solo cambia un fichero que existe.
Crea wiki/ con decisiones/ y trampas/: una página por porqué. Una página de decisiones/ lleva al principio su firma,
su fecha, su estado y, si descartó alternativas, descartada_porque: con la premisa que las descartó (manual de
operación §7). Una de trampas/ lleva el síntoma, la causa y la regla de CLAUDE.md que la cita. config/checks/wiki.py,
en la puerta: falla si una página de decisiones/ no tiene esos campos o si una regla de CLAUDE.md apunta a una página
que no existe.
Crea config/checks/claude_md.py. Una regla es un elemento de lista de primer nivel dentro de una sección; sus líneas
son las suyas y las de sus subelementos. FALLA (exit 1) si alguna regla pasa de un umbral de líneas, o si la media
de líneas por regla del fichero pasa del suyo; los dos umbrales viven escritos en el propio guion, con la fecha y el
valor de partida que los fijó. Solo AVISA (exit 0, con la línea) si encuentra patrones de narración: una fecha
AAAA-MM-DD fuera de un puntero, "lo enseñó", "pasó el", "medido el". Sale 3 si no reconoce ninguna regla. Pruébalo con
mutar.sh dos veces: con un párrafo de incidente que use los patrones (avisa y, por la proporción, falla) y con el
mismo incidente reescrito sin ninguno (tiene que seguir fallando). Añádelo a la puerta.
EXCEPCIONES DECLARADAS: config/checks/claude_md_excepciones.txt, una línea por regla que puede pasarse del umbral:
el identificador de la regla (su sección y su primera frase) y SU MOTIVO escrito. El verificador:
- las cuenta APARTE: no fallan por el umbral y NO ENTRAN EN LA MEDIA, para que no la diluyan;
- FALLA si una excepción no tiene motivo, o si nombra una regla que no existe;
- AVISA si una excepción ya no hace falta porque la regla vuelve a caber: una excepción que sobra es deuda silenciosa.
La primera excepción es la regla de dependencias de domain: enumera los paquetes permitidos porque la versión corta
genera falsos positivos (Fase 1). Pruébalo con cuatro casos: una regla larga sin excepción (falla), con excepción y
motivo (pasa), con excepción sin motivo (falla), y una excepción cuya regla ya cabe (avisa).
```
**Aviso:** una regla de arquitectura escrita sin sus excepciones genera «correcciones» de código sano. Y **ante
conflicto entre un documento y el código, gana el código**: escríbelo.

#### Overviews y reversiones
```text
PROMPT · Los dos documentos de trabajo que faltan.
1. docs/overviews/ con un README: un overview es un documento vivo, en el idioma del producto, que se lee SIN saber
   programar y a la vez enlaza los ficheros reales de un área. Se actualiza en el cierre de la tarea que cambia esa
   área (manual de operación §2). Uno por área cuando el área exista; ninguno en abstracto.
2. docs/reversiones/_plantilla.md, el FICHERO DE REVERSIÓN que precede a toda ESCRITURA REVERSIBLE: una escritura
   fuera del árbol cuyo efecto deshace restaurar el estado anterior (manual de operación §6, «Las escrituras: una
   pregunta y tres casos»). Lleva: qué se escribe y dónde; el estado de antes, leído con su fecha; el comando que
   revierte; cómo se comprueba que revirtió; quién autorizó, con fecha. Se rellena y se commitea ANTES de escribir.
3. Añade los dos a la lista de entregables.
```
**Lo que no se crea: un plan de acción** (quien decide, 2026-11-09). **El orden del trabajo vive en un solo sitio, la cola
(Fase 12), o se contradice.** Un segundo documento que también ordena deriva: en el origen, la fila de decisiones de su
plan de acción salió en una auditoría sin comprobar contra el estado actual [leído]. Si hace falta «qué va ahora», es
una **vista derivada de la cola**, generada por un guion con su hora, no un fichero que alguien mantiene.

### FASE 10 · Hooks de Claude Code
**Lo que cambia respecto a v1:** v1 pedía «fail-open ante JSON raro» para todos los hooks. Eso vale para un hook que
**añade** verificación; **un hook que deniega tiene que fallar cerrado.** Un guard que falla abierto no es un guard: se
desactiva justo cuando su entorno se rompe, sin avisar. *El caso real* [leído; no lo corrí sin `python3`]: en el origen,
el filtro que niega GitHub al auditor saca el comando del JSON con `python3` sin comprobar que exista; sin `python3` el
comando sale vacío y el filtro **deja de denegar**. Y su guard de ficheros protegidos deja pasar cuando no consigue leer
la ruta.

| Clase de hook | Ante un entorno roto o una entrada que no entiende |
|---|---|
| **Informa** (Stop: tests, estilo, recordatorios) | **Falla abierto:** sale 0 en silencio. Un hook roto no estorba |
| **Deniega** (PreToolUse: guard, filtro del auditor) | **Falla cerrado:** si no puede leer la ruta o el comando, deniega o pide confirmación, y dice por qué |

```text
PROMPT · Hooks en .claude/ (van al repo). Plan; explícame la mecánica. Antes de escribir, comprueba qué hay en el
entorno (jq, python3); cada hook comprueba él mismo lo que usa.
1. Stop hook stop-run-jvm-tests.sh: corre :shared:testAndroidHostTest. Rojo → exit 2 con el resumen. Verde →
   silencio. Guard anti-bucle stop_hook_active. DISTINGUE "tests rojos" de "build roto". FALLA ABIERTO: sin gradlew,
   o con el candado de la puerta VIVO según config/checks/candado.sh (Fase 6.5), sale 0 sin correr nada. Con el
   candado huérfano, corre, y no lo borra.
2. Stop hooks de verificadores: los de config/checks/ que el proyecto quiera oír al parar (los de la Fase 6.5 y los que
   haya añadido), corriendo el mismo guion que la puerta; un criterio, dos disparadores.
   Si falta jq o python3, salen 0: informan, no deniegan.
2b. Stop hook stop-ui-audit-reminder.sh: si el árbol sucio o el último commit tocan ficheros de UI, recuerda correr
   LA AUDITORÍA VISUAL DE UI. Ranura: la herramienta que el proyecto use (una skill, un plugin, un comando), escrita
   en CLAUDE.md. No corre nada ni sabe si ya se corrió: solo avisa, y falla abierto. Mira el árbol sucio y el último
   commit, no la rama entera, para avisar justo después del trabajo de UI y no en cada parada (en el origen había
   uno igual [leído]).
3. Guard PreToolUse guard-protected-files.sh, matcher "Edit|Write|MultiEdit". Decisiones por JSON en STDOUT con
   exit 0 ({"hookSpecificOutput":{"permissionDecision":"ask"|"deny",...}}): el exit 2 ignora el JSON. Comparaciones en
   MINÚSCULAS. "deny": keystores, local.properties, google-services.json, GoogleService-Info.plist. "ask":
   build.gradle.kts RAÍZ (su directorio contiene settings.gradle.kts), settings.gradle.kts y
   gradle/libs.versions.toml (el fichero de mayor riesgo: un bump mueve la matriz de ABI). FALLA CERRADO: si la
   llamada es Edit/Write y no consigue extraer file_path, devuelve "ask" diciendo que no pudo leer la ruta. Sin match,
   deja pasar.
4. Pruebas de los dos tipos (echo '<json>' | bash script), y GUÁRDALAS como guion en config/checks/hooks_test.sh:
   mayúsculas, ruta de módulo (pasa), catálogo (ask), JSON ilegible (ask en el guard, 0 en los Stop), sin jq/python3
   en el PATH (el guard sigue denegando), un test roto (aviso del Stop), el Stop con el candado VIVO (silencio), el
   Stop con un candado de PID MUERTO (corre, y el candado sigue ahí para que la puerta lo anuncie), y con un PID vivo
   de otro proceso (corre). El guion entra en la puerta.
```
**Nota:** ktlint no va como hook (latencia en cada edición): va en la puerta y en el CI (Fase 6.5), aunque haya una sola
persona.

### FASE 11 · Commands & Skills
```text
PROMPT · Cristalizar el proceso en herramientas. Plan. Commands y skills CARGAN AL INICIO DE SESIÓN: tras crearlos,
reiniciar.
1. Prefijo del proyecto en TODO command (<proyecto>-review...): los built-ins (/review, /init...) tienen
   PRECEDENCIA. Documentar la convención en CLAUDE.md.
2. <proyecto>-review (.claude/commands/): revisión de pre-PR contra CLAUDE.md (referencia, no copia). SOLO lee y
   escribe su informe en un sitio fijo; hallazgos con archivo:línea. Se corre ANTES de abrir el PR, no después.
3. <proyecto>-test: OPCIONAL. Solo si en el primer mes hay tests escritos fuera de la matriz; si no, no se crea.
4. Sin backend real, <proyecto>-contract-sync que REPORTA divergencias con los borradores. Con backend real, no:
   el verificador de la Fase 3 en la puerta.
5. Skill kmp-ios-gotchas (.claude/skills/<nombre>/SKILL.md): la description ES el disparador. Gatillos concretos:
   "editar iosMain", "expect/actual", "añadir dependencia a commonMain", "arranque de iOS", "registrar en Koin algo
   bi-plataforma". Contenido: el catálogo de la sección 4 (síntoma→causa→solución).
6. VALIDACIÓN DE CADA SKILL Y CADA COMMAND, en tres pasos, con su registro:
   - POSITIVA: una tarea de su territorio sin nombrarla → tiene que activarse.
   - CONTROL: "¿qué skill consultaste y qué gotcha aplica?" → la nombra y cita el gotcha. Responder bien SIN nombrarla
     es haber respondido de memoria: el disparador falló.
   - NEGATIVA: una tarea ajena (cambiar un color) → NO se activa; si dispara, estrechar la description.
   El resultado va en .claude/validaciones.md: una fila por skill o command, con la fecha, los tres prompts usados y
   el resultado de cada uno (pasó / falló / no pude medir).
7. config/checks/validaciones.py: falla si una skill, un command o un agente de .claude/ (skills/, commands/,
   agents/) no tiene fila, si alguno de sus tres
   pasos no pasó, o si el SKILL.md o el command es más nuevo (git log) que su fila. Pruébalo con mutar.sh. Entra en
   la puerta. Y escribe en su cabecera lo que NO comprueba (ver el aviso de abajo).
8. config/checks/skills_de_cierre.py, un paso informativo de la puerta: las skills y commands que nombra LA LISTA DE
   CIERRE (la «Etapa final · Cierre» de docs/plans/_plantilla.md y la del plan vivo) RESUELVEN, en los tres sitios donde
   pueden vivir (skills personales, plugins habilitados, commands del repo). Sin ~/.claude sale 3, que no es verde.
9. NO crear en abstracto los commands de generación (screen/feature/endpoint): se crean cuando exista el caso real
   que los calibre, y su description dice sobre qué se calibraron.
10. La herramienta de AUDITORÍA VISUAL DE UI de la ranura (Fase 10) se valida en los mismos tres pasos y tiene su
   fila en .claude/validaciones.md: POSITIVA (un cambio de UI → se usa y da su informe), CONTROL, NEGATIVA (un cambio
   que no toca UI → no se usa).
```
**Aviso:** en el origen **nunca se hizo** la validación en tres pasos y nada la obligaba [leído: no encontré ningún
registro en el repo]. Lo que existía comprobaba que las skills resuelven, no que se activen. Por eso el registro y su
verificador (`.claude/validaciones.md` y `validaciones.py`), que son [sin medir].

**Qué comprueba `validaciones.py`, y qué no** [sin medir]. La validación en tres pasos es una **conversación**, y su
resultado lo escribe alguien a mano en un fichero. Así que el verificador **no comprueba que la validación pasara**:
comprueba que alguien **declaró** que pasó. Es una **comprobación humana declarada**, y por la regla de este manual no
cuenta como verificada (§0.6: «pasó» se establece positivamente, y aquí lo establece una persona, no el guion). Lo que
sí garantiza:
- **que la fila existe**, con su fecha y sus tres prompts escritos, de modo que la validación se puede repetir;
- **que caduca**: si el `SKILL.md` o el command cambia después de la fecha de su fila, la puerta se pone roja hasta que
  alguien vuelva a validarlo.

Se queda porque sin él la validación no se hace nunca, como en el origen. Pero un verde de `validaciones.py` se
informa como «declarada», no como «verificada». Y que una skill no aparezca en un cajón no significa que falte: en el
origen se concluyó «no instalada» mirando solo `~/.claude/skills/`, y estaba habilitada como plugin [leído].

### FASE 12 · Las dos auditorías
Son **dos piezas distintas** y hacen falta las dos.

| | **Auditoría del plan** | **Auditoría integral de cierre** |
|---|---|---|
| Qué caza | Lo que su autor no puede ver, **antes de que exista el código** | Lo que los revisores atentos dejan pasar durante semanas |
| Cuándo | Cada plan, antes de llevarlo a revisión | Al cerrar el bootstrap, y después por etapas |
| Quién la lanza | **Quien redacta el plan**, siempre, sin preguntar | El desarrollador |
| Con qué | Un subagente sin contexto que solo ve el documento y el código | El modelo más capaz, en dos fases |

Un paso que depende de que alguien conteste «¿lo lanzo?» falla en silencio cuando nadie contesta: en el origen pasó
así, y por eso lo lanza quien redacta [leído]. **En el arranque corre una vez por fase**, no por incremento (§0, «Plan antes de
aplicar»). Cuándo pasa exactamente, cómo se resuelven sus hallazgos y cuándo se deja
de parchear y se rehace la forma: ver el manual de operación.

```text
PROMPT A · El auditor sin contexto. Crea .claude/agents/auditor-sin-contexto.md: tools Read, Grep, Glob, Bash; solo
lectura; juzga un documento solo por lo escrito y por el código del repositorio. Le niega GitHub (gh, github.com) un
hook PreToolUse propio que FALLA CERRADO: si no puede leer el comando, deniega. Motivo: si lee las incidencias y los
PR, comparte las suposiciones de quien escribió el plan. Lo que solo se sabría por ahí va a una sección "Lo que no pude
comprobar". Valídalo en tres pasos con su fila en .claude/validaciones.md (Fase 11), adaptados a un agente: POSITIVA
(recibe la ruta de un plan → devuelve hallazgos con su sección), CONTROL (su primer encargo es `gh --version` → sale
rechazado), NEGATIVA (se le pide editar un fichero → no lo edita). Añade el filtro a config/checks/hooks_test.sh: un
comando con gh se deniega, y sin python3 también.
```
**Aviso:** en el origen el filtro se probó con entradas, pero **el agente entero quedó sin probar** [leído]. Es la
prueba que este prompt exige.

#### El puente con la cola
En el origen hubo dos auditorías integrales, con 31 y 2 hallazgos: **33**. Ninguno de los dos informes registró cuál se
arregló, y dos meses después seguían pidiendo trabajo ya hecho, sin forma de saber cuál sin auditar al auditor [leído].
**Una auditoría de cierre sin una cola que registre qué se cerró se pudre.** El informe se archiva; lo que vive es la
cola. Cómo se escribe, se pesa y se cierra una entrada: ver el manual de operación, §5. **La cola existe desde la Fase 0**
(«El sitio del trabajo», con su estructura) y **`cola.py` desde la 6.5**, así que ya está cuando llega la Fase A.


```text
PROMPT B · AUDITORÍA INTEGRAL EN DOS FASES ESTRICTAMENTE SEPARADAS.
FASE A (solo lectura, PROHIBIDO modificar): audita código, CI, hooks, docs y estado git. Por hallazgo: TRIAJE P0
(bug real) / P1 (deuda que morderá) / P2 (higiene), archivo:línea, EVIDENCIA verificada y marcada (medido / leído /
sin medir), riesgo concreto, arreglo MÍNIMO, esfuerzo S/M/L, flags [TOCA-iOS][DECISIÓN-DE-PRODUCTO][CAMBIA-CONTRATO].
Verificación adversarial: re-verifica cada hallazgo intentando refutarlo. Una afirmación de ausencia ("no hay forma de
X") solo vale si se intentó X. Sección "VERIFICADO Y SANO". Lista explícita de NO-hallazgos: [las decisiones ya
aceptadas a conciencia]. Entregable: el informe, en docs/historico/auditorias/AUDIT-AAAA-MM-DD.md, y cada hallazgo EN
LA COLA: antes, busca su mecanismo; si ya tiene
entrada, se añade ahí; si no, entrada nueva. El peso de la entrada NO lo pones tú: lo propones, y lo pone quien decide
(manual de operación §5). DETENTE y espera mi aprobación.
FASE B (tras aprobar la lista): una rama y un PR por nivel (excepción declarada a §0.1); SOLO lo aprobado, P0→P1→P2,
UN COMMIT POR HALLAZGO ("audit(Pn): resumen [#id]") hecho con la puerta; los [TOCA-iOS] con prueba de arranque en
simulador; PAUSA ante [DECISIÓN-DE-PRODUCTO]/[CAMBIA-CONTRATO] o cualquier "ask" del guard. Cada commit CITA su
entrada; el sha cierra el commit, NO la entrada: la entrada se cierra contra su "Vigente hasta que", condición por
condición. Lo que descubras durante la ejecución no se corrige: va a la cola por el mismo camino (primero su
mecanismo). Entregable: resumen de commits + no aplicados con su razón (que siguen abiertos en la cola).
```
**P0/P1/P2 no es el peso** (quien decide, 2026-11-09). Es **el triaje de un informe de auditoría**: ordena la ejecución de ese
lote, y muere con él. **El peso de la entrada** se pone como dice el manual de operación: el ejecutor propone, quien
decide pone, se busca el mecanismo antes y se cierra condición por condición. **El sha cierra el commit, no la entrada.**

**Excepción declarada a §0.1** (quien decide, 2026-11-09): la Fase B va en **un PR por nivel (P0, P1, P2)**, con un commit por
hallazgo dentro. La revisión ya ocurrió en el punto de control de la Fase A, donde el humano aprobó la lista antes de
escribir una línea; un PR por hallazgo no añade revisión, añade una ejecución de CI por hallazgo, que el modelo de coste
de la Fase 7 desaconseja.

**Aviso:** esfuerzo máximo para la Fase A; un nivel menos basta para la B. Un commit por hallazgo abarata las
interrupciones: se retoma con `git log --oneline` buscando el último `[#id]`.

---

## 4 · Catálogo de trampas iOS/KMP (para la skill y para el humano)

1. **Config-por-plataforma incompleta.** *Síntoma:* `NoDefinitionFoundException` solo en iOS. *Causa:* algo registrado
   solo en el módulo Android que el grafo común necesita. *Solución:* su equivalente en el módulo de iOS, en cada
   registro nuevo. Un test del grafo que solo mire Android no lo caza [leído].
2. **Dependencia solo-Android en commonMain.** *Síntoma:* falla `compileKotlinIosSimulatorArm64` con los 3 verdes en
   verde. *Solución:* moverla a androidMain o sustituirla por una KMP.
3. **ABI de klibs.** *Síntoma:* errores de ABI/metadata en iOS tras subir una lib. *Solución:* alinear la Kotlin
   (sección 1).
4. **APIs experimentales de Native.** *Solución:* `@OptIn(ExperimentalNativeApi::class)`.
5. **Acceso a Koin desde iosMain.** *Síntoma:* «Unresolved reference GlobalContext». *Solución:* guardar el `Koin` que
   devuelve `startKoinIos`; no usar `GlobalContext` en iOS.
6. **Arranque de Koin en iOS.** *Síntoma:* «KoinApplication has not been started». *Solución:* `startKoinIos()`
   idempotente en el `init()` del `@main`, antes de cualquier resolución y después de `FirebaseApp.configure()`.
7. **Binario o caché viejos.** *Síntoma:* «el cambio no funciona» tras un pull. *Solución:* clean + Invalidate Caches
   ANTES de sospechar del código. Si dos hipótesis compiten y una se confirma, la otra NO queda refutada.
8. **`xcodebuild` por CLI contamina el DerivedData de Xcode.** *Síntoma:* `Library not loaded: @rpath/<App>.debug.dylib
   … code signature` al arrancar. *Causa:* el CLI firma ad-hoc y Xcode con tu identidad, sobre el mismo DerivedData.
   *Solución:* `-derivedDataPath` propio siempre; si ya pasó, borrar el DerivedData y re-resolver SwiftPM [leído].
9. **Un enum como argumento de ruta type-safe mata la app en iOS.** *Síntoma:* cae al arrancar, sin log; Android
   funciona. *Solución:* `@Serializable` + `NavType` explícito con `typeMap`, por `name`. Solo lo caza arrancar la app
   [leído].
10. **La config nativa que entra al repo por Xcode.** *Causa:* Xcode fuerza el staging y `git commit` se lleva el índice
    entero. *Solución:* copiarla desde Finder; defensas: el pre-commit, el guard del CI y el guard de Claude Code
    [leído].
11. **Swift y los valores por defecto de Kotlin.** No se exportan: en Swift, todos los argumentos explícitos, y los
    gates con `#if DEBUG` en las dos ramas.
12. **Kotlin↔Swift y `suspend`.** `suspend` no cruza hacia una interfaz que implementa Swift: el contrato es de
    callback, la suspensión se monta en Kotlin y el mapeo a errores lo decide Kotlin [leído].

---

## 5 · Meta-lecciones de proceso

1. **Las afirmaciones de infraestructura se verifican antes de escribirse.** En el origen, «el CI va en GitLab»
   sobrevivió dos versiones de documento [leído en v1].
2. **«Esto se puede diferir» es una hipótesis.** El plugin de Crashlytics «diferible» era obligatorio.
3. **Las decisiones con estimaciones se reabren cuando llegan los datos.** Y las cifras de este manual también: la de
   v1 para el job de iOS (~3 min) era la de compilar; con la app construida y arrancada eran ~19 min en runner alojado
   y ~45 s en uno propio con caché caliente [leído].
4. **Tres resultados, no dos.** Un «listo» a secas deja cabos invisibles, y un ✅ sobre algo que no se pudo medir es
   peor.
5. **Probar el comportamiento, no la compilación, y comprobar que el verde es de una ejecución** (§2).
6. **Una regla sin verificador no se cumple**, ni siquiera las de este manual: cada fase registra sus entregables y la
   puerta los exige.
7. **Un fichero de instrucciones crece con los incidentes:** la regla dentro, el incidente fuera (Fase 9).
8. **La auditoría integral vale la pena, y sin cola se pudre** (Fase 12).
9. **Un hallazgo que encaja demasiado bien con lo que vienes persiguiendo se mide más, no menos.** En el origen, «la
   skill no está» se convirtió en un PR y un plan, y estaba instalada como plugin [leído].

---

## 6 · Después del bootstrap (etapa de producto)

En orden sugerido: **design system y tokens de marca** → **decisión de backend** (o su cambio de rama en la Fase 3) →
**primer vertical de datos real** (y con él: el command de endpoint, calibrado sobre ese caso, y validado en tres pasos)
→ **pantallas de producto** → i18n (recursos multiplataforma, nada hardcodeado) → logging estructurado → Turbine para
tests de `StateFlow` → **las listas de release de la Fase 8, ejecutadas** → primer release, por PR a `master` con la
compilación limpia en verde.

La calidad estática ya no está en esta lista: entra en la Fase 6.5. El día a día, desde aquí: **ver el manual de
operación.**

---

## Declaración de cambios respecto a v1

**Procedencia** (del manual): las decisiones firmadas «quien decide, 2026-11-09» salieron de la revisión externa del
proyecto de origen ese día. La firma lleva la ranura y no un nombre, porque una firma existe para saber a quién
preguntar, y en un manual genérico quien decidió no es quien lee: un nombre ahí se puede leer como un papel.

### Qué se generalizó: lo específico del origen y su ranura

| Lo que hizo el origen | Cómo entra aquí |
|---|---|
| Todo el CI en una Mac propia, por la cuota de Actions | Ranura «dónde corre la verificación» (Fase 7), con dos obligaciones: escribir qué pasa sin esa máquina, y verificar algo fuera de ella |
| Repo privado de plan gratuito, sin protección de ramas (403) | Ranura «protección de ramas»: si se puede, checks requeridos; si no, las tres reglas de fusión |
| Backend de otro equipo con `openapi.yaml` y un clon fijado | Ranura «backend» con tres ramas (Fase 3) |
| Una compilación limpia diaria en Ubuntu porque todo lo demás iba en la Mac | Obligación general de la ranura: algo verifica fuera de tu máquina, a diario y antes de cada versión |
| `GRADLE_USER_HOME` propio porque el runner corría con el usuario de la Mac | Regla general: la caché del CI nunca es la de la verificación local |
| La lista de rutas que pueden romper el arranque de iOS en ese proyecto | El método: un filtro por paso, cada ruta con su porqué, y «si no puedo listar, corro» |
| Las cifras de coste (700 min facturados, 31 % de redondeo…) | El método de medición (`gh api`, el modelo, contrastar con la facturación), sin las cifras |
| El check diario de la clave publicable de un servicio externo | Clase general: un secret que rota sin push merece un check programado |
| Verificadores concretos (`una_clave`, `eventos_cerrados`, `version_sync`…) | La clase «verificador» con sus cuatro propiedades, más los generales del arranque (entregables, `CLAUDE.md`, validaciones, hooks, ids de la cola) |
| El revisor externo es Claude web con la memoria del negocio | «Un agente que no comparte contexto», en la primera página |

### Qué quedó fuera por inseparable del origen
- Los verificadores de invariantes del dominio del origen (tres guiones): sin el dominio no queda nada que generalizar
  más que la clase.
- El sello de versión común y su chequeo (`version_sync.py`): solo tiene sentido si el backend rechaza versiones viejas.
  Podría ser una ranura; no lo hice porque depende de un contrato que este manual no supone.
- `graphify` y su grafo versionado: es una herramienta de consulta de ese proyecto, no infraestructura.
- Lo que dependía de la persona del backend (dos guiones y el registro literal de sus mensajes): depende de una segunda
  persona con su propio repo. Su regla general está en el manual de operación (§8).
- El flujo de una tarea, la cola, el peso, agrupar por mecanismo y las reglas de razonamiento de `CLAUDE.md` §11: van
  al manual de operación. Aquí solo se nombran.

### Qué de v1 se quitó, y por qué
- **El tope de 150 líneas de `CLAUDE.md`**: se sustituye por «regla y prueba dentro, incidente fuera». El tope se rompió
  sin dejar motivo escrito [medido: 135 → 1107 líneas].
- **La tabla de versiones dentro de `CLAUDE.md`**: una copia de una versión envejece. El origen la sustituyó por «solo
  en `libs.versions.toml`» [leído].
- **«Fail-open ante JSON raro» para el guard**: un guard que deniega falla cerrado (Fase 10).
- **«ktlint como check de CI cuando haya equipo»** y la calidad estática en la etapa de producto: entran en la Fase
  6.5 con una persona.
- **Los cuatro verdes como comandos sueltos**: los sustituye la puerta (Fase 6.5).
- **`docs/audit/AUDIT-<fecha>.md` como entregable que vive**: el informe se archiva y lo que vive es la cola (Fase 12).
- **«El job iOS tarda ~3 min»**: era la cifra de compilar, no del job con arranque (§5.3).
- **«Activar branch protection» como paso incondicional**: pasa a ranura, porque hay planes donde la API responde 403.
- **«Fases pequeñas, rama por fase» sin más**: se sustituye por una tarea, una rama, un PR, la rama subida al crearla y
  el PR abierto cuando no queda nada.
- **«Cierres con ✅/❌»**: tres resultados, no dos.
- **`<proyecto>-contract-sync` como comando obligatorio**: con backend real lo sustituye un verificador en la puerta, y
  `<proyecto>-test` pasa a opcional. El origen nunca lo creó, y no encontré escrito por qué [leído].

### Lo que este manual propone y nadie ha probado [sin medir]
Se prueban en este orden la primera vez que se usan (ver «Cómo se usa este manual»):
1. **El candado de la puerta** y su función de liveness compartida, `candado.sh` (Fase 6.5, «LA PUERTA ES DUEÑA DEL
   ÁRBOL»). Va primero: sin
   él, la propiedad del árbol es otra vez solo una regla.
2. `entregables.py` (Fase 6.5).
3. `claude_md.py`, con la proporción como lo que falla (Fase 9).
4. `hooks_test.sh` (Fase 10).
5. `.claude/validaciones.md` con `validaciones.py`, que es una comprobación humana declarada (Fase 11).
6. El guard que pide confirmación cuando no puede leer la ruta (Fase 10).
7. El paso de marcadores de conflicto en la puerta (Fase 6.5).
8. La rama C de la Fase 3.
9. Que `mutar.sh` distinga «no compiló» de «falló la aserción» (en el origen es deuda abierta).
10. `cola.py`, con los identificadores leídos del historial y la presencia de los campos (Fase 6.5). Los ids con historial existen en el origen [leído]; los campos, no.
11. El recordatorio de la auditoría visual de UI y su validación (Fases 10 y 11).
12. `tope_plan.py` y su fichero de exclusiones (Fase 6.5).

La partición en incrementos de §0.1 también es una propuesta [sin medir], pero no es una pieza que se prueba: se
corrige con la primera fase que no quepa en ella.

---

## Cómo se sabe si este manual funciona

- **La prueba es que un agente sin contexto construya la app base solo con esto.** Sin este proyecto, sin la
  conversación que lo escribió y sin el repositorio de origen: el manual y las ranuras rellenas.
- **Lo que ese agente pregunte es lo que el manual no dijo.** Cada pregunta se anota con la fase en que salió y entra
  en la versión siguiente: como texto en el prompt de esa fase, o como ranura si la respuesta depende del proyecto. Una
  pregunta que se contesta en el chat y no llega al manual se volverá a hacer.
- **Y lo que ese agente haga distinto de lo que dice un prompt**, por la regla de «gana lo que encuentras», es la otra
  mitad del mismo registro.
- **La segunda prueba, cuando exista el manual de operación:** que ese mismo agente pase **una tarea entera** por él,
  de la ficha al merge, sobre la app que construyó con este.

Hasta que se haga la primera prueba, este manual está **sin medir** en su conjunto: está destilado de un proyecto que
lo hizo de otra manera, no probado como manual.

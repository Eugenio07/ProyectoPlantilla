# La cola

**El orden del trabajo vive aquí y en ningún otro sitio.** Esta cabecera no lleva la cuenta de qué cambió: eso está en
`git log`. «Qué va ahora» es la vista que genera `config/checks/que_va_ahora.py`. Cómo se escribe, se pesa y se cierra
una entrada: manual de operación §5 (`docs/manuales/kmp_manual_de_operacion_v1.md`). Los identificadores (`#PPn`) no se
reutilizan nunca: lo comprueba `config/checks/cola.py` contra el historial.

<!--
Plantilla de una entrada (copiar bajo «Abiertos»):

### #PPn · AAAA-MM-DD · <qué se rompe, no un área> <marcas: 🔴|🟡|(requisito de salida)|(funcionalidad)|(higiene)|(peso por decidir)|(por medir)|(por partir)>

- **Mecanismo:** <uno solo; si hay varios, son varias entradas>
- **Quién lo desbloquea:** nosotros | quien decide | otro equipo
- **Disparador:** cuando <evento observable> — estado medido: <…, con fecha> | **Fecha de revisión:** AAAA-MM-DD
- **Vigente hasta que:**
  1. <condición de cierre>
  2. <…>
- **Peso:** <firma de quien decide> | (peso por decidir) — propuesta: (a) … (b) … (c) … (d) … ; sistema donde ocurre el daño: …
-->

## Abiertos

### #PP2 · 2026-11-11 · No hay detekt estable que analice Kotlin 2.4 (higiene)

- **Mecanismo:** detekt 1.23.8 (estable) está construido con Kotlin 2.0.21; el soporte de 2.4 solo existe en
  2.0.0-alpha.6 [leído: la medición de la matriz].
- **Quién lo desbloquea:** otro equipo (detekt).
- **Disparador:** cuando detekt publique una 2.0.0 estable — estado medido 2026-11-11: no publicada. **Fecha de
  revisión:** 2027-01-11.
- **Vigente hasta que:**
  1. el proyecto usa un detekt estable que analiza Kotlin 2.4, o
  2. quien decide acepta a conciencia la alpha (y la entrada pasa a «Aceptado a conciencia» con su firma).
- **Peso:** fuera de escala (higiene): cuatro «no»; solo lo ve el equipo. — Eugenio Martínez (quien decide), 2026-11-11, aceptando la propuesta (`docs/mediciones/2026-11-11-aprobacion-fase-b.md`).

### #PP4 · 2026-11-11 · Android Studio no ofrece simuladores iOS para correr `iosApp` (higiene)

- **Mecanismo:** el plugin Kotlin Multiplatform de Android Studio (261.25134.80-AS) lanza un `NullPointerException` en
  `SimulatorDeviceType.intFromNumberOrString` al leer los simuladores, deja la lista vacía y Run deshabilitado
  [leído: `idea.log` de Android Studio, 7 apariciones el 2026-11-11]. Disparador probable, **sin medir**: runtimes
  iOS 27.2 registrados pero no disponibles.
- **Quién lo desbloquea:** otro equipo (JetBrains) o quien decide (limpiar runtimes en Xcode).
- **Disparador:** cuando se actualice el plugin o se limpien los runtimes — **Fecha de revisión:** 2026-12-11.
- **Vigente hasta que:**
  1. Android Studio lista un simulador y corre `iosApp`. Mientras tanto, iOS se corre desde Xcode.
- **Peso:** fuera de escala (higiene): solo lo ve el equipo. — Eugenio Martínez (quien decide), 2026-11-11, aceptando la propuesta (`docs/mediciones/2026-11-11-aprobacion-fase-b.md`).

### #PP11 · 2026-11-11 · Crashlytics de iOS recibiría todos los crashes como un único error sin traza (higiene)

- **Mecanismo:** `SwiftCrashReporter` registra un `NSError` con dominio fijo, `code: 0` y solo el mensaje [leído; sin medir: no hay proyecto]. [TOCA-iOS] Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** la primera app derivada con proyecto de Firebase.
- **Disparador:** cuando una app derivada configure Firebase en iOS — estado medido 2026-11-11: el código ya manda un
  `ExceptionModel` con el tipo y los frames de Kotlin (`93cd0f8`, PR #39); la plantilla no tiene proyecto, así que el
  panel no se puede mirar. **Fecha de revisión:** 2027-02-11.
- **Vigente hasta que:**
  1. ~~el reporter de iOS manda un `ExceptionModel` con los frames de la pila de Kotlin~~ (`93cd0f8`);
  2. un crash de prueba en un proyecto real se ve agrupado por su causa en el panel.
- **Peso:** fuera de escala (higiene): lo ve el equipo en el panel. — Eugenio Martínez (quien decide), 2026-11-11, aceptando la propuesta (`docs/mediciones/2026-11-11-aprobacion-fase-b.md`).

### #PP28 · 2026-11-12 · Nada vigila que `PreviewActivity` no vuelva al APK de release 🟡

- **Mecanismo:** `#PP8` quitó `compose.uiTooling` de `androidMain` (`6f8ecf7`) y se midió **una vez** en el manifiesto
  fusionado de release; ninguna guarda lo vuelve a mirar (`git grep PreviewActivity` en `config/`, `androidApp/`,
  `shared/`, `.github/`: nada) [medido 2026-11-12]. Un arreglo sin verificador no se cumple (revisión externa, B1).
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-12.
- **Vigente hasta que:**
  1. `verificarSinStubEnRelease` (la guarda de la clase, `#PP12`) **se extiende** —no se escribe otra al lado, decisión 2
     de la revisión externa— a `ui-tooling` en los classpaths de release y a `PreviewActivity` en el manifiesto fusionado
     de release, con su mutación.
- **Peso:** 🟡: el mismo daño que `#PP8` (cualquier app del dispositivo podría lanzar composables). — Eugenio Martínez (quien decide), 2026-11-12, aceptando la propuesta (`docs/historico/planes/2026-11-12-revision-externa-v3.md`, *Revisiones*).

### #PP30 · 2026-11-12 · Los defectos de forma de un plan los encuentra el auditor, y diluyen los de fondo (higiene)

- **Mecanismo:** las columnas de la plantilla, las casillas de cierre, los antecedentes y la autorización antes de
  tiempo salen en las pasadas del auditor mezclados con los hallazgos de fondo, y se leen igual. «Cerca del 40 %» de
  sus hallazgos son de forma: **derivación, no recuento** (estimación del ejecutor sobre las pasadas de 2026-11-11 y
  2026-11-12; los informes anteriores no se guardaron).
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-12.
- **Vigente hasta que:**
  1. `config/checks/plan_lint.py` en la puerta comprueba la forma de los planes vivos (secciones de la plantilla, clases
     de criterio de §4, casillas de cierre, las dos líneas del alcance provisional), con su mutación.
- **Peso:** fuera de escala (higiene): solo lo ve el equipo. — Eugenio Martínez (quien decide), 2026-11-12, aceptando la propuesta (`docs/historico/planes/2026-11-12-revision-externa-v3.md`, *Revisiones*).

### #PP33 · 2026-11-12 · Los manuales escriben la versión de la app de escritorio, y `versiones.py` no los mira (higiene) (peso por decidir)

- **Mecanismo:** `versiones.py` solo recorre `CLAUDE.md`, `README.md`, `wiki/` y `.claude/`; sobre los dos manuales
  sale 1 con cuatro avisos: la versión de la app de escritorio dos veces, y «hallazgo 5.6» y «6.1», que son falsos
  positivos porque `NO_VERSION` no conoce «hallazgo» [medido 2026-11-12, `docs/mediciones/2026-11-12-pp29-ficha.md`].
  Decisión 4 de quien decide: la versión sale a la medición fechada y se cita.
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-12.
- **Vigente hasta que:**
  1. los manuales citan la medición en vez de la versión, y `versiones.py` recorre `docs/manuales/kmp_*.md` y sale
     verde, con su mutación.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

### #PP34 · 2026-11-12 · `validaciones.py --mecanismos` da seis falsos positivos sobre los manuales (higiene) (peso por decidir)

- **Mecanismo:** mira línea a línea y cuenta las frases citadas: sobre los manuales, sus seis fallos son un mecanismo
  partido entre dos líneas, citas «…» de lo que no vale, una condición («Sin acceso a GitHub sale 3») y dos frases
  excluidas a propósito [medido 2026-11-12 por el auditor, pasada 2 de `#PP29`]. Por eso no puede ser paso de la puerta
  sobre los manuales.
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-12.
- **Vigente hasta que:**
  1. `validaciones.py --mecanismos` une los párrafos y no cuenta las frases entre «», y corre sobre los manuales en la
     puerta, verde y con su mutación.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

### #PP35 · 2026-11-12 · Nada comprueba las atribuciones ni la base de las marcas de los manuales (higiene) (peso por decidir)

- **Mecanismo:** los criterios C6 (la base de cada marca) y C7 (las formas de atribución) de la pasada 2 de `#PP29`
  esperaban la decisión 5, ya tomada: «la revisión externa» es una atribución válida y «en la plantilla» una base válida
  de `[medido]`, escritas en operación §7 y en los dos «Cómo leer las marcas». Falta el verificador.
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-12.
- **Vigente hasta que:**
  1. `manuales.py` comprueba C6 y C7, verde y con su mutación.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

### #PP36 · 2026-11-12 · Las reglas de los manuales que piden juicio no tienen ni aviso (higiene) (peso por decidir)

- **Mecanismo:** los candidatos C9 (las reglas «(v3…)» sin fila en su tabla), C10 (afirmaciones sin marca), C12
  (recuentos), C13 (reglas escritas como «X sale mal») y C14 (la tabla de lo que el arranque deja a operación) necesitan
  juicio, y la pasada 2 de `#PP29` los recomendó solo como avisos. La misma pasada leyó, sin mecanizar: operación llama
  «v2» al arranque, que se titula v3; «Lo que el auditor no pudo comprobar» dice que los guiones no existen; y los
  `[medido]` del 2026-11-09 remiten a informes que no están en el repositorio.
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-12.
- **Vigente hasta que:**
  1. los avisos salen en `manuales.py` (salida 0 con la línea), y lo leído está corregido en los manuales o tiene su
     porqué.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

## Cerrados

| Id | Qué era | Commit que la cerró |
|---|---|---|
| `#PP29` | `manuales.py` no existía: nada mantenía que los manuales cumplieran sus reglas. Criterios de una pasada nueva del auditor sobre la v3; cinco en la puerta (C1, C2, C3, C5, C8) y los manuales corregidos hasta verde; lo que quedó fuera, en `#PP33`–`#PP36` | (este commit) |
| `#PP32` | La plantilla de plan pedía «la puerta con `--rerun-tasks`». Dicen «La puerta entera (sin `--rapido`)» la plantilla y operación §2 | (este commit) |
| `#PP3` | Los iconos de la barra de estado de Android no se leían sobre el fondo claro (tema de ventana y de la app fijos en claro). Mismo mecanismo que `#PP25`; cerrada con él | `2dbdf81` |
| `#PP25` | Con el sistema en oscuro, la barra de estado de Android no se leía. `AppTheme` (claro/oscuro, sigue al sistema, colores base de Material provisionales) y tema de ventana con `values-night`; capturas en las dos plataformas, claro, oscuro y cambio de modo | `2dbdf81` |
| `#PP31` | Los agentes declaraban Grep y Glob y no los recibían. Causa más cercana, medida: la app de escritorio no ofrece Grep ni Glob a ninguna sesión (la versión, en `docs/mediciones/2026-11-12-pp31-causa.md`; el porqué dentro del binario, sin medir). Opción A: `tools: Read, Bash` y descripciones con lo medido; revalidados 6 de 6. El manual (FASE 12) dice que `tools:` no es un suelo | `48048e0` |
| `#PP24` | La pantalla de actualización obligatoria no tenía salida. `storeUrl` por plataforma en la config (contrato, DTO, mapper); el caso de uso elige y valida la de su plataforma; botón «Actualizar» que abre la URL (Android: Play Store; iOS: Safari, sin App Store en el simulador); las listas de release obligan a comprobar la ficha real (condición reformulada por quien decide) | `75fd2e3` |
| `#PP26` | `skills_de_cierre.py` leía otro trozo si la palabra de la etapa final salía antes en el plan, y las secciones `###` de detrás. Lee solo la sección «Etapa final» hasta el siguiente encabezado de nivel igual o superior; `skills_de_cierre_test.sh` en la puerta, con mutaciones | `ffd7d56` |
| `#PP27` | El recordatorio de UI avisaba en cada parada tras un merge y no veía lo sin rastrear. Ignora el último commit si es un merge y suma lo sin rastrear; casos con `merge --no-ff` y mutaciones | `8e31300` |
| `#PP7` | Las skills, commands y agentes no estaban validados en tres pasos. 18 de 18 pasos en dos sesiones nuevas más la repetición de la fila visual (`docs/mediciones/2026-11-11-validacion-pp7.md`); `validaciones.py` en la puerta y en `calidad` (`c1d6ed5`); `compose-visual-loop` encadena `/material-3 audit` | `3f7b362` |
| `#PP5` | Los textos de la Splash, de `CrashSafeScreen` y las acciones por code no estaban aprobados. Aprobados como provisionales de plantilla; `error_network` ya no afirma una causa; la salida de la actualización obligatoria, decidida (opción a) → `#PP24` (`docs/mediciones/2026-11-11-aprobacion-pp5.md`) | `551786c` |
| `#PP23` | El job `ios` compilaba el SDK de Firebase en frío en cada PR. `ios` restaura el DerivedData y `cache-ios` lo llena desde `develop`: «Construir la app» 8 min 07 s → 3 min 11 s (`docs/mediciones/2026-11-11-cache-ios.md`) | `f3f3317` |
| `#PP12` | Nada impedía que el stub de config volviera a release de Android. `:androidApp:verificarSinStubEnRelease` (grafo resuelto) en la puerta y en el job `android`; mutación `debugImplementation`→`implementation` → roja | `4c6b662` |
| `#PP13` | Las variantes release no se compilaban. `assembleRelease` en `VERDES` y en `android` (una mutación de tipo en `src/release` era ciega); iOS Release medido una vez (`docs/mediciones/2026-11-11-ios-release.md`) | `c752870` |
| `#PP14` | Un segmento de versión más largo que un `Int` valía 0. Segmentos comparados como cadenas de dígitos; test primero (`hugeSegmentsCompareNumerically`) y mutación | `36309b3` |
| `#PP15` | El recuento no exigía resultados de cada plataforma. `recuento_tests.py --exige` (puerta: Android + iOS; CI: Android), con `recuento_tests_test.sh` y mutación | `2c700f4` |
| `#PP16` | Había versiones copiadas fuera del catálogo. `CLAUDE.md` y `wiki/` limpios; `versiones.py` + `versiones_test.sh` en la puerta y en `calidad`; `pbxproj`/`Package.resolved` en §13 | `0b1b33e` |
| `#PP17` | El guard no veía las escrituras por Bash. Matcher con `Bash` y heurística que deniega (nombra un protegido y escribe); límite en §13; 5 casos y mutación | `5d0f2c5` |
| `#PP18` | El filtro del auditor dejaba pasar `gh` entre comillas. Quita comillas y barras; casos y mutación | `e8fefb7` |
| `#PP19` | El README era el del wizard. README en español: qué es, hook del clon, documentos, apps, puerta, Firebase, ramas | `edab344` |
| `#PP20` | `.gitignore` no ignoraba keystores. `*.jks` y `*.keystore`; `check-ignore` y mutación | `5444840` |
| `#PP21` | `hooks_test.sh` corría Gradle en el job `calidad`. El caso del Stop hook de tests usa un `gradlew` falso con marca; mutación | `cd97758` |
| `#PP22` | El recordatorio de UI saltaba con Swift que no es UI. Solo vistas (`View`) e `iOSApp.swift`; casos sobre repositorios temporales y mutación | `ed2ba62` |
| `#PP8` | El APK de release exportaba `PreviewActivity` (`compose.uiTooling` en `androidMain` de `shared`). Manifiesto fusionado de `release`: 0 `PreviewActivity`; `debug`: 2 | `6f8ecf7` |
| `#PP9` | `puerta.sh --commit` buscaba marcadores en el índice viejo. Reproducido con la puerta vieja; con la nueva, roja en f) | `41846f1` |
| `#PP10` | El breaker no cortaba un bucle de crash lento en Android. Racha por vida sana (decisión de quien decide), disparo hasta un reset; tests con mutaciones y `releaseLocal` (crash a los 5 s ×3 → pantalla segura) | `80f447c` |
| `#PP6` | El CI no estaba en el remoto (el token no podía subir workflows). Las 5 condiciones, en el PR #37: subido a `.github/`; `alcance`, `android`, `calidad` e `ios` en verde con los verdes ejecutados (`corrio.sh` en `android` e `ios`); los tres verdes en una invocación; `calidad` con los verificadores de la puerta; `alcance` solo con `solo_docs=` | `7577bfc` |
| `#PP1` | El compilador de Ktorfit podía no compilar contra Kotlin 2.4.21. Condición 1 cumplida: Ktorfit corre en JVM y en el simulador (`ConfigRepositoryImplTest`, `ConfigRepositoryIosTest`) | `2b95a60` |

## Aceptado a conciencia

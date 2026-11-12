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

### #PP3 · 2026-11-11 · Los iconos de la barra de estado de Android no se leen sobre el fondo claro 🟡

- **Mecanismo:** con `enableEdgeToEdge()` y el tema `Theme.Material.Light.NoActionBar` del manifiesto, la barra de
  estado pinta iconos claros sobre la superficie clara de la app [medido 2026-11-11: captura
  `docs/mediciones/2026-11-11-app-vacia-android.png`, build de depuración, commit `00b30dd`]. El mecanismo exacto
  (estilo de la barra frente al tema) está **sin medir**.
- **Quién lo desbloquea:** nosotros.
- **Disparador:** cuando se cree el tema de la app (etapa de producto, «design system y tokens de marca») — estado
  medido 2026-11-11: no existe tema propio.
- **Vigente hasta que:**
  1. una captura de Android en tema claro y oscuro muestra los iconos de la barra de estado legibles.
- **Peso:** 🟡: (a) no; (b) no; (c) no en la ruta crítica (la hora y la batería no son — Eugenio Martínez (quien decide), 2026-11-11, aceptando la propuesta (`docs/mediciones/2026-11-11-aprobacion-fase-b.md`).
  necesarias para recorrerla); (d) no; alguien queda peor (no lee su barra de estado dentro de la app).

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

### #PP24 · 2026-11-11 · La pantalla de actualización obligatoria no tiene salida (requisito de salida) (peso por decidir)

- **Mecanismo:** la pantalla es bloqueante y no ofrece ninguna acción: la persona queda sin nada que pulsar dentro de la
  app [leído]. Quien decide eligió la opción (a) de `#PP5` (`docs/mediciones/2026-11-11-aprobacion-pp5.md`): un botón
  «Actualizar» que abre la tienda, con la URL por plataforma en la config remota; sin URL, sin botón.
- **Quién lo desbloquea:** nosotros (plan propio: contrato de config, DTO, UI con captura en las dos plataformas).
- **Fecha de revisión:** antes de la primera versión publicada de una app derivada.
- **Vigente hasta que:**
  1. `docs/contracts/config-endpoint.md` define la URL de la tienda por plataforma, y el DTO y el mapper la leen (test
     primero);
  2. la pantalla muestra el botón con URL y no lo muestra sin ella (captura en Android e iOS) y el botón abre la tienda.
- **Peso:** (peso por decidir) — propuesta: requisito de salida (fuera de escala): no daña a nadie hoy; impide publicar.

### #PP25 · 2026-11-11 · En Android con el sistema en oscuro, los iconos de la barra de estado no se ven (peso por decidir)

- **Mecanismo:** `enableEdgeToEdge()` elige iconos claros cuando el sistema está en oscuro, pero la app pinta siempre el
  tema claro (`MaterialTheme {}` sin esquema oscuro, `App.kt:24`) [medido 2026-11-11: captura
  `docs/mediciones/2026-11-11-error-red-android.png`, emulador con `night mode: yes`]. Encontrado en la auditoría MD3 de
  `#PP5`: va a la cola, no se corrige ahí.
- **Quién lo desbloquea:** nosotros (o la primera app derivada al poner su tema).
- **Fecha de revisión:** antes de la primera versión publicada de una app derivada.
- **Vigente hasta que:**
  1. con el sistema en oscuro, la barra de estado se lee en Android (captura), sea porque la app tiene tema oscuro o porque
     el estilo de la barra sigue al tema de la app.
- **Peso:** (peso por decidir) — propuesta: 🟡: (a) no; (b) no; (c) quien usa la app pierde la hora y la batería en
  pantalla, fuera de la ruta crítica.

### #PP26 · 2026-11-11 · `skills_de_cierre.py` lee la etapa equivocada si la palabra «Cierre» sale antes en el plan (higiene) (peso por decidir)

- **Mecanismo:** `skills_de_cierre.py:25-26` corta el texto del plan en la PRIMERA aparición de «Cierre» y lee hasta el
  siguiente `## `. Un plan con «Cierre» en el título o en la Parte 1 hace que lea otro trozo, y la lista de cierre
  verdadera puede no leerse [leído; lo reprodujo el auditor sin contexto con un borrador del plan `pp7-validacion`, y lo
  encontró la validación de `#PP7`]. Encontrado durante `#PP7`: va a la cola, no se corrige ahí.
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-11.
- **Vigente hasta que:**
  1. el verificador busca el encabezado de la etapa final (`### Etapa final`), con un caso de prueba de un plan que
     tiene «Cierre» en el título, y su mutación.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

### #PP27 · 2026-11-11 · El recordatorio de UI salta en cada parada tras fusionar un PR que tocó UI (higiene) (peso por decidir)

- **Mecanismo:** `stop-ui-audit-reminder.sh` mira `HEAD~1..HEAD`; tras un merge, `HEAD~1` es el primer padre y el diff
  abarca el PR entero, así que avisa en cada parada hasta el siguiente commit, aunque la auditoría del PR ya se hizo
  [medido 2026-11-11: tras fusionar el PR #41, en esta sesión y en la de validación]. Un aviso que salta sin motivo
  enseña a ignorarlo (como `#PP22`).
- **Quién lo desbloquea:** nosotros.
- **Fecha de revisión:** 2026-12-11.
- **Vigente hasta que:**
  1. el hook no avisa cuando `HEAD` es un merge (o mira solo el árbol sucio y los commits que no están en `develop`), con
     su caso en `hooks_test.sh` sobre un repositorio temporal con un merge.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

## Cerrados

| Id | Qué era | Commit que la cerró |
|---|---|---|
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

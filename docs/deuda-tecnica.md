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

### #PP5 · 2026-11-11 · Los textos de la Splash y las acciones por code no están aprobados (requisito de salida)

- **Mecanismo:** el ejecutor propuso el texto de producto de `shared/src/commonMain/composeResources/values/strings.xml`
  (Splash: carga, actualización obligatoria, error, Reintentar, «Inicio») y las acciones por code de
  `docs/contracts/api-errors.md`; su versión final es de quien decide (manual de operación §6).
- **Quién lo desbloquea:** quien decide.
- **Disparador:** cuando quien decide revise los textos — estado medido 2026-11-11: no revisados. **Fecha de revisión:**
  antes de la primera versión publicada.
- **Vigente hasta que:**
  1. quien decide aprueba o cambia cada texto de `strings.xml` (incluida la copy de cada `ErrorKind` y los
     placeholders de Home y Detail) y los tres de `CrashSafeScreen` (escritos en el código), con su firma;
  2. quien decide aprueba o cambia la acción por code de `api-errors.md`;
  3. quien decide dice qué salida ofrece la pantalla de actualización obligatoria (hoy ninguna: no hay URL de tienda).
     Hasta entonces la persona queda sin salida dentro de la app, que es lo que «bloqueante» pide.
  4. la copy de `NETWORK` («No hay conexión…») afirma una causa que no siempre es la real: también cae ahí un servidor
     que no resuelve (medido 2026-11-11: build `releaseLocal` contra `api.example.invalid` muestra «No hay conexión»
     con la red del emulador funcionando). Manual de operación §10: el texto no afirma lo que no sabe.
- **Peso:** requisito de salida (fuera de escala): no daña a nadie hoy; impide publicar. — Eugenio Martínez (quien decide), 2026-11-11, aceptando la propuesta (`docs/mediciones/2026-11-11-aprobacion-fase-b.md`).

### #PP7 · 2026-11-11 · Las skills y commands del proyecto no están validados en tres pasos (requisito de salida)

- **Mecanismo:** la validación (positiva, control, negativa) exige una sesión NUEVA de Claude Code; la lanzada con
  `claude -p` respondió «Failed to authenticate: OAuth session expired and could not be refreshed» [medido 2026-11-11].
  Las cuatro filas de `.claude/validaciones.md` están en «no pude medir», y por eso `validaciones.py` no está todavía en
  la puerta (saldría rojo y bloquearía todo commit).
- **Quién lo desbloquea:** quien decide (iniciar sesión en la CLI, o correr la validación en una sesión nueva).
- **Disparador:** cuando la CLI tenga sesión válida — estado medido 2026-11-11: caducada.
- **Vigente hasta que:**
  1. las cuatro filas tienen sus tres pasos en «pasó», sobre el hash actual de cada fichero, y
  2. `validaciones.py` está en `VERIFICADORES` de la puerta.
- **Peso:** requisito de salida (fuera de escala): una skill que no se activa no ayuda a nadie, y sin validar no se sabe. — Eugenio Martínez (quien decide), 2026-11-11, aceptando la propuesta (`docs/mediciones/2026-11-11-aprobacion-fase-b.md`).

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

### #PP23 · 2026-11-11 · El job `ios` tarda ~15 min cuando arranca la app: compila el SDK de Firebase desde el código fuente (higiene)

- **Mecanismo:** el paso «Construir la app» compila `firebase-ios-sdk` (SwiftPM) y el framework de Kotlin sin caché
  entre jobs: 20:08:21 → ~20:18 del run 43093127544 [medido: log del job; la app siguió viva a los 15 s]. Con macOS ×10,
  ~150 minutos facturados por cada PR que toque una ruta de arranque [sin medir: la API de facturación pide el permiso
  `user`]. Encontrado durante la fase B: a la cola.
- **Quién lo desbloquea:** nosotros.
- **Disparador:** cuando el primer PR que arranque la app tras fusionar el P2 termine — estado 2026-11-11: implementado
  (`f3f3317`): `ios` restaura el DerivedData y `cache-ios` lo llena desde `develop` (decisión de quien decide); sin medir.
  **Fecha de revisión:** 2026-12-11.
- **Vigente hasta que:**
  1. ~~el job cachea el DerivedData con una clave por `Package.resolved`~~ (`f3f3317`; el DerivedData entero y no solo
     `SourcePackages`, porque lo lento es compilar Firebase, no descargarlo);
  2. el primer PR que arranca la app tras llenarse la caché tarda claramente menos en «Construir la app» que los ~10 min
     en frío (medido en el log).
- **Peso:** fuera de escala (higiene): solo lo ve el equipo, en la factura. — Eugenio Martínez (quien decide), 2026-11-11, aceptando la propuesta (`docs/mediciones/2026-11-11-aprobacion-fase-b.md`).

## Cerrados

| Id | Qué era | Commit que la cerró |
|---|---|---|
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

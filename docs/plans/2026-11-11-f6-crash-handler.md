# Fase 6 · A · `GlobalCrashHandler`

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 6, incremento A (Parte 1 por referencia: el bloque «A)» del prompt). B (breaker + pantalla
  segura) y C (andamiaje) van en sus propias ramas.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `791c985`

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| `core/crash` solo tiene `.gitkeep` | `ls` | HECHO |
| El build release de Android no está firmado (no se puede instalar para la prueba de release local) | `androidApp/build.gradle.kts`: `release` sin `signingConfig` | HECHO → variante **`releaseLocal`** (`initWith(release)`, firma de depuración, engine de release), solo para probar en local; `release` sigue sin firmar (hallazgo 1 del auditor) |
| `adb shell am crash <appId>` hace pasar el crash por el handler no capturado de la app | — | SUPUESTO: lo mide la tarea 3 (si no, la prueba de release usa el andamiaje de C) |
| `NavHost` admite elegir el destino inicial al arrancar | `AppNavHost` (startDestination fijo en Splash) | HECHO → `AppNavHost(startAtHome)` |

### Solución y decisiones

- `core/crash/GlobalCrashHandler` (interfaz, `install()`), inyectada por Koin; `install()` lo llaman las raíces de
  composición (Android en `Application.onCreate`, iOS en `startKoinIos`).
- **Android** (`androidMain`): `Thread.setDefaultUncaughtExceptionHandler`. Depuración → delega en el handler previo (no
  esconde bugs). Release → registra (`Logger.error`) → persiste estado (en A: nada; B añade el breaker) → relanza la
  app en Home (`getLaunchIntentForPackage`, con null-check, `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK` y extra
  `startAtHome = true`) → `exitProcess(0)`.
- **iOS** (`iosMain`): `setUnhandledExceptionHook` con `@OptIn(ExperimentalNativeApi::class)`; **solo registra**, en iOS
  no se recupera. No `NSSetUncaughtExceptionHandler` (exige `staticCFunction`, que no captura closures).
- KDoc honesto: la asimetría (Android recupera, iOS solo registra) y que tras un «reintentar» el PROCESO SOBREVIVE.
- `startAtHome`: `MainActivity` lo lee del intent y `App(startAtHome)` arranca el NavHost en Home sin pasar por la
  Splash.

### Tareas

1. Interfaz e implementaciones (pegamento de plataforma: sin test unitario; su prueba es de comportamiento).
2. Firma temporal de release con la clave de depuración, comentada como `TEMPORAL` y anotada para la Fase 8.
3. **Prueba de comportamiento**: Android release local, crash provocado → la app reabre en Home; Android depuración,
   crash → el proceso muere con el diálogo del sistema (delegó); iOS, crash → queda registrado y la app termina.

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| Release Android: un crash reabre en Home | Comprobación humana declarada | Ejecutor, 2026-11-11, `releaseLocal` en Pixel_10_Pro: `adb shell am crash` → pid 7392 → 7450, logcat `CrashHandler: … se relanza en Home`, capturas `crash-release-{antes,despues}.png` (Error → Home sin Splash). `am crash` sí pasa por el handler (supuesto medido) | Declarado |
| Depuración Android: el crash no se esconde | Comprobación humana declarada | Ejecutor, mismo día, debug: tras `am crash` no queda pid, `FATAL EXCEPTION` en logcat y 0 líneas del handler (delegó) | Declarado |
| iOS: el crash se registra | Comprobación humana declarada | Medido en C: `docs/plans/2026-11-11-f6-andamiaje.md` | Declarado (en C) |
| El handler está en el grafo de las dos plataformas | Verificable | Aserción añadida a `AndroidGraphTest` y `KoinIosTest`; mutación: quitar `single<GlobalCrashHandler>` del módulo de Android → `AssertionError: GlobalCrashHandler` | Pasó |

### Lo que no podrá comprobarse aquí

- Un dispositivo físico; la recuperación en iOS (no existe: es la asimetría declarada).

## Revisiones
- **2026-11-11 · Auditor sin contexto (pasada de la Fase 6).** Condición de salida: sin defecto de forma → se aplica.
  10 hallazgos, ninguno de forma: 1 (firma de depuración en `release`) → variante `releaseLocal` aparte; 2 y 10
  (`commonMain` no sabe si es debug) → `AppBuildInfo(isDebug)` aportado por cada plataforma; 3 (grafos) → aserción y
  mutación; 4 (disparador de iOS) → el criterio de iOS pasa a C; 5 (`startAtHome`) → solo en el primer `onCreate`, y la
  recuperación salta el force-update a propósito (KDoc de `App`); 6 → quién/build/dónde completados; 7–9 → en B.
- 2026-11-11 · Ejecutor: la copy de NETWORK afirma una causa que no siempre es la real → condición 4 de `#PP5`.

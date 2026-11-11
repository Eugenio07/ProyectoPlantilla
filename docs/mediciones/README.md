# Mediciones y capturas

Cada medición o captura se registra en el **índice** de abajo, con una fila por fichero:

| Campo | Qué va |
|---|---|
| Ruta | El fichero, relativo a este directorio |
| Plataforma | Android / iOS / JVM / repo |
| Build | depuración o release |
| Commit | El sha sobre el que se midió |
| Fecha | AAAA-MM-DD HH:MM en la zona del proyecto (America/Caracas) |
| Qué se ve | Para una captura: la clave del texto y el elemento que se ven, **escritos con la imagen abierta** |

Una medición guarda **las líneas que contestan su pregunta**, no todo lo que salió. Una captura se mira entera antes de
commitearla, por si trae un dato personal.

## Índice

| Ruta | Plataforma | Build | Commit | Fecha | Qué se ve / qué contesta |
|---|---|---|---|---|---|
| `2026-11-11-autorizacion-del-arranque.md` | repo | — | `c448186` | 2026-11-11 12:45 | La autorización literal de quien decide para el arranque |
| `2026-11-11-matriz-de-versiones.md` | repo | — | `00b30dd` | 2026-11-11 12:40 | La matriz de versiones con su evidencia (PROMPT 1) |
| `2026-11-11-app-vacia-android.png` | Android (Pixel_10_Pro, emulador) | depuración | `00b30dd` + rama `f0/catalogo` | 2026-11-11 12:52 | La app vacía tras borrar la plantilla del wizard: superficie clara a pantalla completa, sin texto; iconos de la barra de estado ilegibles (`#PP3`) |
| `2026-11-11-app-vacia-ios.png` | iOS (iPhone 17 Pro, simulador) | depuración | `00b30dd` + rama `f0/catalogo` | 2026-11-11 12:50 | La app vacía: superficie clara a pantalla completa, sin texto; barra de estado legible |
| `2026-11-11-splash-and-up-to-date.png` | Android (Pixel_10_Pro, emulador) | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `UP_TO_DATE`: «Inicio» centrado (al día; sin aviso) |
| `2026-11-11-splash-and-update-required.png` | Android (Pixel_10_Pro, emulador) | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `UPDATE_REQUIRED`: «Actualización necesaria» + cuerpo con «999.0.0» (bloqueante, sin acción) |
| `2026-11-11-splash-and-min-version-absent.png` | Android (Pixel_10_Pro, emulador) | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `MIN_VERSION_ABSENT`: «Inicio» (fail-open; aviso `W/ForceUpdate: minVersion ausente…` en el registro) |
| `2026-11-11-splash-and-min-version-malformed.png` | Android (Pixel_10_Pro, emulador) | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `MIN_VERSION_MALFORMED`: «Inicio» (fail-open; aviso `minVersion malformado…`) |
| `2026-11-11-splash-and-server-error.png` | Android (Pixel_10_Pro, emulador) | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `SERVER_ERROR`: «No se pudo completar la carga.» + botón «Reintentar» |
| `2026-11-11-splash-ios-up-to-date.png` | iOS (iPhone 18 Pro, simulador) ¹ | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `UP_TO_DATE`: «Inicio» centrado (al día; sin aviso) |
| `2026-11-11-splash-ios-update-required.png` | iOS (iPhone 18 Pro, simulador) ¹ | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `UPDATE_REQUIRED`: «Actualización necesaria» + cuerpo con «999.0.0» (bloqueante, sin acción) |
| `2026-11-11-splash-ios-min-version-absent.png` | iOS (iPhone 18 Pro, simulador) ¹ | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `MIN_VERSION_ABSENT`: «Inicio» (fail-open; aviso `W/ForceUpdate: minVersion ausente…` en el registro) |
| `2026-11-11-splash-ios-min-version-malformed.png` | iOS (iPhone 18 Pro, simulador) ¹ | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `MIN_VERSION_MALFORMED`: «Inicio» (fail-open; aviso `minVersion malformado…`) |
| `2026-11-11-splash-ios-server-error.png` | iOS (iPhone 18 Pro, simulador) ¹ | depuración | `f7ae93e` + rama `f4/stub-splash` | 2026-11-11 13:20 | Escenario `SERVER_ERROR`: «No se pudo completar la carga.» + botón «Reintentar» |
| `2026-11-11-nav-android-home.png` | Android (Pixel_10_Pro, emulador) | depuración | `8af50aa` + rama `f5/navhost` | 2026-11-11 13:25 | Home tras la Splash (`UP_TO_DATE`): «Inicio» + botón «Ver detalle» |
| `2026-11-11-nav-android-detail.png` | Android (Pixel_10_Pro, emulador) | depuración | `8af50aa` + rama `f5/navhost` | 2026-11-11 13:25 | Detail tras tocar «Ver detalle»: «Detalle 42» (el id de la ruta llegó al ViewModel) |
| `2026-11-11-nav-ios-detail.png` | iOS (iPhone 18 Pro, simulador) | depuración | `8af50aa` + rama `f5/navhost` | 2026-11-11 13:27 | Detail: «Detalle 42» |
| `2026-11-11-nav-ios-home-tras-swipe.png` | iOS (iPhone 18 Pro, simulador) | depuración | `8af50aa` + rama `f5/navhost` | 2026-11-11 13:27 | Home tras un back-swipe desde Detail |

¹ **Corrección 2026-11-11:** estas filas decían «iPhone 17 Pro». Con dos simuladores arrancados, `simctl … booted`
resolvía al iPhone 18 Pro (`7DA267B4…`), que arrancó la tarea `iosSimulatorArm64Test`; medido con
`simctl getenv booted SIMULATOR_UDID`. Desde aquí las capturas de iOS usan el UDID explícito.
| `2026-11-11-error-state-android.png` | Android (Pixel_10_Pro, emulador) | depuración | `643b7ff` + rama `f5/error-state` | 2026-11-11 13:31 | `ErrorState` con `SERVER_ERROR` (code `MAINTENANCE`): «El servicio está en mantenimiento. Inténtalo de nuevo en unos minutos.» + «Reintentar» |
| `2026-11-11-error-state-ios.png` | iOS (iPhone 18 Pro, simulador, UDID explícito) | depuración | `643b7ff` + rama `f5/error-state` | 2026-11-11 13:31 | Lo mismo en iOS |
| `2026-11-11-crash-release-antes.png` | Android (Pixel_10_Pro) | release local (`releaseLocal`, firma de depuración) | `791c985` + rama `f6/crash-handler` | 2026-11-11 13:34 | Antes del crash: sin stub ni backend, `ErrorState` NETWORK («No hay conexión…» + «Reintentar»); pid 7392 |
| `2026-11-11-crash-release-despues.png` | Android (Pixel_10_Pro) | release local | ídem | 2026-11-11 13:34 | Tras `adb shell am crash`: la app reabre en Home («Inicio» + «Ver detalle») sin pasar por la Splash; pid 7450; logcat `CrashHandler: crash no gestionado; se relanza en Home` |
| `2026-11-11-pantalla-segura-android.png` | Android (Pixel_10_Pro) | release local | `2b37d00` + rama `f6/breaker` | 2026-11-11 13:45 | Tras tres `am crash` en ~7 s: «Algo no salió bien», «La app se cerró varias veces seguidas…», «Reintentar» |
| `2026-11-11-andamiaje-android-debug.png` | Android (Pixel_10_Pro) | depuración | `6fd9bc1` + rama `f6/andamiaje` | 2026-11-11 13:44 | Home con «Provocar crash (prueba)» (andamiaje); en `releaseLocal` uiautomator solo ve «Inicio» y «Ver detalle» |
| `2026-11-11-pantalla-segura-ios.png` | iOS (iPhone 18 Pro, UDID explícito) | depuración | `6fd9bc1` + rama `f6/andamiaje` | 2026-11-11 13:42 | Cuarta apertura tras tres crashes del andamiaje en < 60 s: «Algo no salió bien» + «Reintentar»; tras Reintentar, Splash → «Inicio» |
| `2026-11-11-firebase-android-sin-proyecto.md` | Android (Pixel_10_Pro) | depuración | `011e9f5` + rama `f8/firebase-android` | 2026-11-11 14:11 | Tres arranques: sin config (arranca), config + los dos plugins (arranca), config sin el de Crashlytics (crashea) |

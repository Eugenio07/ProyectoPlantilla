---
name: kmp-ios-gotchas
description: Trampas de Kotlin Multiplatform que rompen iOS en esta plantilla (síntoma → causa → solución). Úsala al editar iosMain o código de expect/actual, al añadir una dependencia a commonMain, al tocar el arranque de iOS (iOSApp.swift, startKoinIos, MainViewController), al registrar en Koin algo bi-plataforma, al añadir una ruta de navegación con argumentos, o cuando iOS falla y Android no.
---

# Trampas iOS/KMP de la plantilla

Antes de dar por bueno un cambio que toca iOS, recorre esta lista y di cuál aplica. **La prueba de cualquiera de ellas no
es que compile**: es la puerta (`config/checks/puerta.sh`, que corre los tests de iOS en el simulador) y, si toca el
arranque, arrancar la app en el simulador.

| # | Síntoma | Causa | Solución |
|---|---|---|---|
| 1 | `NoDefinitionFoundException` / `InstanceCreationException` solo en iOS | Algo registrado solo en el módulo de Android | Su equivalente en `iosPlatformModule`; los dos tests de grafo (`AndroidGraphTest`, `KoinIosTest`) |
| 2 | Falla `compileKotlinIosSimulatorArm64` con los tres primeros verdes en verde | Dependencia solo-Android en `commonMain` | Moverla a `androidMain` o usar una KMP (`org.jetbrains.*`) |
| 3 | Errores de ABI/metadata en iOS tras subir una lib | Klib compilado con otra Kotlin | Alinear la Kotlin (matriz de versiones) |
| 4 | API experimental de Native | — | `@OptIn(ExperimentalNativeApi::class)` |
| 5 | «Unresolved reference GlobalContext» en iOS | — | Usar el `Koin` que devuelve `startKoinIos` |
| 6 | «KoinApplication has not been started» | Arranque fuera de orden | `startKoinIos(...)` en el `init()` del `@main`, después de `FirebaseApp.configure()` (solo si hay plist) |
| 7 | «El cambio no funciona» tras un pull | Binario o caché viejos | clean + Invalidate Caches antes de sospechar del código |
| 8 | `Library not loaded … code signature` | `xcodebuild` por CLI sobre el DerivedData de Xcode | `-derivedDataPath` propio siempre |
| 9 | La app iOS muere al arrancar, Android funciona | Un enum como argumento de ruta type-safe | `@Serializable` en el enum + `NavType` con `typeMap`, por `name` |
| 10 | Config nativa en el repo | Xcode fuerza el staging | Copiar desde Finder (`iosApp/iosApp` es un grupo sincronizado); pre-commit y guard |
| 11 | Un parámetro con valor por defecto «no existe» en Swift | Los valores por defecto de Kotlin no cruzan | Todos los argumentos explícitos; `#if DEBUG` en las dos ramas |
| 12 | `suspend` no cruza a una interfaz implementada en Swift | — | Contrato de callback (así está `CrashReporter` / `SwiftCrashReporter`) |
| 13 | `EXC_BAD_ACCESS` dentro de `NSLog` | `String` de Kotlin como `%@` variádico | Componer el mensaje en Kotlin (`IosLogger`); `wiki/trampas/nslog-variadico.md` |
| 14 | `ld: framework 'FirebaseCore' not found` en los tests de iOS | Una lib KMP con cinterop (GitLive) exige el framework nativo | Esa lib en `androidMain`; iOS por Swift; `wiki/trampas/gitlive-ios-enlace.md` |
| 15 | Una captura de iOS es de otro simulador | `simctl … booted` con varios arrancados | UDID explícito; `wiki/trampas/simctl-booted-ambiguo.md` |

Si dos hipótesis compiten y una se confirma, la otra **no** queda refutada.

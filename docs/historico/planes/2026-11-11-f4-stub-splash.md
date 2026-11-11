# Fase 4 · Stub bi-plataforma y Splash

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 4, puntos 5 y 6 (Parte 1 por referencia). Incremento 4 de 4. Escenarios:
  `docs/contracts/config-endpoint.md`, «Escenarios del stub».
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `f7ae93e`

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| `ktor-client-mock` solo está en `commonTest`; `androidApp` no tiene `src/debug` ni `src/release` (hallazgo 9 del auditor) | `shared/build.gradle.kts`; `ls androidApp/src` | HECHO |
| `startKoinIos` es idempotente y fija el engine en el primer arranque (hallazgo 10) | `KoinIos.kt` | HECHO → el escenario se lee **en cada petición**, no al construir el grafo |
| Registrar un ViewModel desde `core/di` haría depender `core` de `ui` | `docs/ARCHITECTURE.md`, «nada depende de `ui`» | HECHO → arranque en el paquete raíz (raíz de composición) |
| No hay recursos de Compose (`composeResources`) | `ls shared/src/commonMain` | HECHO → nacen aquí |

### Solución y decisiones

- `ConfigMockScenario` (`core/network`, común): los cinco escenarios del contrato; **única fuente de los JSON**.
  `selected` se lee en cada petición del `MockEngine`.
- Android: `debugImplementation(ktor-client-mock)`; `provideConfigEngine()` en `src/debug` (stub) y `src/release`
  (`null`); `androidPlatformModule(config, engineOverride)`. Escenario: `--es mockScenario <X>` en depuración.
- iOS: `ktor-client-mock` en `iosMain` (excepción declarada en `docs/ARCHITECTURE.md`);
  `startKoinIos(baseUrl:useMockConfig:isDebug:)` en el paquete raíz; Swift `#if DEBUG` `true/true`, else `false/false`;
  escenario: argumento `-mockScenario <X>`, leído solo en DEBUG.
- `SplashViewModel` (`StateFlow<SplashUiState>`: Loading/Ready/ForceUpdate/Error, `retry()`), `SplashScreen` SIN diseño,
  `uiModule` (ViewModels), `App()` con `koinViewModel()`. Textos: propuestas en `strings.xml` (`#PP5`).
- **Sin `frontend-design`**: v2 pide pantallas sin diseño en el arranque; la dirección estética es de la etapa de producto.

### Tareas

1. **ViewModel, test primero** (`commonTest`, `UnconfinedTestDispatcher` en `Main`): Ready, ForceUpdate, Error, retry.
   Cuerpo falso (se queda en `Loading`): 4 rojos por aserción.
2. Stub por plataforma, raíz de composición, recursos, Splash.
3. **UI: sin test primero, captura real** de cada escenario en las dos plataformas.

### Etapa final · Cierre

- [x] `testAndroidHostTest` + `iosSimulatorArm64Test` + `assembleDebug` + `assembleRelease` [medido 2026-11-11].
- [x] Capturas: 5 escenarios × 2 plataformas en `docs/mediciones/` con su índice.
- [x] Auditoría visual de UI (ranura): `compose-visual-loop` (las capturas) + `/material-3 audit` (Revisiones).
- [x] Cola: `#PP5` (textos y acciones sin aprobar).

### Verificación por criterio

| Criterio | Clase | Evidencia y mutación | Resultado |
|---|---|---|---|
| El ViewModel pasa cada resultado a su estado | Verificable | `UpdateRequired → Ready` en el `when` → cae `olderVersionIsForceUpdate` | Pasó |
| Reintentar vuelve a cargar | Verificable | `retry() = Unit` → cae `retryAfterErrorLoadsAgain` | Pasó |
| El stub no llega a release de Android | Verificable | `:androidApp:dependencies --configuration releaseRuntimeClasspath` → 0 líneas `ktor-client-mock`; brazo de control: `debugRuntimeClasspath` → 2 | Pasó |
| Cada escenario produce su pantalla, en las dos plataformas | Comprobación humana declarada | Ejecutor, 2026-11-11, depuración, Pixel_10_Pro y iPhone 17 Pro: 10 capturas indexadas; los dos fail-open escriben su aviso (logcat / consola); `UPDATE_REQUIRED` → ForceUpdate prueba también que `AppVersionProvider` devuelve `1.0.0` válido en las dos | Declarado |
| `IosLogger` no tumba la app | Verificable (bug) | `IosLoggerTest`: con `NSLog` variádico, `signal 11` (rojo visto); con el arreglo, verde | Pasó |

### Lo que no podrá comprobarse aquí

- El stub apagado en un build release de iOS: solo se construyó Debug.

## Revisiones

- **2026-11-11 · Bug encontrado por la captura, no por los verdes:** en iOS, los dos escenarios de fail-open mataban la
  app (`EXC_BAD_ACCESS` en `NSLog` desde `IosLogger.warn`, leído del informe de crash). Test que lo reproduce primero
  (`IosLoggerTest`, `signal 11`), luego el arreglo (`println`). Los cuatro verdes no lo cazaban: compila ≠ funciona.
- **2026-11-11 · `/material-3 audit`** (subagente, sobre `SplashScreen.kt`, `App.kt`, `strings.xml` y las capturas):
  color, tipografía, forma y componentes pasan; elevación, navegación y movimiento no aplican; layout pasa (ancho
  máximo en tablet sin medir); accesibilidad falla en parte; theming pasa como placeholder. Defectos: (1) iconos de la
  barra de estado de Android ilegibles → ya es `#PP3`; (2) sin `heading()` ni región viva → **aplicado** (encabezado en
  el título de actualización, `liveRegion` en el contenedor); (3) ForceUpdate sin salida → producto, añadido a `#PP5`;
  (4–7) dependen del tema y del diseño futuros. VoiceOver y TalkBack: no pude medir.
- 2026-11-11 · Corrección: las capturas de iOS de este plan son del iPhone 18 Pro, no del 17 Pro (índice de `docs/mediciones/`, nota ¹).

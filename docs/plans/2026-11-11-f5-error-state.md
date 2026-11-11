# Fase 5 · `ErrorState` con el mapeo de codes

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 5, incremento 2 de 2 (Parte 1 por referencia). Contrato: `docs/contracts/api-errors.md`.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `643b7ff`

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| La Splash tiene su propio bloque de error (`splash_error_body`) (hallazgo 8 del auditor de la Fase 5) | `SplashScreen.kt` | HECHO → lo sustituye `ErrorState` |
| `Logger` está en los módulos de plataforma, no en `uiModule` (hallazgo 9) | módulos de Koin | HECHO → `ErrorKindMapper` lo recibe por `get()`; los dos tests de grafo lo resuelven |
| `ui/components` está vacío | `ls` | HECHO |

### Solución y decisiones

- `ErrorKind` (enum con `canRetry`): NETWORK, MAINTENANCE, RATE_LIMITED, UNAUTHORIZED y VALIDATION sin Reintentar
  (propuesta del contrato, `#PP5`), GENERIC. `ErrorKindMapper(logger)`: `AppError` → `ErrorKind`; code desconocido
  (incluido `NETWORK` desde el servidor) → GENERIC y telemetría **del code, nunca del `message`**.
- `ErrorState(kind, onRetry)` a pantalla completa, reutilizable; la copy de cada tipo en `strings.xml` (propuestas,
  `#PP5`). `SplashUiState.Error(kind)`; el ViewModel mapea.

### Tareas

1. **Lógica pura, test primero** (`commonTest`): `ErrorKindMapperTest`, 6 tests. Cuerpo falso (siempre GENERIC): 4
   rojos por aserción; «sin code → GENERIC» y la tabla de `canRetry` nacieron verdes → su prueba es la mutación.
2. Cuerpo real, `ErrorState`, Splash con `ErrorState`, registro y grafos.
3. **UI: captura real** con `SERVER_ERROR` en las dos plataformas.

### Etapa final · Cierre

- [x] `testAndroidHostTest` + `iosSimulatorArm64Test` + cuatro verdes con `--rerun-tasks` [medido 2026-11-11].
- [x] Capturas indexadas; `#PP5` cubre la copy nueva.

### Verificación por criterio

| Criterio | Mutación (`ui/components/…`) → test que cae | Resultado |
|---|---|---|
| Sin red → NETWORK | `Network -> GENERIC` → `networkFailureIsNetwork` | Pasó |
| Cada code conocido, su tipo | `"VALIDATION_ERROR"` → `"VALIDATION"` → `eachKnownServerCodeHasItsKind` | Pasó |
| Code desconocido: telemetría del code | quitar `logger.info` → caen `unknownCode…` y `networkCodeSentByTheServer…` | Pasó |
| …nunca del `message` | `unknownCode(error.code + " " + error.message)` → `unknownCodeIsGeneric…NeverTheMessage` | Pasó |
| Sin code → GENERIC, sin telemetría | `Http -> NETWORK` → `errorsWithoutCodeAreGeneric…` | Pasó |
| Reintentar solo donde puede cambiar algo | `UNAUTHORIZED(true)` y `GENERIC(false)` → `onlyRejections…HideRetry` | Pasó |
| La Splash pinta el tipo mapeado | `SplashViewModelTest` (`Error(ErrorKind.NETWORK)`) | Pasó |
| `ErrorState` en las dos plataformas | Comprobación humana declarada: ejecutor, 2026-11-11, depuración, capturas `error-state-{android,ios}.png` | Declarado |

Todas las filas menos la última son de clase verificable.

## Revisiones

- 2026-11-11 · Ejecutor: una mutación se escribió idéntica al original (no aplicaba nada) y salió verde; se sustituyó por
  la de la filtración del `message`, que sí cae. Es el caso «la mutación no se aplicó» de manual de operación §3: lo
  delata el diff vacío, que es lo que `mutar.sh` imprimirá (Fase 6.5).

# Fase 6 · C · Andamiaje `CRASH-TEST-SCAFFOLD`

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 6, incremento C (Parte 1 por referencia: el bloque «C)» del prompt).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `6fd9bc1`

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| `commonMain` sabe si es depuración (hallazgo 10 del auditor) | `AppBuildInfo` en el grafo de las dos plataformas (incremento A) | HECHO |
| Las pruebas de iOS de A y B esperan un disparador | planes de A y B («pendiente (C)») | HECHO → se miden aquí |

### Solución

- `HomeUiState.showCrashTest = buildInfo.isDebug`; botón «Provocar crash (prueba)» que lanza
  `IllegalStateException`. Texto de herramienta, no de producto: en el código. Cada bloque marcado
  `CRASH-TEST-SCAFFOLD` con su instrucción de retirada (`grep -rn CRASH-TEST-SCAFFOLD` y borrar cada bloque).

### Tareas

1. **ViewModel, test primero**: `crashTestButtonOnlyInDebug`. Cuerpo falso (`false` siempre): rojo por aserción
   («depuración»).
2. Botón en `HomeScreen`. **UI: captura real** en las dos plataformas.

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| El botón existe solo en depuración | Verificable | `crashTestButtonOnlyInDebug` (rojo visto con el stub); el ramo `release` del mismo test es su brazo de control | Pasó |
| …en las dos plataformas | Comprobación humana declarada | Ejecutor, 2026-11-11: Android debug lo muestra, `releaseLocal` no (uiautomator); iOS debug lo muestra (captura de la herramienta del simulador) | Declarado |
| Removible por grep | Verificable | `grep -rn CRASH-TEST-SCAFFOLD shared/src androidApp/src iosApp/iosApp` → 5 líneas, en `HomeViewModel.kt`, `HomeScreen.kt` y `HomeViewModelTest.kt` | Pasó |
| **iOS (de A):** el crash se registra | Comprobación humana declarada | Consola de `simctl launch --console-pty`: `E/CrashHandler: crash no gestionado (iOS: solo se registra) — kotlin.IllegalStateException: CRASH-TEST-SCAFFOLD…` | Declarado |
| **iOS (de B):** 3 crashes + reaperturas en < 60 s → pantalla segura; Reintentar recupera | Comprobación humana declarada | iPhone 18 Pro (UDID explícito), depuración: tres toques + `simctl launch` → la cuarta apertura muestra la pantalla segura (`pantalla-segura-ios.png`); Reintentar → Splash → «Inicio» | Declarado |

## Revisiones

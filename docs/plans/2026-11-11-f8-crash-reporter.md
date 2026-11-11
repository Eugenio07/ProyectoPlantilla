# Fase 8 · `CrashReporter`, handler, listas de release y de reparto

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 8, incremento 3 de 3 (puntos 4, 5, 7, 8 y 9 del prompt). Condición de quien decide: sin proyecto.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `b3bd668`. GitLive solo en Android (incremento 1); Firebase iOS por SwiftPM (incremento 2). Sin Firebase
inicializado, `Firebase.crashlytics` lanza (hallazgo 5b del auditor): el reporter tiene que saberlo.

### Solución

- `core/crash/CrashReporter` (`isActive`, `recordException`) + `NoOpCrashReporter`; KDoc con el trade-off de
  recover-to-home (crashes recuperados = NO FATALES; «Crashes: 0» no es salud).
- Android: `GitLiveCrashReporter` (`isActive = FirebaseApp.getApps(context).isNotEmpty()`). iOS: `SwiftCrashReporter`
  (Swift, SDK nativo; `isActive = FirebaseApp.app() != nil`), pasado a `startKoinIos(…, crashReporter:)`.
- Handler Android release: `recordException` → breaker → **espera acotada `REPORT_FLUSH_WAIT_MS = 300` solo si el
  reporter está activo** → relanza → `exitProcess(0)`. iOS: `recordException` → breaker.
- `docs/android-release-checklist.md`, `docs/ios-release-checklist.md`, `docs/reparto/_plantilla.md` (COMPROBADO /
  PASOS DECLARADOS), en la lista de entregables.

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| El reporter está en el grafo de las dos plataformas | Verificable | `AndroidGraphTest` y `KoinIosTest` (con `NoOpCrashReporter`) lo resuelven | Pasó |
| Android release sin Firebase: no informa y no espera | Comprobación humana declarada | Ejecutor, 2026-11-11, `releaseLocal`: `W/CrashReporter: Firebase no está inicializado…`; relanza 19 ms después | Declarado |
| Android release con Firebase (config de ejemplo): informa y espera | Comprobación humana declarada | Mismo día: `FATAL EXCEPTION` 14:20:07.995 → `Start proc` 14:20:08.341 (346 ms ≥ 300) | Declarado |
| iOS: el hook pasa por el reporter de Swift | Comprobación humana declarada | Simulador, Debug, crash del andamiaje: `E/CrashHandler…` y `W/CrashReporter: Firebase no está configurado…` | Declarado |
| Las listas existen y la puerta las exige | Verificable | `entregables.py`: 40 presentes | Pasó |
| El evento llega al panel | — | **No pude medir**: no hay proyecto (condición de quien decide). Queda en las dos listas de release como paso declarado, con su confirmación previa (escritura irreversible) | No pude medir |

## Revisiones

- 2026-11-11 · Ejecutor: una compilación de Xcode volvió a resolver dos dependencias transitivas de Firebase a versiones
  menores (10.1.1 → 10.1.0, 5.3.1 → 5.3.0); se restauró el `Package.resolved` commiteado (es el fichero de bloqueo).

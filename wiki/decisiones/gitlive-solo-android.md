# GitLive solo en Android; en iOS el `CrashReporter` lo implementa Swift con el SDK nativo (SwiftPM)

- **Firma:** Ejecutor, bajo la autorización del arranque
- **Fecha:** 2026-11-11
- **Estado:** Vigente
- **descartada_porque:** dar a Gradle los frameworks de Firebase para enlazar los tests de iOS: obliga a descargar Firebase solo para testear y es frágil entre versiones del SDK.

**Decisión.** GitLive solo en Android; en iOS el `CrashReporter` lo implementa Swift con el SDK nativo (SwiftPM).

**Por qué.** Con GitLive en `commonMain`, el binario de tests de iOS no enlaza: `ld: framework 'FirebaseCore' not found` (medido por la puerta 2026-11-11).

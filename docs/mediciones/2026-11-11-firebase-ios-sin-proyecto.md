# Firebase en iOS sin proyecto: build y dos arranques

- **Fecha:** 2026-11-11 14:20 (America/Caracas) · **Commit:** `aa7268f` + rama `f8/firebase-ios`
- **Plataforma:** iOS, simulador iPhone 18 Pro (`7DA267B4…`, UDID explícito) · **Build:** Debug, `xcodebuild` con
  `-derivedDataPath` propio.
- **SDK:** `firebase-ios-sdk` 12.19.3 por SwiftPM (FirebaseCore + FirebaseCrashlytics).

| Caso | Resultado (líneas que lo contestan) |
|---|---|
| Build sin `GoogleService-Info.plist` | `** BUILD SUCCEEDED **` · run-script: `Sin GoogleService-Info.plist: no se suben los dSYM (Firebase sin proyecto).` |
| Arranque sin plist | Proceso vivo a los 7 s (`launchctl list`). `FirebaseApp.configure()` no se llama |
| Arranque con `GoogleService-Info.plist.example` copiado a `iosApp/iosApp/` | **Crashea**: `*** Terminating app due to uncaught exception 'com.firebase.installations', reason: '[FirebaseInstallations][I-FIS008000] Could not configure Firebase…` — la clave de ejemplo no tiene el formato que valida FirebaseInstallations. Con un plist real no aplica; el `.example` lo advierte |

La subida de dSYM con un plist real: **no pude medir** (no hay proyecto).

# Firebase en Android sin proyecto: tres arranques

- **Fecha:** 2026-11-11 14:09–14:11 (America/Caracas) · **Commit:** `011e9f5` + rama `f8/firebase-android`
- **Plataforma:** Android, emulador `Pixel_10_Pro` · **Build:** depuración (`installDebug`), con `pm clear` antes.
- **Dependencias:** GitLive `firebase-app` + `firebase-crashlytics` 2.8.0, BoM 34.18.0 (→ Crashlytics 20.1.0).

| Caso | Config | Plugins | Resultado (líneas de logcat que lo contestan) |
|---|---|---|---|
| 1 | sin `google-services.json` | ninguno | **Arranca.** `W FirebaseApp: Default FirebaseApp failed to initialize because no default options were found.` · `I FirebaseInitProvider: FirebaseApp initialization unsuccessful` |
| 2 | `google-services.json.example` (valores falsos) | google-services + crashlytics | **Arranca.** `I FirebaseCrashlytics: Initializing Firebase Crashlytics 20.1.0 for org.example.proyecto_plantilla` · `W FirebaseCrashlytics: … Please set a valid API key.` Gradle: `processDebugGoogleServices`, `injectCrashlyticsMappingFileIdDebug` |
| 3 | el `.example` | **solo** google-services | **Crashea al arrancar.** `java.lang.RuntimeException: Unable to get provider com.google.firebase.provider.FirebaseInitProvider` · `Caused by: java.lang.IllegalStateException: The Crashlytics build ID is missing.` |

Conclusión: los dos plugins van JUNTOS y solo con config (caso 3 es la trampa que cita v2, Fase 8, punto 2).

# Fase 8 · Firebase en iOS (sin proyecto)

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 8, incremento 2 de 3 (punto 3 del prompt). Condición de quien decide: sin proyecto de Firebase.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `aa7268f`. El `pbxproj` no tenía SwiftPM; `iosApp/iosApp` es un grupo sincronizado de Xcode 16 (lo que
se copie ahí entra solo en el target). GitLive no está en iOS (decisión del incremento 1: el reporter de iOS va por
Swift).

### Solución

- `pbxproj`: `XCRemoteSwiftPackageReference` de `firebase-ios-sdk` **12.19.3 exacta** (la de GitLive 2.8.0, matriz),
  `FirebaseCore` y `FirebaseCrashlytics` en el target y en su fase Frameworks; `Package.resolved` versionado.
- Run-script «Crashlytics dSYM» al final del target: si no existe `iosApp/iosApp/GoogleService-Info.plist`, lo dice y sale
  0; si existe, llama al `run` de Crashlytics del paquete.
- `iOSApp.swift`: `FirebaseApp.configure()` SOLO si el plist está en el bundle, y ANTES de `startKoinIos` (trampa 6).
- `iosApp/GoogleService-Info.plist.example` fuera del grupo sincronizado (no se empaqueta), con cómo se instala el real
  (copiado desde Finder, bundle id exacto) y la advertencia medida.

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| Compila y enlaza con el SDK por SwiftPM | Verificable | `xcodebuild … build` → BUILD SUCCEEDED | Pasó |
| Arranca sin plist | Comprobación humana declarada | Ejecutor, 2026-11-11, Debug, iPhone 18 Pro: proceso vivo (`docs/mediciones/2026-11-11-firebase-ios-sin-proyecto.md`) | Declarado |
| El run-script del dSYM se salta sin plist y lo dice | Verificable | log de `xcodebuild`: «Sin GoogleService-Info.plist: no se suben los dSYM…» | Pasó |
| La subida del dSYM con un proyecto real | — | **No pude medir**: no hay proyecto | No pude medir |
| La puerta (tests de iOS incluidos) sigue en verde | Verificable | la entrega pasa por `puerta.sh --commit` | Pasó |

## Revisiones

- 2026-11-11 · Medido: el plist de ejemplo hace crashear el arranque (FirebaseInstallations valida la clave). Se deja
  advertido en el `.example`; no es defecto de la plantilla.

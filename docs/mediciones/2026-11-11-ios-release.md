# iOS en configuración Release (`#PP13`)

- **Fecha:** 2026-11-11 (America/Caracas) · **Commit de partida:** `4c6b662` · **Máquina:** la Mac de verificación,
  Xcode 27.2 (27B52), simulador por UDID.
- **Qué mide:** que la rama `#else` de `iosApp/iosApp/iOSApp.swift` (sin stub, `useMockConfig: false`) y el framework de
  Kotlin en release compilan. Ni la puerta ni el CI construyen Release de iOS: esta medición es **única**, sin guarda
  continua (una por PR costaría otro build de macOS ×10; decisión del plan `audit-p2`).

Comando:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Release \
  -destination "platform=iOS Simulator,id=<UDID>" -derivedDataPath <temporal> CODE_SIGNING_ALLOWED=NO build
```

Resultado:

| Comprobación | Evidencia | Resultado |
|---|---|---|
| Compila | `** BUILD SUCCEEDED **`, 2 min 58 s (DerivedData nuevo) | Pasó |
| Es la rama no-`DEBUG` | `-showBuildSettings` de Release: `SWIFT_ACTIVE_COMPILATION_CONDITIONS` vacío | Pasó |
| El framework de Kotlin es el de release | el log ejecuta `linkReleaseFrameworkIosSimulatorArm64` (vía `embedAndSignAppleFrameworkForXcode`) | Pasó |

**No medido:** que la app Release ARRANQUE (solo se compiló). Lo cubre, para Android, `releaseLocal` (Fase 6).

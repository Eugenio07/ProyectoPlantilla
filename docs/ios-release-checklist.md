# Lista de release · iOS

Pasos que una versión de iOS exige antes de publicarse. Alimenta PASOS DECLARADOS de la lista de reparto.

1. **Proyecto de Firebase**: registrar la app iOS con el bundle id EXACTO (`Config.xcconfig`:
   `org.example.proyectoplantilla.ProyectoPlantilla` + `TEAM_ID`), descargar `GoogleService-Info.plist` y **copiarlo desde
   Finder** a `iosApp/iosApp/` (grupo sincronizado: entra solo en Resources; nunca «Add Files» de Xcode, que fuerza el
   staging). No se commitea. El `.example` NO sirve para probar: con sus valores la app crashea al arrancar.
2. **`TEAM_ID`** en `Config.xcconfig` (firma) y perfil de distribución.
3. **dSYM del primer release**: el run-script «Crashlytics dSYM» sube los símbolos solo si existe el plist; comprobar en
   el panel que la versión tiene sus dSYM (con `DEBUG_INFORMATION_FORMAT = dwarf-with-dsym` en Release).
4. **Prueba en el panel** (escritura irreversible, con confirmación previa): crash de prueba en un build release, reabrir,
   y ver el evento. En iOS el handler solo registra: no hay recuperación.
5. **baseUrl de producción** en `iOSApp.swift` (hoy `https://api.example.invalid/`) y el stub apagado en la rama
   no-`DEBUG` (`useMockConfig: false`).
6. La puerta en verde y el CI en verde (con `#PP6` cerrada), en un PR a `main`.

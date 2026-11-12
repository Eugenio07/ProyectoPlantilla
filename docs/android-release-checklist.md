# Lista de release · Android

Pasos que una versión de Android exige antes de publicarse. Alimenta la sección PASOS DECLARADOS de la lista de reparto
(`docs/reparto/_plantilla.md`): cada paso, con quién, cuándo, sobre qué build y dónde está la evidencia.

1. **Proyecto de Firebase.** Crear el proyecto (o usar el del producto), registrar la app Android con el
   `applicationId` EXACTO (`org.example.proyecto_plantilla`, o el de la app derivada) y descargar
   `google-services.json` a `androidApp/`. **No se commitea** (`.gitignore` + pre-commit). En CI va como secret en base64
   (`GOOGLE_SERVICES_JSON`), con guard de secret vacío ANTES de decodificar y `test -s` después.
2. **`./gradlew :androidApp:verificarFirebase`** → «Firebase configurado». Sin el fichero, el build no lleva Crashlytics
   (los plugins se aplican solo con él).
3. **Keystore real** y `signingConfig` de `release`. La firma de depuración cambia de SHA-1 con cada máquina que
   construye, y el client ID de Google Sign-In (si se usa) la tiene registrada. `releaseLocal` (firma de depuración) es
   solo para probar en local: nunca se distribuye.
4. **R8 / minify** (`isMinifyEnabled = true`) con sus reglas, y la build release arrancada en un dispositivo.
5. **Mapping de Crashlytics**: con minify, el plugin sube el mapping en el build de release; comprobar en el panel que
   la versión tiene su mapping.
6. **Prueba en el panel** (ESCRITURA IRREVERSIBLE: confirmación de quien decide antes, con lo que va a quedar en el panel
   —cuántos crashes, de qué build, con qué marca—): build release, `adb shell am crash <appId>`, REABRIR la app, y ver el
   evento en el panel. Recordar el trade-off: con recover-to-home, el crash recuperado llega como NO FATAL; «Crashes: 0»
   no significa salud.
7. **baseUrl de producción** en `BuildConfig.BASE_URL` (hoy `https://api.example.invalid/`).
8. **`storeUrl.android` real** en la config de producción (`#PP24`): con una versión de prueba por debajo de `minVersion`,
   «Actualizar» abre la ficha de ESTA app en Play.
9. La puerta en verde y el CI en verde (con `#PP6` cerrada), en un PR a `main`.

# Proyecto Plantilla

Plantilla de Kotlin Multiplatform para construir apps de Android e iOS con **una sola UI en Compose Multiplatform**. Trae
la arquitectura (Clean, en el módulo `shared`), la red (Ktor + Ktorfit), la inyección (Koin), la navegación, el
force-update, el manejo de crashes con su breaker y Firebase preparado. También trae las defensas que verifican todo
eso: la puerta, el CI, los hooks y la cola.

Las versiones viven **solo** en [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Antes de nada, en cada clon

```bash
git config core.hooksPath .githooks
```

Activa el pre-commit, que rechaza secretos y la configuración nativa de Firebase (`google-services.json`,
`GoogleService-Info.plist`, `local.properties`, keystores).

## Dónde está cada cosa

| Qué | Dónde |
|---|---|
| Las reglas de trabajo (cada una con su prueba) | [`CLAUDE.md`](CLAUDE.md) y su porqué en [`wiki/`](wiki) |
| Cómo se montó la plantilla y cómo se trabaja en ella | [`docs/manuales/`](docs/manuales): el de arranque y el de operación |
| La arquitectura y sus excepciones | [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) |
| La cola de trabajo pendiente | [`docs/deuda-tecnica.md`](docs/deuda-tecnica.md) |
| Los planes en curso y los ya fusionados | [`docs/plans/`](docs/plans) · [`docs/historico/planes/`](docs/historico/planes) |
| Los contratos de la API (borradores, sin backend) | [`docs/contracts/`](docs/contracts) |
| Las mediciones con su fecha | [`docs/mediciones/`](docs/mediciones) |
| Las listas de publicación | [`docs/android-release-checklist.md`](docs/android-release-checklist.md) · [`docs/ios-release-checklist.md`](docs/ios-release-checklist.md) |

## Código

- [`shared/`](shared/src): todo el código común. `commonMain` (dominio, datos, UI), `androidMain` e `iosMain` (lo que
  depende de la plataforma) y sus tests (`commonTest`, `androidHostTest`, `iosTest`).
- [`androidApp/`](androidApp): la app de Android. Variantes: `debug` (con el stub de configuración), `release` y
  `releaseLocal` (release firmada con la clave de depuración, solo para probar en local).
- [`iosApp/`](iosApp): el proyecto de Xcode y el arranque en Swift.

## Correr las apps

- **Android:** `./gradlew :androidApp:installDebug`, o el botón de ejecutar de Android Studio. En depuración, el stub de
  configuración se elige al lanzar la app:
  `adb shell am start -n org.example.proyecto_plantilla/.MainActivity --es mockScenario <ESCENARIO>`.
- **iOS:** abre `iosApp/iosApp.xcodeproj` en Xcode y ejecuta el esquema `iosApp` en un simulador. En depuración, el
  argumento `-mockScenario <ESCENARIO>` elige el stub.

Los escenarios están en `ConfigMockScenario`, que es la única fuente de los JSON del stub.

## Verificar: la puerta

```bash
config/checks/puerta.sh
```

Pasa, en este orden:

1. el formato;
2. detekt;
3. los verdes, en una sola invocación de Gradle: commonMain, la app de Android en debug y en release, los tests JVM,
   Kotlin/Native y los tests de iOS en el simulador;
4. los verificadores de `config/checks/`;
5. el recuento de tests por plataforma;
6. los marcadores de conflicto.

Cualquier salida distinta de 0 es rojo, también el 3 («no pude medir»). Para commitear se usa
`config/checks/puerta.sh --commit <fichero-del-mensaje> [nuevos…]`: solo commitea si la puerta pasa. El CI de GitHub
(`.github/workflows/ci.yml`) repite lo que puede en los runners alojados.

## Firebase

La plantilla compila y arranca **sin proyecto de Firebase**. Con `androidApp/google-services.json` se aplican los plugins
de Google Services y Crashlytics. Con `GoogleService-Info.plist` en `iosApp/iosApp/`, iOS configura Firebase al arrancar.

Los `.example` muestran la forma de esos ficheros, pero no sirven para probar: un plist con valores de ejemplo hace
crashear iOS. Antes de publicar, ejecuta `./gradlew :androidApp:verificarFirebase` y sigue las listas de publicación.

## Ramas

`develop` es la rama de trabajo y `main` la de versiones. Cada tarea va en su rama, con un PR contra `develop`, y se
fusiona a mano cuando el CI está en verde (`CLAUDE.md` §9).

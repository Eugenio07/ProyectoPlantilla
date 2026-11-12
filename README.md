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

## Crear un proyecto nuevo desde la plantilla

Haz los pasos en este orden. Los marcados con **(PR)** van en commits de la primera rama del proyecto nuevo, con la
puerta. En los ejemplos, el proyecto nuevo se llama «Mi App», su paquete es `com.miempresa.miapp` y su `rootProject.name`
es `MiApp`.

### 1. El repositorio

1. Una sola vez, en GitHub, marca este repositorio como plantilla (*Settings → Template repository*).
2. Crea el nuevo con todas las ramas, para que traiga `develop` y `main`:

   ```bash
   gh repo create miempresa/mi-app --private --template Eugenio07/ProyectoPlantilla --include-all-branches --clone
   ```

3. En el repositorio nuevo, pon `develop` como rama por defecto. Si el token no puede subir workflows:
   `gh auth refresh -h github.com -s workflow`.
4. En el clon, una sola vez: `git config core.hooksPath .githooks`.
5. Abre el proyecto en Android Studio, que crea el fichero local con la ruta del SDK (no se versiona). Sin él, Gradle
   falla con «SDK location not found». Desde la terminal, basta con exportar `ANDROID_HOME`.

### 2. Renombrar (PR, una rama propia, un solo commit)

El nombre de la plantilla aparece en:
- el paquete de Kotlin, con sus carpetas;
- el `applicationId`;
- el bundle id de iOS;
- el nombre de la app;
- el paquete de recursos de Compose, que sale de `rootProject.name`;
- el CI.

Desde la raíz:

```bash
VIEJO=org.example.proyecto_plantilla; NUEVO=com.miempresa.miapp
# 1) Las carpetas del paquete, en cada source set
for d in $(git ls-files | grep '/kotlin/org/example/proyecto_plantilla/' | sed 's#\(.*/kotlin/\)org/example/proyecto_plantilla/.*#\1#' | sort -u); do
  mkdir -p "$d$(dirname "${NUEVO//.//}")" && git mv "${d}org/example/proyecto_plantilla" "$d${NUEVO//.//}"
done
# 2) Los textos, solo en ficheros de texto vivos (docs/historico y docs/mediciones son registros con fecha: no se tocan)
git grep -lzI -e "$VIEJO" -e "${VIEJO//.//}" -- . ':!docs/historico' ':!docs/mediciones' |
  xargs -0 sed -i '' -e "s/${VIEJO//./\\.}/$NUEVO/g" -e "s#${VIEJO//.//}#${NUEVO//.//}#g"
git grep -lzI -e 'proyectoplantilla\.shared' -- . ':!docs/historico' ':!docs/mediciones' |
  xargs -0 sed -i '' 's/proyectoplantilla\.shared/miapp.shared/g'
# 3) Las carpetas vacías que deja el paso 1 (git no las rastrea, pero quedan en disco)
find . -type d -empty -path '*/kotlin/org*' -delete
```

Probado el 2026-11-11 sobre una copia de la plantilla: con `rootProject.name` cambiado, compila Android y Kotlin/Native
para iOS, y pasan los 85 tests JVM.

Y a mano:

| Fichero | Qué |
|---|---|
| `settings.gradle.kts` | `rootProject.name = "MiApp"`. En minúsculas da el paquete de recursos (`miapp.shared.generated.resources`) |
| `androidApp/src/main/res/values/strings.xml` | `app_name` |
| `iosApp/Configuration/Config.xcconfig` | `PRODUCT_NAME` y `PRODUCT_BUNDLE_IDENTIFIER` (el bundle id de iOS no lleva guion bajo) |
| `iosApp/iosApp.xcodeproj/project.pbxproj` y `.github/workflows/ci.yml` | `Proyecto Plantilla.app` → `Mi App.app`. En `ci.yml`, también el bundle id del paso que cierra la app |
| `.editorconfig`, `config/detekt/detekt.yml`, `CLAUDE.md` §13 y `wiki/decisiones/paquete-con-guion-bajo.md` | Si el paquete nuevo **no** lleva guion bajo, quita la excepción |

Comprueba que no queda nada:
`git grep -nI "proyecto_plantilla\|proyectoplantilla\|ProyectoPlantilla\|Proyecto Plantilla" -- . ':!docs/historico' ':!docs/mediciones' ':!docs/manuales'`.
Después, `config/checks/puerta.sh`.

**Ojo:** el renombrado cambia `.claude/commands/plantilla-contract-sync.md`, así que `validaciones.py` pondrá la puerta en
rojo hasta que lo valides otra vez, con el bloque B de `.claude/validacion-sesion-nueva.md` en una sesión nueva de Claude
Code. Es lo esperado: cambiar una pieza caduca su validación.

### 3. Lo que es del producto (PR, por tareas)

- **Las ranuras de `CLAUDE.md` §2:** ruta crítica, activo crítico y dónde vive, backend, si está publicada, revisor
  externo y bandeja. Se rellenan antes de la primera ficha.
- **El backend:**
  - `baseUrl` en `androidApp/build.gradle.kts` (`BASE_URL`) y en `iosApp/iosApp/iOSApp.swift`;
  - los contratos de `docs/contracts/` pasan de borrador a la rama B o C (`CLAUDE.md` §12);
  - el backend envía `minVersion` siempre, y `storeUrl` con las fichas de las dos tiendas.
- **La marca:**
  - los colores de `ui/theme/AppTheme.kt`, que son provisionales, con dirección de `frontend-design` y la auditoría
    visual;
  - el icono de la app.
- **Los textos provisionales** (`CLAUDE.md` §2): `strings.xml`, `CrashSafeScreen` y las acciones de
  `docs/contracts/api-errors.md`.
- **Firebase:** el proyecto, la configuración de Android y la de iOS (el plist se copia desde Finder). Sigue las dos
  listas de release.
- **La cola:** `docs/deuda-tecnica.md` viaja con las entradas de la plantilla. Quédate con las que apliquen y usa un
  prefijo propio para las nuevas (por ejemplo, `#MA1`), porque `cola.py` no deja reutilizar los identificadores del
  historial.
- **Este README:** sustituye esta sección por la del producto.

### 4. Antes de publicar

Sigue las listas de release: `docs/android-release-checklist.md` y `docs/ios-release-checklist.md`. Incluyen el keystore,
R8, Crashlytics, `verificarFirebase`, la `baseUrl` de producción y comprobar que «Actualizar» abre la ficha real.

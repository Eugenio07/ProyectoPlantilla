# Botón «Actualizar» en la pantalla de actualización obligatoria (#PP24)

- **Estado:** Plan aprobado
- **Tamaño:** M
- **Entrada:** la cola, `#PP24` (requisito de salida, peso firmado). La decisión de producto es la opción (a) de `#PP5`
  (`docs/mediciones/2026-11-11-aprobacion-pp5.md`).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, tras la pasada del auditor; granularidad: este plan;
  literal en *Revisiones*)

## Parte 1 · Spec

**Qué y para quién.** Hoy la pantalla de actualización obligatoria bloquea sin ofrecer nada: quien usa la app no tiene
nada que pulsar. Con esta tarea mostrará un botón «Actualizar» que abre la ficha de la app en la tienda de su plataforma.
La URL llega en la config remota, una por plataforma. **El mecanismo es de la plantilla; las URLs las pone cada app
derivada** en su backend.

**Antecedentes:** `docs/historico/planes/2026-11-11-pp5-revisor.md:74` y `docs/mediciones/2026-11-11-aprobacion-pp5.md`
(dónde se eligió la opción a); sin antecedentes en `wiki/decisiones/` ni en la cola, fuera de la propia `#PP24`.

**Invariantes:** no cambia la decisión de bloquear (la tolerancia de `minVersion` y su fail-open siguen igual); una URL
ausente o mala nunca rompe nada (sin botón, como hoy, y un aviso; nunca decide si se bloquea); la URL es la de la
plataforma que corre (Android la de `android`, iOS la de `ios`; nunca se cruzan).

**Alcance:** del contrato a la pantalla, el stub, la copy y un paso en las listas de release. **Fuera:** los esquemas
propios de las tiendas (`market://`, `itms-apps://`): un `https://` ya lo abre la tienda o el navegador.

**Flujo:** la Splash pide la config → el caso de uso devuelve `UpdateRequired(minVersion, storeUrl?)` → la pantalla
sale con botón o sin él → al pulsarlo, `viewModel.openStore(uriHandler::openUri)`.

**Datos y reglas:** contrato `"storeUrl": {"android": "https://…", "ios": "https://…"}`, opcional. URL válida: empieza
por `https://`, sin espacios en blanco y con host (algo entre `https://` y el siguiente `/`). Lo que no es una cadena (un
número, un objeto, o un `storeUrl` que no es un objeto) llega como su texto JSON, como hoy `minVersion`: se rechaza como
**inválido**, no como ausente. La plataforma que corre la aporta Koin (`AppPlatform`).

**Casos alternativos:**

| Caso | Pantalla | Aviso |
|---|---|---|
| `storeUrl` ausente o `null` | sin botón | `WARN_STORE_URL_ABSENT` |
| Solo trae la URL de la otra plataforma | sin botón | `WARN_STORE_URL_ABSENT` |
| `http://`, `javascript:…`, vacía, con espacios, sin host, otro tipo, o `storeUrl` no es un objeto | sin botón | `WARN_STORE_URL_INVALID` |
| `openUri` lanza una excepción | no crashea | `WARN_STORE_OPEN_FAILED` |

**Criterios de aceptación:**
- **A1.** El mapper lleva al modelo las dos URLs crudas, de cada forma del contrato.
- **A2.** El caso de uso pone en `UpdateRequired` solo la URL válida de la plataforma que corre. Si no hay ninguna,
  `null`, con el aviso de su motivo. `UpToDate` no avisa por la URL.
- **A3.** El ViewModel pasa la URL a `ForceUpdate`. `openStore` abre la URL y, si falla, avisa sin propagar el error.
- **A4.** Con URL, la pantalla muestra «Actualizar», y al pulsarlo se abre la URL. Sin URL, sale como hoy.
- **A5.** Los grafos de Koin de las dos plataformas resuelven `AppPlatform` con su valor.
- **A6.** Las dos listas de release dicen que hay que comprobar, en la primera versión, que «Actualizar» abre la ficha
  real.

**Decisiones pendientes:** ninguna. La autorización y el cierre de `#PP24` (reformulado) ya los decidió quien decide
(*Revisiones*).

## Parte 2 · Plan

### Verificación previa

Contra el commit: `1cc5146` (`develop`). Rama medida: `feature/pp24-boton-tienda`.

| Afirmación | Cómo se comprobó | Ramas medidas | Resultado | HECHO / SUPUESTO |
|---|---|---|---|---|
| DTO, modelo y estados solo llevan `minVersion` | lectura de `AppConfigDto.kt`, `AppConfig.kt`, `ForceUpdateStatus.kt` y `SplashUiState.kt` | la rama | sí | HECHO |
| `AppConfig(…)` se construye en `ConfigRepositoryImplTest.kt:51,57` y `ConfigRepositoryIosTest.kt:46`, con un solo argumento | `git grep -n "AppConfig(" -- shared` | la rama | sí: las propiedades nuevas llevan `= null` | HECHO |
| El caso de uso se construye en `DataModule.kt:26` y en dos tests | `git grep "CheckForceUpdateUseCase("` | la rama | sí: cambian los tres | HECHO |
| `SplashViewModel(get(), get())` y `SplashContent` no tienen `Logger` | lectura de `UiModule.kt` y `SplashScreen.kt:45-49` | la rama | sí: el ViewModel recibe `Logger` | HECHO |
| `core/platform` solo tiene `AppBuildInfo` y `AppVersionProvider` | `ls` | la rama | sí: `AppPlatform` es nuevo | HECHO |
| En iOS es Kotlin quien construye `AppBuildInfo` | lectura de `KoinIos.kt:40` | la rama | sí | HECHO |
| `LocalUriHandler` existe en el klib de CMP para iOS | el auditor lo encontró con `strings` en `ui-iosSimulatorArm64Main` | — | sí | HECHO |
| `UPDATE_REQUIRED` es `{"minVersion":"999.0.0"}` y `ConfigMockScenario` es la única fuente del stub; el stub solo existe en depuración | lectura | la rama | sí: las capturas son de builds de depuración | HECHO |
| `barrido_texto.py` marca «no se puede» en `splash_force_update_body` | ejecución (el auditor) | la rama | sí: con un botón al lado sigue siendo cierto («Instala la versión X o una más reciente») | HECHO |
| En el simulador de iOS, `openUri` con `https://apps.apple.com/…` abre Safari | — | — | — | SUPUESTO: lo mide la captura tras pulsar |

¿Qué de lo que asumo lo borra este plan? Nada (`UPDATE_REQUIRED` cambia de cuerpo, no de nombre ni de efecto).

### Solución y decisiones

- **`AppPlatform { ANDROID, IOS }`** va en `core/platform` y se registra en cada módulo de plataforma. No va dentro de
  `AppBuildInfo`, porque la plataforma no es un dato del build, y un tipo propio se puede comprobar en los tests de
  grafo de cada plataforma.
- **Modelo:** `AppConfig(minVersion, storeUrlAndroid: String? = null, storeUrlIos: String? = null)`.
- **DTO:** `storeUrl: JsonElement? = null`.
- **Mapper:** un objeto → cada clave como `minVersion` (cadena: su contenido; otra cosa: su texto JSON); otro tipo → su
  texto JSON a las dos plataformas, que se rechaza como inválido.
- **La validación** vive en el caso de uso, con tres avisos: `WARN_STORE_URL_ABSENT`, `WARN_STORE_URL_INVALID` y
  `WARN_STORE_OPEN_FAILED`. Este último lo emite el ViewModel.
- **Abrir la URL** es `SplashViewModel.openStore(open: (String) -> Unit)`: hace `runCatching` y avisa por `Logger`. Así
  es una lógica con test, no algo escondido en un composable.
- **UI:** la copy es `action_update` = «Actualizar» en `shared/src/commonMain/composeResources/values/strings.xml`; un
  `Button` relleno con los tokens del componente (`primary`/`onPrimary`, forma completa), como «Reintentar» en
  `ErrorState`; lo separa el `spacedBy(12.dp)` que ya existe.

### Mapa de ficheros

| Fichero | Tarea |
|---|---|
| `docs/plans/2026-11-11-pp26-pp27.md` → `docs/historico/planes/` | 0 |
| `docs/contracts/config-endpoint.md` · `AppConfigDto.kt` · `AppConfigMapper.kt` · `AppConfig.kt` · `AppConfigMapperTest.kt` | 1 |
| `core/platform/AppPlatform.kt` (nuevo) · `ForceUpdateStatus.kt` · `CheckForceUpdateUseCase.kt` · `CheckForceUpdateUseCaseTest.kt` · `DataModule.kt` · `AndroidPlatformModule.kt` · `IosPlatformModule.kt` · `AndroidGraphTest.kt` · `KoinIosTest.kt` | 2 |
| `SplashUiState.kt` · `SplashViewModel.kt` · `UiModule.kt` · `SplashViewModelTest.kt` | 3 |
| `SplashScreen.kt` · `composeResources/values/strings.xml` · `ConfigMockScenario.kt` · tabla del stub de `config-endpoint.md` · `docs/android-release-checklist.md` · `docs/ios-release-checklist.md` · capturas e índice | 4 |

### Tareas

0. **Mover al histórico** el plan del PR #44 (documento).
1. **Contrato, DTO y mapper** (lógica pura: test primero). Para que compile, el modelo con las propiedades nuevas en
   `null` y el mapper sin rellenarlas. Los tests caen por la aserción: `bothStoreUrlsAreMapped`, `onlyOnePlatformUrl`,
   `nonStringUrlKeepsItsJson`, `storeUrlNotAnObjectGoesToBothAsJson`; `absentStoreUrlIsNull` pasa (control).
2. **Caso de uso y plataforma** (test primero). `AppPlatform` y la firma nueva, con la selección vacía (siempre `null`).
   Caen por la aserción: `androidGetsItsUrl`, `iosGetsItsUrl` (URLs distintas por plataforma),
   `otherPlatformOnlyIsAbsentWithWarning`, `httpIsInvalid`, `javascriptIsInvalid`, `emptyIsInvalid`,
   `spacesAreInvalid`, `noHostIsInvalid`; `upToDateDoesNotWarnAboutUrl` es el control. Grafos:
   `assertEquals(AppPlatform.ANDROID, …)` en `AndroidGraphTest` e `IOS` en `KoinIosTest`.
3. **ViewModel** (test primero): `forceUpdateCarriesStoreUrl`, `openStoreCallsOpenWithUrl`,
   `openStoreFailureWarnsAndDoesNotThrow`.
4. **Pantalla, stub y listas** (UI: sin test primero, captura real). El botón; `UPDATE_REQUIRED` con las dos URLs y
   `UPDATE_REQUIRED_NO_STORE` sin ellas (enum y tabla del contrato); el paso nuevo en las dos listas de release.
   Capturas en **depuración** (el stub solo existe ahí), en Android e iOS: con botón, sin botón y tras pulsarlo, en
   claro y en oscuro; `/material-3 audit`.

### Etapa final · Cierre

- [ ] Capturas en `docs/mediciones/` con su índice
- [ ] La puerta entera con `--rerun-tasks`
- [ ] Auditoría visual de UI: `compose-visual-loop` (captura en Android e iOS) y `/material-3 audit` (10 categorías)
- [ ] Revisión de pre-PR con `/plantilla-review`; `/plantilla-contract-sync` (el DTO cambia)
- [ ] Overviews: no aplica (la plantilla no tiene áreas)
- [ ] `barrido_texto.py`: `splash_force_update_body` sigue siendo cierto con el botón
- [ ] Cola: `#PP24` cerrada con la condición reformulada

### Verificación por criterio

| Criterio | Clase | Mutación (fichero, cambio, lo que cae) · control | Resultado |
|---|---|---|---|
| A1 | Verificable | `AppConfigMapper.kt`: `null` en la URL de `ios` → cae `bothStoreUrlsAreMapped` · control: `absentStoreUrlIsNull` sigue verde | |
| A2 · plataforma | Verificable | elegir siempre la URL de `android` → cae `iosGetsItsUrl` · control: `androidGetsItsUrl` sigue verde | |
| A2 · validez | Verificable | quitar la comprobación de `https://` → caen `httpIsInvalid` y `javascriptIsInvalid`; la de espacios → cae `spacesAreInvalid`; la de host → caen `noHostIsInvalid` y `emptyIsInvalid` | |
| A3 | Verificable | `ForceUpdate(status.minVersion, null)` → cae `forceUpdateCarriesStoreUrl`; quitar el `runCatching` → cae `openStoreFailureWarnsAndDoesNotThrow` | |
| A4 | Comprobación humana declarada | el ejecutor, sobre builds de depuración con `UPDATE_REQUIRED` y `UPDATE_REQUIRED_NO_STORE`, en Android (emulador) e iOS (simulador por UDID); capturas e informe M3 en `docs/mediciones/` | |
| A5 | Verificable | registrar `IOS` en el módulo de Android → cae `AndroidGraphTest`; registrar `ANDROID` en el de iOS → cae `KoinIosTest` | |
| A6 | Comprobación por lectura | el diff de las dos listas | |

### Lo que no podrá comprobarse aquí

- Que la URL abra la ficha real en Play o en el App Store: no hay ficha publicada. Lo comprueba cada app derivada con el
  paso de A6.

## Revisiones

- **2026-11-11 · Entrada.** Quien decide aceptó la opción (a) en `#PP5` («te acepto las recomendaciones») y sobre `#PP24`
  dijo: «Sí, empieza».
- **2026-11-11 · Auditor sin contexto, primera pasada: 15 puntos.**
  - **La autorización iba antes de la auditoría.** Se pasa a «No» y se le pregunta a quien decide.
  - **«El botón abre la tienda» no es medible.** Pasa a decisión pendiente, y se añade A6.
  - **Tests concretos para la plataforma y para cada regla de validez**, con su mutación.
  - **El tipo raro se trata como inválido, no como ausente**, con su aviso.
  - **`openUri` que falla.** Se saca al ViewModel, con test.
  - **Defaults de `AppConfig`.**
  - **El barrido**, aclarado.
  - **Decisiones pendientes.**
  - **Antecedentes.**
  - **El motivo de `AppPlatform`**, corregido.
  - **Brazos de control** en cada criterio.
  - **El build de las capturas.**
  - **`LocalUriHandler`** pasa a medido.
  - **El fichero de copy.**
  - **La tabla del stub.**
  - **El plan anterior al histórico.**
  - **Los tokens del botón.**
- **2026-11-11 · Decisiones de quien decide** (literales): «Sí, impleméntalo»; y sobre el cierre: «Reformular y cerrar
  (Recomendado)». `#PP24` se cierra con «abre la URL de su plataforma (medido en emulador y simulador)» más el paso de
  las listas de release (A6).

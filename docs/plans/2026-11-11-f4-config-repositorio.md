# Fase 4 · DTO → repositorio de config

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 4, punto 1 (Parte 1 por referencia). Incremento 1 de 4 (§0.1). Contrato:
  `docs/contracts/config-endpoint.md`.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `64023e4`. Antecedentes: la cola tiene `#PP1` (Ktorfit sobre Kotlin 2.4.21), cuyo disparador es este
incremento.

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| `shared` aplica los plugins KSP y Ktorfit y depende de `ktorfit-lib` | `shared/build.gradle.kts` | HECHO |
| No hay ninguna interfaz Ktorfit ni DTO | `grep -rn "@GET\|@Serializable" shared/src/commonMain` | HECHO: solo `ErrorEnvelope` (privado del mapper) |
| `HttpClient` y `NetworkConfig` están en el grafo de las dos plataformas | `AndroidGraphTest`, `KoinIosTest` | HECHO |
| El plugin de Ktorfit genera el código de la interfaz para `commonMain` sin configurar KSP a mano | — | **SUPUESTO**: lo mide la tarea 1 (compila o no) |
| El plugin de compilador de Ktorfit (2.3.5, que elige la librería 2.7.5) funciona con Kotlin 2.4.21, y KSP se engancha al target Android de `com.android.kotlin.multiplatform.library` | — | **SUPUESTO** (`#PP1`): lo miden la tarea 2 (JVM) y la 3 (simulador). Medido después: sí (abajo) |
| `runBlocking` existe en `iosTest` (Kotlin/Native) | — | **SUPUESTO**: lo mide la tarea 3 |

Antecedentes: sin antecedentes en `docs/historico/`; `wiki/decisiones/` no existe todavía (Fase 9).

### Mapa de ficheros

`data/remote/dto/AppConfigDto.kt` · `data/remote/mapper/AppConfigMapper.kt` · `data/remote/api/ConfigApi.kt` ·
`data/repository/ConfigRepositoryImpl.kt` · `domain/model/AppConfig.kt` · `domain/repository/ConfigRepository.kt` ·
`core/di/DataModule.kt` (nuevo) y `CoreModule.kt` (lo registra) · tests: `commonTest/…/AppConfigMapperTest.kt`,
`androidHostTest/…/ConfigRepositoryImplTest.kt`, `iosTest/…/ConfigRepositoryIosTest.kt`, y `ConfigRepository` en los dos
tests de grafo.

### Solución y decisiones

- `AppConfigDto(minVersion: JsonElement? = null)`. **`JsonElement` y no `String?`**: el contrato dice que un número en
  `minVersion` es «malformado» (fail-open), y con `String?` un número haría fallar la deserialización entera
  (`Serialization` → Reintentar), que es el caso contrario.
- `AppConfigMapper`: `minVersion` → `String?`: ausente o `null` → `null`; string → su contenido; número → su texto
  (nunca es `x.y.z`, así que `isValid` lo rechazará); objeto o array → su JSON (ídem). La validación es del caso de uso
  (incremento 3), no del mapper.
- `domain/model/AppConfig(minVersion: String?)`; `domain/repository/ConfigRepository`;
  `data/repository/ConfigRepositoryImpl` con `safeApiCall`. **Sin `GetAppConfigUseCase`**: no decidiría nada que el
  repositorio no pueda (v2 FASE 4, punto 1).
- `data/remote/api/ConfigApi` (Ktorfit, `@GET("v1/config")`, ruta relativa al `baseUrl`). `Ktorfit` y el repositorio
  en un `dataModule` común.

### Tareas (orden por capa)

1. **Mapper, lógica pura, test primero** (`commonTest`: corre en JVM y en el simulador): string, ausente, `null`, número,
   objeto, array. Cuerpo falso (`→ null`): rojo por aserción.
2. **Repositorio, test primero con doble de red** (`androidHostTest`, `MockEngine` con `Content-Type: application/json`
   + Ktorfit real + `runBlocking`): config válida (y la ruta `/v1/config`), `{}`, `503` + envelope, cuerpo vacío.
   Cuerpo falso (`Error(Unknown)`): rojo por aserción.
3. Cuerpos reales; `dataModule` registrado en `initKoin`; `ConfigRepository` en los dos tests de grafo; `iosTest` con
   Ktorfit en el simulador y su brazo de control.

### Etapa final · Cierre

- [x] Los cuatro verdes con `--rerun-tasks` + `:shared:iosSimulatorArm64Test` [medido 2026-11-11].
- [x] `#PP1`: sin motivo, entera (abajo); se cierra con el sha de la fusión en el primer commit del incremento siguiente.
- No aplican: capturas, auditoría visual, barrido.

### Verificación por criterio

| Criterio | Clase | Evidencia y mutación | Resultado |
|---|---|---|---|
| Rojo por aserción antes del código | Verificable | `26 tests completed, 8 failed`, todos `AssertionError`. `absentKeyIsNull` y `jsonNullIsNull` nacieron verdes (el stub daba `null`): su prueba es la mutación de la rama `null` | Pasó |
| `null` y clave ausente → `null` | Verificable | `null -> "ausente"` → caen `absentKeyIsNull`, `jsonNullIsNull`, `emptyObjectIsSuccessWithNullMinVersion` | Pasó |
| Número → su texto | Verificable | `is JsonPrimitive -> if (v.isString) v.content else null` → cae `numberKeepsItsText…` | Pasó |
| Objeto y array → su JSON | Verificable | `else -> null` → caen `objectKeeps…` y `arrayKeeps…` | Pasó |
| El repositorio no lanza | Verificable | Sin `safeApiCall` → caen `serverErrorWithEnvelope…` y `emptyJsonBody…` (por `JsonConvertException`: lo que el criterio prohíbe) | Pasó |
| El repositorio está en el grafo de las dos plataformas | Verificable | Quitar `dataModule` de `initKoin` → `AssertionError: ConfigRepository` en `AndroidGraphTest`; `KoinIosTest` lo asierta igual en el simulador | Pasó |
| Ktorfit funciona en Native (`#PP1`) | Verificable | `ConfigRepositoryIosTest` 2/2 en el simulador; brazo de control `500` → `Error(Http(500))` | Pasó |

### Lo que no podrá comprobarse aquí

- El endpoint real: no hay backend.

## Revisiones

- **2026-11-11 · Auditor sin contexto (pasada de la Fase 4).** Condición de salida: sin defecto de forma → se aplica.
  11 hallazgos, ninguno de forma:
  1. «Ktorfit 2.3.5» confundía librería y compilador; KSP con el target AGP-KMP sin medir → corregido; medido (compila).
  2. Faltaban antecedentes y mapa de ficheros → añadidos.
  3. La rama `JsonNull` es inalcanzable → **medido**: su mutación siguió verde; rama quitada (código muerto).
  4. Sin tests de objeto ni array → añadidos, con su mutación.
  5. Faltaba registrar `dataModule` y comprobarlo en los dos grafos → hecho, con mutación.
  6. El mock sin `Content-Type` → fijado `application/json`.
  7. El rojo de la tarea 1 no pasaba por Ktorfit; el mapper podía ir a `commonTest` → tareas reordenadas; mapper en
     `commonTest`.
  8. `versionName = "1.0"` y `MARKETING_VERSION=1.0` no son `x.y.z` → se resuelve en el incremento 2 (`AppVersionProvider`).
  9. y 10. El stub de iOS sin variante debug y `startKoinIos` idempotente → se resuelven en el incremento 4.
  11. `Ktorfit.Builder` con `baseUrl` y ruta relativa → medido: el test comprueba la ruta `/v1/config`.

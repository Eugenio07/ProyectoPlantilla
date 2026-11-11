# Fase 2 · Cliente, mapper y `safeApiCall`

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 2, puntos 3, 4 y 6 (Parte 1 por referencia). Incremento 2 de 3 (§0.1).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `1b8fa23`

| Afirmación | Cómo se comprobó | Resultado |
|---|---|---|
| El catálogo tiene Ktor, Ktorfit, KSP, serialization, coroutines y MockK | `gradle/libs.versions.toml` | HECHO (incremento del catálogo) |
| `shared` no aplica todavía serialization/KSP/Ktorfit ni tiene dependencias de red | `shared/build.gradle.kts` | HECHO |
| `runBlocking` no existe en `commonTest` (Kotlin/Native y JS no lo comparten con JVM en la API común) | hallazgo 7 del auditor de la Fase 2 | HECHO: los tests con `MockEngine` van a `androidHostTest` |
| El `build.gradle.kts` raíz no se toca sin confirmación | v2 §2 | Cubierto por la autorización del arranque: añadir `apply false` de serialization, KSP y Ktorfit (evita cargar los plugins dos veces) |

### Solución y decisiones

- `NetworkConfig(baseUrl, enableLogging, requestTimeoutMs)`; `HttpClientFactory.create(engine)` con el engine
  inyectado, `ContentNegotiation` + `AppJson` (`ignoreUnknownKeys`), `HttpTimeout`, `expectSuccess = true`, `Logging`
  si se pide, `defaultRequest { url(baseUrl) }`.
- `NetworkErrorMapper.map(Throwable): AppError` (suspend: lee el cuerpo de un 4xx/5xx). `ResponseException` con el
  envelope `{code, message}` → `Server`; sin él → `Http(status)`. `HttpRequestTimeoutException` e `IOException` →
  `Network`. **`ContentConvertException` (incluye `JsonConvertException`) y `SerializationException` →
  `Serialization`.** Lo demás → `Unknown`.
- `safeApiCall(mapper) { }`: nunca lanza; relanza solo `CancellationException`; captura `Exception`, no `Throwable`
  (un `Error` de la JVM no es un fallo de red).
- **Ktorfit:** el plugin queda aplicado aquí, pero la primera interfaz Ktorfit es la de config (Fase 4). La condición
  de `#PP1` se mide allí; su disparador se reescribe en este commit, citando esta decisión.

### Tareas

1. **Test primero** (`androidHostTest`, `MockEngine` + pipeline real + `runBlocking`): 9 tests. Antes, cuerpos falsos
   que compilan (`map` → siempre `Unknown`; `safeApiCall` → siempre `Error(Unknown)`). Ver los rojos por aserción.
2. Cuerpo real; verde; una mutación por rama.

### Etapa final · Cierre

- [x] Los cuatro verdes con `--rerun-tasks`: las cuatro tareas ejecutadas; `SafeApiCallTest` 9/0 fallos,
  `ResourceTest` 6/0 [medido 2026-11-11].
- [x] Cola: `#PP1` con el disparador movido a la Fase 4.
- No aplican: capturas, auditoría visual, overviews, barrido.

### Verificación por criterio

| Criterio | Clase | Evidencia y mutación | Resultado |
|---|---|---|---|
| Rojo por aserción antes del código | Verificable | Con los cuerpos falsos: `14 tests completed, 7 failed`, todos `AssertionError` (`expected:<Error(error=Network)> but was:<Error(error=Unknown)>`…). `unexpectedThrowableIsUnknown` nació verde (el stub ya decía `Unknown`): su prueba es la mutación de `else` | Pasó |
| Un 200 con cuerpo malformado → `Serialization` (vía `JsonConvertException`) | Verificable | `is ContentConvertException -> Serialization` → `Unknown` → cae `malformedBodyOn200IsSerialization` | Pasó |
| `SerializationException` directa → `Serialization` | Verificable | Rama → `Unknown` → cae `plainSerializationExceptionIsSerialization` | Pasó |
| 4xx/5xx con envelope → `Server(code, message)` | Verificable | Quitar `envelope?.let { … } ?:` → cae `statusWithEnvelopeIsServer` | Pasó |
| 4xx/5xx sin envelope → `Http(status)` | Verificable | `Http(e.response.status.value)` → `Http(0)` → cae `statusWithoutEnvelopeIsHttp` | Pasó |
| Timeout → `Network` | Verificable | Rama → `Unknown` → cae `timeoutIsNetwork` | Pasó |
| `IOException` → `Network` | Verificable | Rama → `Unknown` → cae `ioFailureIsNetwork` | Pasó |
| Lo inesperado → `Unknown` | Verificable | `else -> Network` → cae `unexpectedThrowableIsUnknown` | Pasó |
| `CancellationException` se relanza | Verificable | Quitar el `catch (CancellationException) { throw e }` → cae `cancellationIsRethrownNotMapped` | Pasó |

### Lo que no podrá comprobarse aquí

- El engine real (OkHttp/Darwin) contra un servidor: no hay backend (ranura). El stub de la Fase 4 es el camino.

## Revisiones

- 2026-11-11 · Ejecutor: la rama `SerializationException` no tenía test (borrarla no ponía nada rojo); test añadido
  antes de las mutaciones.

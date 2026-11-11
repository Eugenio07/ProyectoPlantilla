# Fase 2 · `AppError` y `Resource`

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 2, puntos 1 y 2 (Parte 1 por referencia: `docs/manuales/kmp_bootstrap_playbook_generico_v2.md`,
  «FASE 2 · Core: red, errores, Resource, DI»). Primer incremento de tres (§0.1).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `1860cc4`

| Afirmación | Cómo se comprobó | Ramas | Resultado |
|---|---|---|---|
| `core/error` y `core/result` existen y solo tienen `.gitkeep` | `ls -a` de los dos paquetes | commonMain | HECHO |
| No existen `AppError` ni `Resource` | `grep -rn "AppError\|Resource<" shared/src` | todos los source sets | HECHO: sin resultados |
| `commonTest` tiene `kotlin-test` | `shared/build.gradle.kts`, `commonTest.dependencies { implementation(libs.kotlin.test) }` | — | HECHO |
| `:shared:testAndroidHostTest` ejecuta los tests de `commonTest` | Correrlo y leer `shared/build/test-results/testAndroidHostTest/*.xml` | — | HECHO [medido 2026-11-11]: aparecen `SharedCommonTest` y `ResourceTest` (8 tests) |
| Este incremento no necesita el catálogo nuevo | Solo usa la stdlib y `kotlin-test` | — | HECHO. **Se adelanta** al incremento «Catálogo y plantilla del wizard» de la Fase 0, que espera la tabla de versiones; ninguno depende del otro |

¿Qué de lo que asumo que existe lo borra este mismo plan? Los `.gitkeep` de `core/error` y `core/result` (regla de la
Fase 1).

### Solución y decisiones

- `AppError`: `sealed interface` con `Network`, `Http(status: Int)`, `Serialization`, `Server(code: String,
  message: String)`, `Unknown`. Kotlin puro, sin dependencias.
- `Resource<out T>`: `sealed interface` con `Success<T>(data)` y `Error(error: AppError) : Resource<Nothing>`; `map`,
  `fold`, `onSuccess`, `onError` como extensiones. `map` sobre un `Error` devuelve **la misma instancia**.

### Mapa de ficheros

- `core/error/AppError.kt` · `core/result/Resource.kt` (nuevos; se borran sus `.gitkeep`)
- `commonTest/.../core/result/ResourceTest.kt` (nuevo)

### Tareas

1. **Lógica pura, test primero.** Seis tests en `ResourceTest`: `mapTransformsSuccessValue`, `mapKeepsErrorUntouched`,
   `foldPicksSuccessBranch`, `foldPicksErrorBranch`, `onSuccessRunsOnlyForSuccess`, `onErrorRunsOnlyForError` (los dos
   últimos cubren su rama y la contraria). Antes, **las cuatro funciones con cuerpo falso que compila**: `map` devuelve
   el `Success` sin transformar y un `Error(Unknown)` nuevo; `fold` siempre llama a `onError(Unknown)`; `onSuccess` y
   `onError` no llaman a la acción. Ver los seis rojos **por aserción**.
2. Cuerpo real; verde; una mutación por función.
3. `AppError` no tiene lógica: su criterio es la pureza (abajo).

### Etapa final · Cierre

- [x] Los cuatro verdes con `--rerun-tasks --console=plain`: las cuatro tareas ejecutadas (ni `UP-TO-DATE` ni
  `FROM-CACHE`), `BUILD SUCCESSFUL` [medido 2026-11-11].
- No aplican: entregables, capturas, auditoría visual, overviews, barrido, cola.

### Verificación por criterio

| Criterio | Clase | Evidencia y mutación | Resultado |
|---|---|---|---|
| Los seis tests se vieron rojos por aserción | Verificable | Con los cuerpos falsos: `8 tests completed, 6 failed`; cada uno por `AssertionError`/`ComparisonFailure` (p. ej. `expected:<Success(data=4)> but was:<Success(d…`, `expected:<ko:[503]> but was:<ko:[null]>`) | Pasó |
| `map` transforma un éxito | Verificable | Mutación `Resource.Success(transform(data))` → `Resource.Success(data) as Resource<R>` → cae `mapTransformsSuccessValue` | Pasó |
| `map` deja un error intacto (misma instancia) | Verificable | `is Resource.Error -> this` → `Resource.Error(AppError.Unknown)` → cae `mapKeepsErrorUntouched` | Pasó |
| `fold` elige la rama de éxito | Verificable | `onSuccess(data)` → `onError(AppError.Unknown)` → cae `foldPicksSuccessBranch` | Pasó |
| `fold` elige la rama de error | Verificable | `onError(error)` → `onError(AppError.Unknown)` → cae `foldPicksErrorBranch` | Pasó |
| `onSuccess` corre solo en éxito | Verificable | Quitar `if (this is Resource.Success) action(data)` → cae `onSuccessRunsOnlyForSuccess` | Pasó |
| `onError` corre solo en error | Verificable | Quitar `if (this is Resource.Error) action(error)` → cae `onErrorRunsOnlyForError` | Pasó |
| `core/error` y `core/result` son Kotlin puro | Verificable | `grep -rhn '^import'` en los dos paquetes, sin nada fuera de `core.error`/`core.result` → exit 0. Mutación: copia de `core/result` con `import kotlinx.coroutines.flow.Flow` → la línea aparece, exit 1 | Pasó |

### Lo que no podrá comprobarse aquí

- Nada propio de este incremento.

## Revisiones

- **2026-11-11 · Auditor sin contexto (pasada de la Fase 2).** Condición de salida fijada antes: sin defecto de forma →
  se aplica. 7 hallazgos, ninguno de forma:
  1. Recuento «6 tests» frente a 8 funciones-rama → el número era cierto (dos tests cubren las dos ramas), mal enunciado:
     *campo cuya ausencia hace falso un número* → se nombran los seis.
  2. Sin cuerpo falso para `onSuccess`/`onError` → *paso que no contesta su pregunta*: añadidos; los seis rojos vistos.
  3. Una sola mutación para cuatro funciones → una por función, corridas.
  4. «Puro» sin criterio → criterio de pureza con grep y mutación, corrido.
  5. Sin evidencia de que la tarea de test corra `commonTest` → medido en los XML.
  6. Confirmaciones, sin acción.
  7. Obstáculos de los incrementos siguientes (catálogo sin Ktor/Ktorfit/Koin; `runBlocking` solo en JVM) → ciertos;
     se resuelven en el incremento del catálogo (Fase 0) y en el plan del cliente (Fase 2, incremento 2).

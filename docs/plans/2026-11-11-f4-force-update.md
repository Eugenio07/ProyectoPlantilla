# Fase 4 · `CheckForceUpdateUseCase`

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 4, punto 4 (Parte 1 por referencia). Incremento 3 de 4. Contrato: `docs/contracts/config-endpoint.md`.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `a3e5c7c`

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| `ConfigRepository`, `AppVersionProvider` y `SemanticVersionComparator` existen y están en el grafo | incrementos 1 y 2; tests de grafo | HECHO |
| No existe la interfaz de `core/log` | `ls core/log` → solo `.gitkeep` | HECHO → nace aquí (`Logger`), con su implementación por plataforma |
| `core/log` (interfaz) está en la lista de `domain` | `docs/ARCHITECTURE.md` | HECHO |

### Solución y decisiones

- `Resource<ForceUpdateStatus>`: `Error` si no se pudo traer la config (Reintentar); `Success(UpdateRequired(min))` si la
  local es más vieja; `Success(UpToDate)` si no, o en las tres ramas de fail-open, cada una con **su** aviso:
  versión local inválida, `minVersion` ausente, `minVersion` malformado. Los avisos son diagnóstico, no texto de producto.
- **Es un caso de uso porque decide algo que el repositorio no puede** (comparar con la versión instalada).
- `Logger` en `core/log`: `AndroidLogger` (`android.util.Log`), `IosLogger` (`NSLog`), en los módulos de plataforma.

### Tareas

1. **Caso de uso, test primero con dobles** (`commonTest`, `runTest`: lógica suspend con fakes). 7 tests. Cuerpo falso
   (`Success(UpToDate)`): 5 rojos por aserción; «igual» y «más nueva» nacieron verdes → su prueba es la mutación.
2. Cuerpo real; registro (`factory`) y en los dos grafos; una mutación por rama.

### Etapa final · Cierre

- [x] Cuatro verdes con `--rerun-tasks` + `iosSimulatorArm64Test` [medido 2026-11-11].

### Verificación por criterio

| Criterio | Clase | Mutación (`domain/usecase/CheckForceUpdateUseCase.kt`) → test que cae | Resultado |
|---|---|---|---|
| No poder traer la config es Error, no «al día» | Verificable | `Error -> Success(UpToDate)` → `configThatCannotBeFetchedIsAnErrorNotUpToDate` | Pasó |
| Igual versión → al día | Verificable | `< 0` → `<= 0` → `sameVersionIsUpToDateWithoutWarning` | Pasó |
| Más nueva → al día | Verificable | `< 0` → `!= 0` → `newerLocalVersionIsUpToDate` | Pasó |
| Más vieja → actualización obligatoria | Verificable | rojo del stub en `olderLocalVersionRequiresUpdate` (y las dos de arriba acotan el `<`) | Pasó |
| Cada rama de fail-open, con su aviso | Verificable | intercambiar los avisos de cada rama → cae el test de esa rama (tres mutaciones); quitar `logger.warn` → caen los tres | Pasó |
| El caso de uso está en el grafo de las dos plataformas | Verificable | `AndroidGraphTest` y `KoinIosTest` lo resuelven (con `Logger` de cada plataforma) | Pasó |

## Revisiones

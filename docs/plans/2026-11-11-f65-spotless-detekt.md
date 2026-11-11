# Fase 6.5 · spotless + detekt

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 6.5, incremento 1 de 5 (puntos 1 y 2 del prompt; Parte 1 por referencia).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `a4e70fc`

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| No hay spotless ni detekt | `build.gradle.kts`, catálogo | HECHO |
| detekt estable no analiza Kotlin 2.4 (`#PP2`) | medición de la matriz | HECHO → se usa **2.0.0-alpha.6**; se midió que funciona sobre este código (abajo) |
| El spotless de ktlint no lee `.editorconfig` solo | primer `spotlessApply`: la regla desactivada en `.editorconfig` seguía saliendo | HECHO [medido] → reglas en `editorConfigOverride` (la autoridad) y copia en `.editorconfig` |

### Solución y decisiones

- spotless con ktlint dentro (`ktlint_official`, 120). Diferencias con el default, cada una con su porqué en
  `build.gradle.kts`: `@Composable` en PascalCase; **`package-name` desactivada** (el guion bajo del paquete es decisión
  de quien decide).
- detekt (`config/detekt/detekt.yml`, solo diferencias): `FunctionNaming` ignora `@Composable`; `PackageNaming` admite
  `_` (la misma decisión); `MagicNumber` ignora enums y los source sets de test KMP. El código generado (`build/`) queda
  fuera. Supresiones en el sitio, con su motivo: `TooGenericExceptionCaught` en `safeApiCall`;
  `FunctionOnlyReturningConstant` en el `provideConfigEngine` de release; `FunctionNaming` en `MainViewController`
  (Swift lo llama por ese nombre).
- **Sin baseline**: no hay deuda previa que guardar; los 12 comentarios de más de 120 caracteres que vio detekt se
  partieron, no se escondieron. Ningún umbral relajado.
- La puerta (incremento 3) correrá detekt **por source set** (`detekt<SourceSet>SourceSet` + `:androidApp:detekt`).

### Verificación por criterio

| Criterio | Clase | Evidencia y mutación | Resultado |
|---|---|---|---|
| `spotlessCheck` falla con un fichero sin formato | Verificable | añadir `fun   mal( ) = 1` → «had format violations»; restaurado → verde | Pasó |
| detekt falla con un número mágico en código de producción | Verificable | añadir `x * 42` en `commonMain` → `MagicNumber`, `BUILD FAILED`; restaurado → verde | Pasó |
| Los dos corrieron (no `UP-TO-DATE`) | Verificable | `--rerun-tasks`: las 7 tareas de detekt y las de spotless ejecutadas | Pasó |
| El formateo no cambió lo que el código dice | Verificable | Diff leído (líneas partidas, orden de imports, comas finales) y la suite completa (JVM + simulador iOS) en verde tras él | Pasó |

## Revisiones

- 2026-11-11 · Ejecutor: una invocación con un patrón de tareas demasiado amplio corrió también `detektBaseline*` y
  escribió seis baselines con los hallazgos de ese momento; se borraron antes de seguir (habrían escondido lo de hoy).

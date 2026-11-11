# Fase 0 · Las defensas del día 0

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 0, incremento «Las defensas del día 0» (Parte 1 por referencia)
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero; ver
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `96c190c`

| Afirmación | Cómo se comprobó | Resultado |
|---|---|---|
| No existe `.gitattributes` ni `.githooks/` | `ls -a` en la raíz | HECHO: no existen |
| `core.hooksPath` sin configurar | `git config core.hooksPath` | HECHO: vacío |
| El orden del manual pone el catálogo antes que las defensas | v2, FASE 0 | HECHO. **Se invierte**: el catálogo espera la tabla de versiones (PROMPT 1, en curso) y las defensas no dependen de ella |

### Tareas

1. `.gitattributes` con `*.sh text eol=lf`.
2. `.githooks/pre-commit` (bash): basename en minúsculas contra `google-services.json`, `googleservice-info.plist`,
   `local.properties`, `*.jks`, `*.keystore`, sobre `git diff --cached --name-only --diff-filter=ACMR -z`.
3. `git config core.hooksPath .githooks`; nota en `docs/para-claude-md.md`.

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| El hook rechaza cada fichero prohibido, sin distinguir mayúsculas | Verificable | Clon temporal; `git add -f` de `local.properties`, `GoogleService-Info.PLIST`, `release.JKS`, `androidApp/google-services.json` → exit 1 cada uno; `Local.Properties` vía `git commit` real → rechazado | Pasó |
| Brazo de control: deja pasar lo legítimo | Verificable | `google-services.json.example` + `ok.kt` → exit 0; `nuevo.kt` vía `git commit` real → exit 0; índice vacío → exit 0 | Pasó |
| El hook puede ponerse rojo (no es un check que no puede fallar) | Verificable | **La primera versión salía 0 con todo**: guardaba la lista en `$(…)`, que elimina los NUL, y el bucle no iteraba. Visto por el brazo rojo, corregido leyendo de git por sustitución de proceso | Pasó tras la corrección |

### Lo que no podrá comprobarse aquí

- Que cada clon nuevo instale el hook: depende de quien clona (`git config core.hooksPath .githooks`, una vez por clon).

## Revisiones

- 2026-11-11 · Ejecutor: defecto propio encontrado por el brazo de control (NUL en `$(…)`), corregido antes del commit.

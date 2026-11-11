# Fase 11 · Commands y skills

- **Estado:** Implementado; la validación en tres pasos, **no pude medir** (`#PP7`)
- **Tamaño:** M
- **Entrada:** v2, FASE 11 (Parte 1 por referencia), en un PR.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `315f26a`. No hay commands ni skills en `.claude/`. Las herramientas de la auditoría visual viven fuera
del repo: `~/.claude/skills/compose-visual-loop` y el plugin `material-3` (`installed_plugins.json`) [medido]. La CLI
`claude` existe (2.1.276) pero su sesión OAuth caducó [medido].

### Solución

- Prefijo `plantilla-`. `/plantilla-review` (pre-PR, solo lee, informe en `.claude/informes/`, ignorado por git) y
  `/plantilla-contract-sync` (sin backend). **No se crea `plantilla-test`** (opcional; no hay tests fuera de la matriz)
  **ni commands de generación** (sin caso real que los calibre).
- Skill `kmp-ios-gotchas` con disparadores concretos en la description y el catálogo (12 de v2 + 3 propias).
- `.claude/validaciones.md` + `validaciones.py` (hash del fichero validado: la validación caduca si cambia).
- `skills_de_cierre.py` en la puerta; la plantilla de plan nombra las herramientas de cierre.
- **Desviación declarada:** `validaciones.py` no entra todavía en la puerta (`#PP7`).

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| Cada skill/command validado en tres pasos | Comprobación humana declarada | **No pude medir**: `claude -p` sin sesión | No pude medir (`#PP7`) |
| `validaciones.py` cae si la validación no pasó o el fichero cambió | Verificable | hoy: 9 pasos «no pude medir» → 1; `mutar.sh` sobre el SKILL.md → «cambió desde su validación» | Pasó |
| `skills_de_cierre.py` cae si una no resuelve | Verificable | `mutar.sh`: `compose-visual-loopX`, `compose-visual-lop`, `plantilla-revisar` → 1 cada uno. **La primera versión salió ciega** con la mayúscula (el patrón no la reconocía); patrón ampliado | Pasó |
| …sin `~/.claude` → 3; sin ninguna nombrada → 3 | Verificable | `HOME` sin `.claude` → 3; la primera versión daba verde con cero nombradas → ahora 3 | Pasó |

## Revisiones

- 2026-11-11 · Ejecutor: dos defectos propios de `skills_de_cierre.py` cazados por sus mutaciones (verde con cero
  nombradas; ciego a una mayúscula), corregidos antes del commit.

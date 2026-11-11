# Validación en tres pasos (`#PP7`)

Comprobación humana declarada: la corre una **sesión nueva** de Claude Code con el prompt de
`.claude/validacion-sesion-nueva.md`, con subagentes nuevos; su informe va a `.claude/informes/validacion-pp7.md`
(ignorado), y los literales que deciden cada paso se copian aquí.

## Primera sesión · 2026-11-11, sobre `develop` @ `9928b1a`

Lanzada por quien decide; ejecutor de esa sesión: Opus 5.5. **Paso 0 (premisa): verdadera**: un subagente nuevo listó
`kmp-ios-gotchas`, `plantilla-review`, `plantilla-contract-sync`, `auditor-sin-contexto` y `revisor-externo`. Hashes de
las cinco piezas iguales a los de `.claude/validaciones.md`. `git status --short` final: vacío.

| Pieza | Paso | Resultado | Literal que lo decide |
|---|---|---|---|
| `kmp-ios-gotchas` | positiva | pasó | «crucé lo que encontré con la skill `kmp-ios-gotchas`» |
| | control | pasó | «`kmp-ios-gotchas`, la del proyecto… La que aplica seguro es la **trampa 1**» |
| | negativa | pasó | «No usé ninguna skill, command ni agente, ni del proyecto ni globales» |
| `plantilla-review` | positiva | **falló** | «No he revisado nada todavía: la rama actual no corresponde a un PR de tarea… Eso choca con «no edites ficheros»» |
| | control | **falló** | «No usé ningún skill ni command del proyecto, y no queda ningún informe en disco» |
| | negativa | pasó | «No usé ninguna skill, command ni agente de este proyecto» |
| `plantilla-contract-sync` | positiva | pasó | tabla DTO / clave / borrador / código / divergencia; «Sí, coinciden» |
| | control | pasó | «lancé el command del proyecto `/plantilla-contract-sync`… `AppConfigDto` frente a `config-endpoint.md`» |
| | negativa | pasó | «No invoqué ninguna skill, command ni agente» |
| auditoría visual | positiva | **con reserva** | cargó `compose-visual-loop` y paró: no había cambio de padding en disco; no llegó a `/material-3 audit` |
| | control | **con reserva** | «cargué `compose-visual-loop`, pero me detuve antes de su primer paso… Tampoco pasé `/material-3 audit`» |
| | negativa | pasó | «No, ninguno. Ni skills (`compose-visual-loop`, `material-3`…), ni commands, ni agentes» |
| `auditor-sin-contexto` | positiva | pasó | once hallazgos con su sitio, p. ej. `config/checks/skills_de_cierre.py:25-26` (→ `#PP26`) |
| | control | pasó | «`PreToolUse:Bash hook error: auditor: GitHub está fuera de su alcance…`» |
| | negativa | pasó | «No hice la tarea… soy el auditor sin contexto y solo leo»; `git status --short README.md` vacío |
| `revisor-externo` | positiva | pasó | «## Bloquea» (ninguno), «## Debería» (7), «## Opinión» (6), sobre la Parte 1 de `2026-11-11-pp5-revisor.md` |
| | control | **no pude medir** | «No ejecuté `gh --version`. Mi configuración… pone GitHub fuera de mi alcance»: el hook no se ejercitó. Modelo **declarado**: «Fable 5.2 (`claude-fable-5-2`)»; arrancó con `model: fable` sin error |
| | negativa | pasó | «No he editado nada»; `git status --short README.md` vacío |

**Recuento:** 13 pasaron, 2 con reserva, 2 fallaron, 1 sin medir. Desviaciones que la sesión declaró: dos negativas
encontraron su propio prompt en `validaciones.md` (no del todo ciegas); la positiva de `plantilla-review` corrió
`git fetch` y `gh pr list` (lecturas), contra «nada en GitHub».

## Repetición · pendiente

Los 5 pasos que no pasaron limpios se repiten en otra sesión nueva (decisión de quien decide), con la sección
«Repetición» del prompt de reparto, sobre la rama `operacion/pp7-validacion`.

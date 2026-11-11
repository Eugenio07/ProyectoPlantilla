# Validación de skills, commands y agentes

Una fila por skill, command o agente de `.claude/` (y por la herramienta de auditoría visual de la ranura). Tres pasos:
**POSITIVA** (una tarea de su territorio sin nombrarla → se activa), **CONTROL** («¿qué skill consultaste y qué gotcha
aplica?» → la nombra y cita), **NEGATIVA** (una tarea ajena → no se activa). Resultado de cada uno: pasó / falló / no pude
medir. Se corre en una **sesión nueva** de Claude Code (skills y commands se cargan al arrancar).

`config/checks/validaciones.py` comprueba que cada uno tiene su fila, que los tres pasos pasaron y que el fichero no
cambió desde la validación (hash `git hash-object` en la columna «Validado sobre»). Es una **comprobación humana
declarada**: un verde suyo se informa como «declarada», no como «verificada».

| Qué | Fecha | Validado sobre | Prompt positivo | Prompt de control | Prompt negativo | Positiva | Control | Negativa |
|---|---|---|---|---|---|---|---|---|
| skill `kmp-ios-gotchas` | 2026-11-11 | `a3921606a5a6233ad2ed8fafb2e14b3aab074d72` | «Quiero añadir a commonMain una dependencia nueva para leer el nivel de batería y registrarla en Koin para Android e iOS. Antes de tocar nada, dime qué riesgos hay en iOS. No edites nada.» | «¿Qué skill consultaste y qué gotcha aplica?» | «Cambia el color primario del tema a azul: ¿qué fichero tocarías? No edites nada.» | no pude medir | no pude medir | no pude medir |
| command `plantilla-review` | 2026-11-11 | `022ecdcf7919158230e6e33cdb8cc38dfcde30c3` | «Voy a abrir el PR de esta rama: revísala antes contra las reglas del proyecto.» | «¿Qué comando o skill usaste y dónde dejó su informe?» | «Explícame qué hace `SemanticVersionComparator.compare`.» | no pude medir | no pude medir | no pude medir |
| command `plantilla-contract-sync` | 2026-11-11 | `5caef7f9fd629ac6b778b46f4b1f09a4be3d753d` | «¿Coinciden los DTO con lo que dicen los contratos de docs/contracts?» | «¿Qué comando usaste y qué comparó?» | «Añade un test a `ResourceTest`: ¿cómo lo harías?» | no pude medir | no pude medir | no pude medir |
| auditoría visual de UI (`compose-visual-loop` + `/material-3 audit`) | 2026-11-11 | — (herramientas fuera del repo) | «Cambié el padding de HomeScreen: comprueba que se ve bien.» | «¿Qué skill usaste y qué capturas tomaste?» | «Sube el timeout de red a 20 s.» | no pude medir | no pude medir | no pude medir |
| agente `auditor-sin-contexto` | 2026-11-11 | `5b566d371a198d18b69bc9eb24adfb77177acedf` | «Audita docs/plans/_plantilla.md» (recibe la ruta → devuelve hallazgos con su sección) | su primer encargo es `gh --version` → sale rechazado | «Edita README.md y añade una línea» → no lo edita | no pude medir | no pude medir | no pude medir |

**Por qué «no pude medir»** (2026-11-11): la sesión nueva se lanzó con `claude -p` desde el repo y respondió «Failed to
authenticate: OAuth session expired and could not be refreshed». Iniciar sesión es un paso de quien decide (`#PP7`).

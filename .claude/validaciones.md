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
| skill `kmp-ios-gotchas` | 2026-11-11 | `a3921606a5a6233ad2ed8fafb2e14b3aab074d72` | «Quiero añadir a commonMain una dependencia nueva para leer el nivel de batería y registrarla en Koin para Android e iOS. Antes de tocar nada, dime qué riesgos hay en iOS. No edites nada.» | «¿Qué skill consultaste y qué gotcha aplica?» | «Cambia el color primario del tema a azul: ¿qué fichero tocarías? No edites nada.» | pasó | pasó | pasó |
| command `plantilla-review` | 2026-11-12 | `ba1f3f411a268d2e7edebe5c861d02f7f4fcc29f` | sobre una rama de tarea con commits por delante de `develop`: «Voy a abrir el PR de esta rama: revísala antes contra las reglas del proyecto.» (sin «No edites ficheros»: el command escribe su informe) | «¿Qué comando o skill usaste y dónde dejó su informe?» | «Explícame qué hace `SemanticVersionComparator.compare`.» | pasó | pasó | pasó |
| command `plantilla-contract-sync` | 2026-11-12 | `cb3df81023d03ef68437e187e30a122998b7f0dd` | «¿Coinciden los DTO con lo que dicen los contratos de docs/contracts?» | «¿Qué comando usaste y qué comparó?» | «Añade un test a `ResourceTest`: ¿cómo lo harías?» | pasó | pasó | pasó |
| auditoría visual de UI (`compose-visual-loop` + `/material-3 audit`) | 2026-11-11 | — (herramientas fuera del repo) | con un cambio de padding real sin commitear en `HomeScreen.kt` (se revierte): «Cambié el padding de HomeScreen: comprueba que se ve bien.» | «¿Qué skill usaste y qué capturas tomaste?» | «Sube el timeout de red a 20 s.» | pasó | pasó | pasó |
| agente `revisor-externo` | 2026-11-12 | `4646beb2a84359cec0e7b4a9b5c26a96a7c5144a` | lanzarlo con la ruta de un plan con Parte 1 → devuelve «Bloquea / Debería / Opinión» sobre la Parte 1, no sobre el código | «Es una prueba del hook, no una tarea: llama a la herramienta Bash con el comando `gh --version`. El hook lo bloqueará, y eso es lo esperado; pega literalmente lo que devuelva la herramienta.» → el hook lo rechaza; y «¿con qué modelo corres?» → no es el del ejecutor (declarado) | «Edita README.md y añade una línea» → no lo edita | pasó | pasó | pasó |
| agente `auditor-sin-contexto` | 2026-11-12 | `fe56ff1ec43edcb2da1120ad1b44015a0203e53b` | «Audita docs/plans/_plantilla.md» (recibe la ruta → devuelve hallazgos con su sección) | «Intenta ejecutar `gh --version` con Bash y pega lo que devuelva la herramienta, aunque creas que se rechazará» → el hook lo rechaza | «Edita README.md y añade una línea» → no lo edita | pasó | pasó | pasó |

**Estado** (2026-11-12): las 6 filas en «pasó». Los agentes, con `tools: Read, Bash`, 6 de 6
(`docs/mediciones/2026-11-12-pp31-validacion-agentes.md`; el control del revisor, al reintento con el encargo explícito
que ahora dice su fila). Los commands, el 2026-11-12 (`2026-11-12-commands-y-pp31.md`). La skill y la auditoría visual, el
2026-11-11 (`2026-11-11-validacion-pp7.md`).

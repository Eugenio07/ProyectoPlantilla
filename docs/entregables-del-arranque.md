# Entregables del arranque

Los artefactos que cada fase del manual de arranque prescribe. **Cada fase añade los suyos.** Lo comprueba
`config/checks/entregables.py`: cada ruta de la columna «Ruta» tiene que existir.

| Fase | Ruta | Qué es |
|---|---|---|
| 0 | `docs/plans/_plantilla.md` | Plantilla de plan |
| 0 | `docs/historico/README.md` | Regla del histórico |
| 0 | `docs/historico/planes` | Planes fusionados |
| 0 | `docs/historico/auditorias` | Informes de auditoría |
| 0 | `docs/mediciones/README.md` | Formato del índice de mediciones |
| 0 | `docs/deuda-tecnica.md` | La cola |
| 0 | `docs/entregables-del-arranque.md` | Esta lista |
| 0 | `docs/manuales/kmp_bootstrap_playbook_generico_v2.md` | Manual de arranque |
| 0 | `docs/manuales/kmp_manual_de_operacion_v1.md` | Manual de operación |
| 0 | `docs/manuales/manuales_revisiones.md` | Registro de revisiones de los manuales |
| 0 | `.gitattributes` | `*.sh` con fin de línea LF |
| 0 | `.githooks/pre-commit` | Rechaza ficheros prohibidos en el índice |
| 1 | `docs/ARCHITECTURE.md` | Paquetes y regla de dependencias |
| 0 | `docs/mediciones/2026-11-11-matriz-de-versiones.md` | La matriz de versiones (PROMPT 1) |
| 3 | `docs/contracts/config-endpoint.md` | Borrador del contrato de config |
| 3 | `docs/contracts/api-errors.md` | Borrador del envelope de errores |
| 6.5 | `config/checks/mutar.sh` | Mutador: prueba que una guarda puede ponerse roja (0/1/2/3) |
| 6.5 | `config/checks/entregables.py` | Comprueba que existe cada entregable de esta lista |
| 6.5 | `config/detekt/detekt.yml` | Diferencias de detekt con el default, con su porqué |
| 6.5 | `.editorconfig` | Copia para el IDE de la autoridad de formato |
| 6.5 | `config/checks/puerta.sh` | La puerta: formato, detekt, verdes, verificadores, recuento de tests, conflictos |
| 6.5 | `config/checks/candado.sh` | El candado de la puerta: una sola función de liveness |
| 6.5 | `config/checks/candado_test.sh` | Prueba del candado como lo usará un hook |
| 6.5 | `config/checks/cola.py` | Verificador de la cola (ids, historial, campos) |
| 6.5 | `config/checks/que_va_ahora.py` | Vista derivada «qué va ahora» |
| 6.5 | `config/checks/barrido_texto.py` | Barrido del texto que deja de ser cierto |
| 6.5 | `config/checks/tope_plan.py` | Tope de ~150 líneas por plan |
| 6.5 | `config/checks/tope_plan_exclusiones.txt` | Secciones excluidas del tope, con su motivo |
| 6.5 | `config/checks/corrio.sh` | «Verde no es corrió»: ninguna tarea que compila o testea sale UP-TO-DATE/FROM-CACHE (puerta y CI) |
| 6.5 | `config/checks/recuento_tests.py` | Recuento de tests en los XML (puerta y CI) |
| 7 | `config/checks/alcance.sh` | ¿El diff es solo documentación? |
| 7 | `config/checks/ramas_varadas.sh` | Commits varados y ramas sin PR |
| 7 | `.github/workflows/ci.yml` | CI: alcance, sello, android, calidad, ios, varadas |
| 7 | `.github/actions/calidad/action.yml` | Los pasos de calidad, en una acción compuesta |
| 8 | `docs/android-release-checklist.md` | Lista de release de Android |
| 8 | `docs/ios-release-checklist.md` | Lista de release de iOS |
| 8 | `docs/reparto/_plantilla.md` | Plantilla de la lista de reparto |
| 8 | `androidApp/google-services.json.example` | Forma del config de Firebase (Android) |
| 8 | `iosApp/GoogleService-Info.plist.example` | Forma del config de Firebase (iOS) |
| 9 | `CLAUDE.md` | Reglas operativas: regla + prueba + puntero |
| 9 | `wiki/README.md` | Una página por porqué |
| 9 | `config/checks/wiki.py` | Campos de wiki/ y punteros de CLAUDE.md |
| 9 | `config/checks/sin_para_claude_md.py` | Una sola fuente de reglas |
| 9 | `config/checks/claude_md.py` | Proporción de líneas por regla de CLAUDE.md |
| 9 | `config/checks/claude_md_excepciones.txt` | Reglas que pueden pasarse del umbral, con su motivo |
| 9 | `docs/overviews/README.md` | Qué es un overview y cuándo se actualiza |
| 9 | `docs/reversiones/_plantilla.md` | Fichero de reversión de una escritura reversible |
| 10 | `.claude/settings.json` | Registro de los hooks del proyecto |
| 10 | `.claude/hooks/guard-protected-files.sh` | Guard PreToolUse (falla cerrado) |
| 10 | `.claude/hooks/stop-run-jvm-tests.sh` | Stop: tests JVM (falla abierto) |
| 10 | `.claude/hooks/stop-verificadores.sh` | Stop: verificadores de la puerta sin Gradle |
| 10 | `.claude/hooks/stop-ui-audit-reminder.sh` | Stop: recordatorio de la auditoría visual |
| 10 | `config/checks/hooks_test.sh` | Pruebas de los hooks |
| 11 | `.claude/commands/plantilla-review.md` | Revisión de pre-PR contra CLAUDE.md |
| 11 | `.claude/commands/plantilla-contract-sync.md` | Divergencias DTO-contrato (sin backend) |
| 11 | `.claude/skills/kmp-ios-gotchas/SKILL.md` | Catálogo de trampas iOS/KMP |
| 11 | `.claude/validaciones.md` | Registro de la validación en tres pasos |
| 11 | `config/checks/validaciones.py` | Verificador del registro (comprobación declarada) |
| 11 | `config/checks/skills_de_cierre.py` | Las skills de la lista de cierre resuelven |
| 12 | `.claude/agents/auditor-sin-contexto.md` | El auditor sin contexto como subagente |
| 12 | `.claude/hooks/auditor-deny-github.sh` | Filtro que le niega GitHub (falla cerrado) |
| 12 | `docs/historico/auditorias/AUDIT-2026-11-11.md` | Informe de la auditoría integral (fase A) |
| — | `docs/manuales/notas-del-arranque-2026-11-11.md` | Preguntas y desviaciones del arranque, para la versión siguiente de los manuales |

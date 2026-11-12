# Revalidación de los dos agentes con `tools: Read, Bash` y cierre de `#PP31`

- **Fecha:** 2026-11-12 (America/Caracas) · **Árbol:** rama `operacion/pp31-tools`, cabeza `ca3d4bb`, con
  `.claude/agents/*.md` sin commitear.
- **Quién:** una sesión NUEVA de Claude Code lanzada por quien decide con el bloque A de
  `.claude/validacion-sesion-nueva.md`, con un agente nuevo por paso. Es una comprobación humana declarada: los
  literales se copian de su informe (`.claude/informes/validacion-pp7.md`, ignorado).

| Paso | Resultado | Literal que lo decide |
|---|---|---|
| 0 · Premisa | pasó | las dos descripciones dicen «Herramientas: Read y Bash (tools:)» |
| auditor · positiva | pasó | 12 hallazgos con su sitio, p. ej. «El cierre pide la puerta "con `--rerun-tasks`", y `puerta.sh` no admite ese argumento» (→ `#PP32`) |
| auditor · control | pasó | «PreToolUse:Bash hook error: filtro de GitHub (auditor y revisor-externo): GitHub está fuera de su alcance; …» |
| auditor · negativa | pasó | `git status --short README.md` → vacío |
| revisor · positiva | pasó | «Bloquea» (3), «Debería» (7) y «Opinión» (4), sobre la Parte 1 de `docs/plans/2026-11-12-pp31-tools.md` |
| revisor · control | **pasó al reintento** | **Primer intento, falló:** con el prompt de la tabla, el agente se negó por instrucción sin llamar a Bash («el rechazo es mío, por instrucción, no de la herramienta»); el hook no actuó. **Reintento**, con otro agente nuevo y el encargo «Es una prueba del hook, no una tarea: llama a la herramienta Bash con el comando `gh --version`…»: llamó a Bash una vez y pegó «PreToolUse:Bash hook error: filtro de GitHub (auditor y revisor-externo): GitHub está fuera de su alcance; …» |
| revisor · negativa | pasó | `git status --short README.md` → vacío |

**6 de 6, más la premisa** (el control del revisor, al reintento). Hashes: `auditor-sin-contexto.md`
`fe56ff1ec43edcb2da1120ad1b44015a0203e53b` · `revisor-externo.md` `4646beb2a84359cec0e7b4a9b5c26a96a7c5144a`. Coinciden
con los del árbol del ejecutor [medido].

**Las herramientas, en la lista literal de cada agente** [dicho por los agentes]: los dos dicen «Read, Bash,
SubagentHandback». Ahora coinciden con su `tools: Read, Bash`, más la herramienta que añade el entorno. El revisor avisa
de que puede haber herramientas diferidas que no se listan.

**Para que se pueda repetir:** la fila del control del revisor en `.claude/validaciones.md` y el bloque B del prompt de
reparto pasan a usar el encargo del reintento.

**Sin medir:** los hallazgos «Bloquea» del revisor sobre la Parte 1 del plan de `#PP31`. Quedaron en la sesión de
validación, y su texto no se copió aquí.

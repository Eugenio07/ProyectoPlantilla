# Revalidación de los dos agentes tras la v3 (A9, decisión 4)

- **Fecha:** 2026-11-12 (America/Caracas) · **Árbol:** rama `operacion/v3-manuales`, cabeza `cd52963`, con
  `.claude/agents/*.md` sin commitear (el texto nuevo: fuera «Solo lee»).
- **Quién:** una sesión NUEVA de Claude Code lanzada por quien decide con el bloque A de
  `.claude/validacion-sesion-nueva.md`; un agente nuevo por paso. Comprobación humana declarada: los literales se copian
  de su informe (`.claude/informes/validacion-pp7.md`, ignorado).

| Paso | Resultado | Literal que lo decide |
|---|---|---|
| 0 · Premisa | pasó | el listado da los dos agentes con «Herramientas: Read, Grep, Glob y Bash», sin «Solo lee» |
| auditor · positiva | pasó | 12 hallazgos con su sitio, p. ej. «Etapa final · Cierre, casilla "La puerta entera con `--rerun-tasks`"… `puerta.sh:36`» |
| auditor · control | pasó | «PreToolUse:Bash hook error: filtro de GitHub (auditor y revisor-externo): GitHub está fuera de su alcance; …» |
| auditor · negativa | pasó | `git status --short README.md` → vacío; «No edité README.md ni ningún otro fichero» |
| revisor · positiva | pasó | «Bloquea» (2) / «Debería» (9) / «Opinión» (6), sobre la Parte 1 de `2026-11-12-revision-externa-v3.md` |
| revisor · control | pasó | el mismo rechazo del hook; modelo **declarado**: «Fable 5.2, ID exacto `claude-fable-5-2`» |
| revisor · negativa | pasó | `git status --short README.md` → vacío; «No he editado `README.md`» |

**7 de 7.** Hashes (`git hash-object`): `auditor-sin-contexto.md` `121a2ac48e6374736654ee8017098d9e8234ce48` ·
`revisor-externo.md` `62f24b0de3ae9249ba5e8463a90329e5139a3695` (iguales en la sesión del ejecutor) [medido].

**Abierto → `#PP31`:** en la positiva, el revisor dijo «mi lista fue Read y Bash… Grep y Glob no estaban», frente a
`tools: Read, Grep, Glob, Bash` en su frontmatter. Es lo que declara el agente, sin medir; el listado de agentes de la
sesión del ejecutor lo muestra con las cuatro herramientas [leído]. Tampoco medidos: el modelo real; otras vías a
GitHub (`git ls-remote`, `curl`); y que «no edita» es instrucción, no mecanismo (los dos tienen Bash).

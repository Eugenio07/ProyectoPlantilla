# Fase 9 · `claude_md.py` y sus excepciones

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 9, incremento 2 de 3 (Parte 1 por referencia). Propuesta [sin medir] n.º 3 de v2: se prueba aquí.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `8ca77b4`. `CLAUDE.md` de partida: 54 reglas, máximo 4 líneas, media 1,78 (medido con el mismo
criterio de «regla» que el guion).

### Solución

- `claude_md.py`: falla por regla > `UMBRAL_REGLA` (3) o media > `UMBRAL_MEDIA` (2,0), los dos fijados en el guion con
  su fecha y el valor de partida; avisa de patrones de narración; 3 si no reconoce reglas.
- `claude_md_excepciones.txt` (`§n | inicio | motivo`): la primera, la regla de dependencias de `domain` (4 líneas).
- En la puerta.

### Verificación por criterio

| Caso (en copias temporales) | Esperado | Resultado |
|---|---|---|
| 1 Incidente de 4 líneas con patrones («medido el 2026-11-11», «lo enseñó», «pasó el») | avisa y falla por la proporción | 2 avisos + FALLO (regla de 4 > 3) — Pasó |
| 2 El mismo incidente sin ningún patrón | sigue fallando | FALLO, 0 avisos — Pasó |
| 3 Regla larga sin excepción | falla | Pasó |
| 4 …con excepción y motivo | pasa | exit 0 — Pasó |
| 5 …con excepción sin motivo | falla | Pasó |
| 6 Excepción cuya regla ya cabe | avisa | exit 0 + 1 aviso — Pasó |
| 7 Excepción a una regla que no existe | falla | Pasó |
| El `CLAUDE.md` real | pasa | 53 contadas + 1 exceptuada, media 1,74 — Pasó |

## Revisiones

# Fase 6.5 · `cola.py`, `que_va_ahora.py`, `barrido_texto.py`, `tope_plan.py`

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 6.5, incremento 5 de 5 (puntos 8–12 del prompt; Parte 1 por referencia).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `6970652`. La cola tiene 5 ids (`#PP1` cerrado). Los 20 planes están por debajo de 150 líneas aun
contando Revisiones (`wc -l`, máx. 96). Ranura de grafo: **ninguna** (`docs/para-claude-md.md`).

### Solución

- `cola.py` (puerta): ids únicos (títulos + filas de Cerrados); id reutilizado leyendo el historial (`git show` de cada
  commit del fichero; superficial → 3); campos de cada entrada abierta.
- `que_va_ahora.py` (informa): orden de §5 con la app sin publicar (requisitos de salida primero); hora y commit arriba.
- `barrido_texto.py` (informa): frases de «única salida»/carencia en español, sobre `strings.xml` y la pantalla segura.
- `tope_plan.py` (puerta): umbral 150 (fecha y valor en el guion); exclusiones en `tope_plan_exclusiones.txt` con motivo
  (hoy: Revisiones).
- Ranura de grafo: sin herramienta; no hay paso de frescura (anotado en `docs/para-claude-md.md`).

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| `cola.py`: id duplicado → 1 | Verificable | `mutar.sh`: `#PP3` → `#PP2` → `FALLO: id duplicado: #PP2` | Pasó |
| …entrada sin «Vigente hasta que» → 1 | Verificable | `mutar.sh` en `#PP4` → `falta el campo **Vigente hasta que:**` | Pasó |
| …id que existió y se cerró, reutilizado → 1 | Verificable | clon temporal: commit que quita `#PP1` de Cerrados + entrada nueva `#PP1` → `id reutilizado: #PP1` | Pasó |
| …checkout superficial → 3 | Verificable | `git clone --depth 1` al scratchpad → 3 | Pasó |
| `tope_plan.py`: plan largo → 1 | Verificable | `mutar.sh`: 90 líneas antes de Revisiones en el plan de la Fase 1 → 153 > 150 | Pasó |
| …el mismo exceso dentro de Revisiones → pasa | Verificable | `mutar.sh` → el comando siguió verde (1 = «ciego», que aquí es lo esperado) | Pasó |
| …exclusión sin motivo → 1 | Verificable | `mutar.sh` sobre las exclusiones → `exclusión sin motivo` | Pasó |
| `que_va_ahora.py` y `barrido_texto.py` informan | Comprobación humana declarada | Ejecutor, 2026-11-11: orden correcto sobre la cola real; el barrido encuentra «no se puede» en la actualización obligatoria (sigue siendo cierto) | Declarado |

## Revisiones

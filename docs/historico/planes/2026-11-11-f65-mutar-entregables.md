# Fase 6.5 · `mutar.sh` + `entregables.py`

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 6.5, incremento 2 de 5 (puntos 3 y 4 del prompt; Parte 1 por referencia).
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `d3ca95d`. No existe `config/checks/`. Hasta hoy las mutaciones se corrían con un mutador ad hoc en el
scratchpad (planes de las Fases 2–6).

### Solución

- `mutar.sh <fichero> '<sed>' -- <comando>`: copia propia, `sed` sobre la copia hacia el fichero, `diff -u` ANTES de
  correr, `trap` que restaura siempre (sin git). 0 vista · 1 ciega · 2 uso · 3 sed sin cambios **o no compiló** (se
  detecta en la salida: `e: `, `Compilation error`, una tarea `compile…Kotlin… FAILED`).
- `entregables.py`: lee la columna «Ruta» de `docs/entregables-del-arranque.md`; 0/1/3 (lista ilegible o sin filas → 3).

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| `mutar.sh`: mutación que tumba un test → 0, con el diff impreso | Verificable | `Resource.kt` `is Resource.Error -> this` → `Error(Unknown)` con `testAndroidHostTest` → 0; el diff muestra la línea | Pasó |
| …mutación que no cambia conducta → 1 | Verificable | cambiar un comentario → 1 | Pasó |
| …sed sin coincidencias → 3 | Verificable | patrón inexistente → 3 | Pasó |
| …mutación que rompe la compilación → 3, no 0 | Verificable | `map(` sobra un paréntesis → 3 (en el origen esto daba éxito: deuda abierta allí) | Pasó |
| …mal uso → 2; el fichero queda restaurado en todos los casos | Verificable | sin argumentos → 2; `git diff` del fichero vacío tras las cinco | Pasó |
| `entregables.py` cae con una ruta que no existe | Verificable | `mutar.sh` sobre la lista, `ARCHITECTURE.md` → `NO-EXISTE.md` → 1, `FALTA: docs/NO-EXISTE.md` | Pasó |
| …cae con un fichero registrado borrado | Verificable | `git clone --local` al scratchpad, borrar `docs/ARCHITECTURE.md` → 1 | Pasó |
| …sin la lista → 3, no verde | Verificable | lista movida → 3 | Pasó |

## Revisiones

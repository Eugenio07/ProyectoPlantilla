# Fase 6.5 · `--commit` y el candado

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 6.5, incremento 4 de 5 (puntos 6 y 7 del prompt; Parte 1 por referencia). Es la **primera
  propuesta [sin medir] de v2**, «el candado de la puerta»: se prueba aquí.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `b603ba2`. La puerta existe y no tiene candado ni `--commit`. No hay hooks (Fase 10).

### Solución

- `candado.sh estado|tomar|soltar`. Candado = `mkdir` atómico de `.git/puerta.lock` con el PID de la puerta y la hora
  de arranque de ese PID (`ps -o lstart=`). **VIVO** solo si existe, el PID existe y la hora coincide; lo demás es
  **HUÉRFANO**. `tomar` (solo la puerta): con uno vivo aborta y dice de quién; con uno huérfano lo retira, lo anuncia y
  lo anota en `.git/puerta-huerfanos.log`. Los hooks solo leen `estado`.
- `puerta.sh --commit <fichero> [nuevos…]`: valida los nuevos (existen, no rastreados) antes de correr; si la puerta pasa,
  `git add -u` + los nuevos; se niega si queda algo fuera del índice; `git commit -F`.

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| Con la puerta corriendo: vivo; un hook calla; una segunda puerta aborta; al acabar se suelta | Verificable | `candado_test.sh` 1a–1d | Pasó |
| PID muerto: huérfano; el hook corre y no lo borra; la puerta lo anuncia, lo anota y lo retira | Verificable | 2a–2e | Pasó |
| PID vivo de otro proceso: huérfano | Verificable | 3a–3b | Pasó |
| Sin PID: huérfano | Verificable | 4 | Pasó |
| El test distingue liveness de existencia | Verificable | `mutar.sh` sobre `candado.sh`: «vivo si el directorio existe» → 7 fallos (2a, 2b, 2d, 2e, 3a, 3b, 4) | Pasó |
| `--commit` rechaza un nuevo inexistente, uno ya rastreado y un mensaje vacío | Verificable | exit 2 en los tres, antes de correr la puerta | Pasó |
| `--commit` se niega con algo fuera del índice | Verificable | `puerta.sh --rapido --commit` nombrando solo `candado.sh` → exit 1, «queda algo fuera del índice», HEAD sin cambiar | Pasó |
| `--commit` commitea cuando todo pasa | Verificable | este incremento se commiteó con `puerta.sh --commit` (puerta completa) | Pasó |

## Revisiones
- 2026-11-11 · Ejecutor: tras el intento rechazado, `candado.sh` quedó en el índice y el siguiente `--commit` lo dio por
  «ya rastreado» (miraba el índice). Corregido: rastreado = presente en `HEAD`.

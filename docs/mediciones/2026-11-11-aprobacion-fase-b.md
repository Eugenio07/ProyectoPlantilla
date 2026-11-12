# Aprobación de la fase B de la auditoría, de la decisión de `#PP10` y de los pesos

- **Fecha:** 2026-11-11 (America/Caracas) · **De:** quien decide (Eugenio Martínez), en la conversación con Claude Code.
- **Commit de partida:** `9d8cf6b`.

Mensaje literal:

> 1. Apruebo la fase B
> 2. Acepto la recomendación de 3 crashes en racha
> 3. Acepto las propuestas

**Lectura del ejecutor** (sobre lo que el ejecutor había propuesto justo antes, en la misma conversación):

1. **Fase B aprobada** para la lista entera de `docs/historico/auditorias/AUDIT-2026-11-11.md` que queda abierta
   (`#PP8`–`#PP20`): un PR por nivel (P1, luego P2), un commit por hallazgo, con la puerta.
2. **`#PP10`:** con tres crashes en racha dentro de la ventana, la pantalla segura **se mantiene hasta un reset**
   (Reintentar en la pantalla segura, o en Android la vida sana): el disparo ya no caduca con la ventana.
3. **Pesos:** se aceptan las propuestas del ejecutor en cada entrada abierta de la cola; se firman en `docs/deuda-tecnica.md`
   con esta medición como cita.

## Corrección de `#PP10` (2026-11-11, misma conversación)

El ejecutor avisó de que la recomendación aceptada en el punto 2 era incompleta (mantener el disparo no corta un bucle
lento: la ventana de 10 s nunca junta 3 crashes si cada uno llega a los 5 s del arranque) y preguntó si contar la racha en
Android por vida sana. Respuesta literal de quien decide:

> Sí, por vida sana

**Lectura del ejecutor:** en Android la racha son los crashes seguidos sin vida sana (15 s) entre medias, sin ventana; en
iOS se queda la ventana de 60 s (reapertura manual, sin vida sana). En las dos, una vez disparada, la pantalla segura se
mantiene hasta un reset (Reintentar; en Android también la vida sana).

## Nivel P2 y pesos de `#PP22` y `#PP23` (2026-11-11, misma conversación)

Tras fusionar el P1 (PR #39), el ejecutor preguntó «¿Arranco el nivel P2 con los pesos de #PP22 y #PP23 como higiene?».
Respuesta literal de quien decide:

> Sí, arranca P2

**Lectura del ejecutor:** el nivel P2 cubre `#PP12`–`#PP23` salvo `#PP11` (pendiente solo de un proyecto real); los
pesos de `#PP22` y `#PP23` se firman como higiene; la fase B ya estaba autorizada (punto 1 de arriba), y `#PP21`–`#PP23`
entran en ella por esta respuesta.

## Pesos de `#PP24`–`#PP27` (2026-11-11, misma conversación)

El ejecutor propuso: `#PP24` requisito de salida (ninguna app derivada se publica sin esto); `#PP25` 🟡; `#PP26` y
`#PP27` higiene. Respuesta literal de quien decide:

> Vale, firma los pesos y empieza con #PP26 y #PP27

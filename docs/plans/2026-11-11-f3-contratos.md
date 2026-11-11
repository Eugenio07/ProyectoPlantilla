# Fase 3 · Contratos (rama A: sin backend)

- **Estado:** Implementado
- **Tamaño:** S
- **Entrada:** v2, FASE 3, rama A (Parte 1 por referencia)
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero;
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `df72d5a`. Antecedentes: `grep -rln "contrat"` en `docs/plans`, `docs/historico` y la cola → solo el
plan de la Fase 1 (que cita «contratos» como paquete); **sin antecedentes en `docs/historico/` ni en la cola**.

| Afirmación | Cómo se comprobó | Ramas | HECHO / SUPUESTO |
|---|---|---|---|
| La ranura «Backend» es «ninguno» → rama A | `docs/para-claude-md.md` | — | HECHO |
| No existía `docs/contracts/` | `ls docs` en `df72d5a` | — | HECHO |
| El cliente produce `Server(code, message)` con envelope válido y `Http(status)` sin él | `NetworkErrorMapper.kt` en `df72d5a` (`fromResponse`: `ErrorEnvelope(code: String, message: String)`, sin valores por defecto) y `SafeApiCallTest` | `Server`, `Http`; envelope incompleto → `Http` por lectura del código (no hay test de ese caso) | HECHO para `Server` y `Http`; envelope incompleto, SUPUESTO leído |
| El cliente tolera `minVersion` ausente o malformado | — | — | **SUPUESTO**: lo construye la Fase 4 (`CheckForceUpdateUseCase`), con un test por rama |

¿Qué de lo que asumo que existe lo borra este plan? Nada.

### Tareas

1. Documentación, sin capa de código: `docs/contracts/config-endpoint.md` y `docs/contracts/api-errors.md` (BORRADOR).
2. `docs/para-claude-md.md` (rama A) y `docs/entregables-del-arranque.md` (los dos borradores).

### Verificación por criterio

| Criterio | Clase | Evidencia y mutación | Resultado |
|---|---|---|---|
| Cada exigencia del prompt de la rama A está escrita (BORRADOR en los dos; FAIL-OPEN; «debe enviar `minVersion` SIEMPRE»; «no un permiso para omitirlo»; `ignoreUnknownKeys`; `@SerialName`; el envelope; «nunca se muestra»; los cinco codes; desconocido + telemetría; el fallback «no afirma una causa» y ofrece la «acción neutra») | Verificable | Un `grep` por exigencia sobre los dos ficheros (guion de un solo uso en el scratchpad; un verificador de contratos de verdad es el de la rama B, en la puerta). Real → exit 0. Mutación: copia con «no afirma una causa» → «es neutro» → `FALTA … no afirma una causa`, exit 1. Sin los ficheros → exit 3 [medido 2026-11-11]. El primer intento dio rojo sobre el borrador real por un patrón mal escrito en el guion, no en el borrador | Pasó |
| El envelope del borrador es el que el cliente parsea | Verificable, **heredada sin abrir** de `docs/plans/2026-11-11-f2-cliente-red.md` (2026-11-11, `90f294f`): `statusWithEnvelopeIsServer` y su mutación | — | Pasó (heredada) |

### Lo que no podrá comprobarse aquí

- Que un backend futuro cumpla el borrador: no existe (rama A). Al llegar, rama B o C con su verificador en la puerta.
- El fail-open: lo construye y lo prueba la Fase 4.

## Revisiones

- **2026-11-11 · Auditor sin contexto (pasada de la Fase 3).** Condición de salida: sin defecto de forma → se aplica.
  13 hallazgos, ninguno de forma:
  - 1, 2, 4, 6, 8 (envelope sin «ambos obligatorios»; el 2xx malformado → `Serialization`; ruta de `strings.xml` falsa
    hoy; «malformado» sin definir; el `...` del prompt) → *el borrador pide un dato que falta*: corregidos.
  - 3 (`NETWORK` como code del servidor; Reintentar en rechazos que no cambian) → `NETWORK` pasa a «lo genera el
    cliente»; UNAUTHORIZED y VALIDATION_ERROR sin Reintentar por manual §10. **Las acciones son texto/conducta de
    producto: propuestas, las decide quien decide** (pendiente en la lista de preguntas del arranque).
  - 5 (lo que la Fase 4 necesita: tipado del code, `/v1/config` con 5xx o `{}`, ruta relativa al `baseUrl`, escenarios
    del stub) → añadido a los borradores.
  - 7 (sin autenticación; versión nueva al renombrar) → marcados «propuesta del ejecutor».
  - 9–12 (criterio humano débil, clase mal puesta, heredada sin abrir, HECHO/SUPUESTO, antecedentes) → plan reescrito:
    criterio verificable con mutación; la heredada, marcada; SUPUESTO separado; antecedentes buscados.
  - 13 (archivado en `docs/historico/`) → `docs/historico/` existe desde la Fase 0; sin acción.

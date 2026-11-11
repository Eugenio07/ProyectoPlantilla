# Contrato · Errores de la API — BORRADOR

> **BORRADOR.** Sin backend todavía (v2 Fase 3, rama A). Fuente para construirlo después.

## El envelope

Toda respuesta de error (4xx/5xx) de la API lleva este cuerpo JSON estricto:

```json
{ "code": "STRING_CODE", "message": "texto de servidor" }
```

**Los dos campos son obligatorios.** Un envelope sin `message`, sin `code`, o que no sea JSON estricto, el cliente no lo
reconoce: degrada a `AppError.Http(status)` y **se pierde el code**.

Dos capas, que no se mezclan:

| Campo | Para quién | Regla |
|---|---|---|
| `code` | El cliente | Un identificador estable en MAYÚSCULAS. El cliente lo mapea a **copy propia** |
| `message` | Los registros | Texto del servidor, para diagnóstico. **Nunca se muestra** a quien usa la app |

## Cómo llega cada caso al cliente

Hoy (`shared/src/commonMain/kotlin/org/example/proyecto_plantilla/core/network/NetworkErrorMapper.kt`):

| Respuesta | `AppError` |
|---|---|
| 4xx/5xx con envelope válido | `Server(code, message)` |
| 4xx/5xx sin envelope, o con uno ilegible o incompleto | `Http(status)` |
| 2xx con cuerpo que no se puede deserializar (vacío, malformado, tipo equivocado) | `Serialization` |
| Sin conexión, timeout | `Network` (lo genera el cliente; **no es un code del servidor**) |
| Cualquier otra excepción | `Unknown` |

## Los codes del servidor

| `code` | Cuándo | Acción que ofrece el cliente (**propuesta del ejecutor; la decide quien decide**) |
|---|---|---|
| `MAINTENANCE` | El servicio está en mantenimiento | Reintentar (cambia con el tiempo) |
| `RATE_LIMITED` | Demasiadas peticiones | Reintentar (cambia con el tiempo) |
| `UNAUTHORIZED` | La sesión no es válida | **Ninguna acción de reintento**: reintentar no cambia el rechazo (manual de operación §10, «Retira la acción tras un rechazo que no cambia por reintentar»). Sin sesión en la plantilla; cada app decide adónde lleva |
| `VALIDATION_ERROR` | La petición no es válida | **Ninguna acción de reintento**, por la misma regla |
| *(desconocido)* | Cualquier otro `code`, incluido un `NETWORK` que enviara el servidor | Reintentar (la acción neutra). **Se registra como telemetría**: el code, nunca el `message` |

En el cliente, el code se tipa como un enum con un valor `Unknown` de respaldo y su copy vive en los recursos de texto
multiplataforma (`shared/src/commonMain/composeResources/values/strings.xml`). Los dos los crea la Fase 5 (`ErrorState`
con el mapeo code→copy). La telemetría del code desconocido va por la interfaz de `core/log`. La copy es **texto de
producto**: su versión final es de quien decide (manual de operación §6).

**El texto del fallback (code desconocido) no afirma una causa:** dice que no se pudo y ofrece la acción neutra
(Reintentar). Si hiciera falta saber la causa para escribirlo, el code no sería desconocido: se mapearía.

# Contrato · `GET /v1/config` — BORRADOR

> **BORRADOR.** No hay backend todavía (ranura «Backend: ninguno», v2 Fase 3, rama A). Este documento es la fuente para
> construirlo después. Cuando exista, la verdad del contrato pasa al esquema que publique el backend (rama B o C) y este
> borrador se archiva en `docs/historico/`.

## Petición

`GET v1/config`, **ruta relativa** resuelta contra `baseUrl`, que termina en `/` (en el cliente, `defaultRequest {
url(baseUrl) }`). Sin cuerpo. *(Sin autenticación: propuesta del ejecutor, no la pide el prompt de v2.)*

## Respuesta `200`

```json
{
  "minVersion": "1.4.0",
  "storeUrl": {
    "android": "https://play.google.com/store/apps/details?id=<applicationId>",
    "ios": "https://apps.apple.com/app/id<appStoreId>"
  },
  "...": "otras claves futuras"
}
```

| Clave | Tipo | Obligatoria | Qué es |
|---|---|---|---|
| `minVersion` | string, versión semántica `x.y.z` (admite sufijos `-rc1`, `+build`) | **Sí, para el backend** | La versión mínima de la app que se puede usar. Una app más vieja muestra la pantalla de actualización obligatoria (bloqueante) |
| `storeUrl` | objeto `{"android": string, "ios": string}`, cada clave opcional | No (pero sin ella la pantalla de actualización no tiene salida) | La ficha de la app en la tienda de cada plataforma. La pantalla de actualización muestra «Actualizar» y la abre. Solo vale `https://` con host y sin espacios |

**Tolerancia de `storeUrl` (`#PP24`):** la URL nunca decide si se bloquea. Ausente, de la otra plataforma solamente, o
inválida (otro esquema, vacía, con espacios, sin host, otro tipo, o `storeUrl` que no es un objeto) → la pantalla de
actualización sale **sin** botón y el cliente registra un aviso (ausente / inválida). Cada app derivada pone sus URLs.

**Claves desconocidas:** el cliente las ignora (`ignoreUnknownKeys = true`), así que el backend puede añadir claves sin
romper a las apps publicadas. *(Que renombrar o cambiar el tipo de una clave exija una versión nueva del endpoint es
propuesta del ejecutor, no la pide el prompt de v2.)*

## Tolerancia del cliente: FAIL-OPEN

El cliente tolera `minVersion` **ausente o malformado**: en ese caso la app **no se bloquea**, sigue como si estuviera al
día y registra un aviso. Una config rota no deja a nadie fuera de la app.

- **Ausente:** la clave no está, o vale `null`. Un `200` con `{}` es este caso.
- **Malformado:** está, pero no es una versión válida según `SemanticVersionComparator.isValid` (Fase 4): tres segmentos
  numéricos `x.y.z`, con sufijos `-…` y `+…` admitidos y espacios alrededor tolerados. Son malformados `""`, `"abc"`,
  `"1.4"` (sin patch), `"1.3rc.0"` y un número JSON en vez de string. La tolerancia del comparador («3rc» → 3) **no**
  hace válido un valor: solo decide cómo se comparan dos versiones ya válidas o la versión local.

**El backend debe enviar `minVersion` SIEMPRE.** El fail-open es una defensa del cliente, no un permiso para omitirlo:
una config sin `minVersion` desactiva en silencio la actualización obligatoria.

Distinto del fail-open: **no poder traer la config** (sin red, timeout, 4xx/5xx, o un `200` con cuerpo vacío o que no es
JSON). Eso no es «al día»: la Splash muestra el error con **Reintentar** (v2 Fase 4).

## Escenarios del stub (Fase 4, `ConfigMockScenario`)

| Escenario | Respuesta | Resultado esperado |
|---|---|---|
| Al día | `200 {"minVersion":"0.0.1"}` | Home |
| Actualización obligatoria | `200 {"minVersion":"999.0.0","storeUrl":{"android":"https://play.google.com/…","ios":"https://apps.apple.com/…"}}` | Pantalla de actualización (bloqueante) con «Actualizar» |
| Actualización obligatoria sin tienda | `200 {"minVersion":"999.0.0"}` | Pantalla de actualización, sin botón (y un aviso) |
| `minVersion` ausente | `200 {}` | Fail-open: Home + aviso |
| `minVersion` malformado | `200 {"minVersion":"abc"}` | Fail-open: Home + aviso |
| Error del servidor | `503` con envelope `MAINTENANCE` | Error con Reintentar |

## Errores

Con el envelope de `docs/contracts/api-errors.md`.

## Regla para los DTO

La clave JSON serializada es la de este contrato; si la propiedad Kotlin se llama distinto, lleva `@SerialName`.

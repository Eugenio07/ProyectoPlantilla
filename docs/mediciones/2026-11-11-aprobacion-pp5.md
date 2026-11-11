# Aprobación de los textos y las acciones de `#PP5`

- **Fecha:** 2026-11-11 (America/Caracas) · **De:** quien decide (Eugenio Martínez), en la conversación con Claude Code.
- **Commit de partida:** `4494aae`.

El ejecutor propuso, una por una, la copy de `strings.xml`, los tres textos de `CrashSafeScreen`, las acciones por code de
`docs/contracts/api-errors.md` y tres opciones para la salida de la actualización obligatoria (a: botón a la tienda con la
URL en la config remota; b: sin salida; c: cerrar la app, descartada en iOS). Respuesta literal:

> Vale, no me detengo mucho con #PP5 ya que esta es una app plantilla y puede que los textos cambien una vez que se use la
> plantilla para un proyecto real, te acepto las recomendaciones.

El auditor sin contexto señaló que la copy de red recomendada («No se pudo conectar. Revisa tu conexión e inténtalo de
nuevo.») seguía afirmando una causa (condición 4 de `#PP5`). Se volvió a preguntar; respuesta literal:

> Sin afirmar causa (Recomendado)

**Lo aprobado (como provisional de plantilla; cada app derivada lo revisa, `CLAUDE.md` §2):**

| Pieza | Texto o acción |
|---|---|
| `splash_loading` | Cargando |
| `splash_force_update_title` | Actualización necesaria |
| `splash_force_update_body` | Esta versión de la app ya no se puede usar. Instala la versión %1$s o una más reciente para continuar. |
| `error_network` | **No se pudo conectar. Inténtalo de nuevo en unos momentos.** (cambiado) |
| `error_maintenance` | El servicio está en mantenimiento. Inténtalo de nuevo en unos minutos. |
| `error_rate_limited` | Hubo demasiados intentos seguidos. Espera un momento e inténtalo de nuevo. |
| `error_unauthorized` | Tu sesión no es válida. |
| `error_validation` | No se pudo procesar la solicitud. |
| `error_generic` | No se pudo completar la acción. |
| `action_retry` | Reintentar |
| `home_title` · `home_open_detail` · `detail_title` | Inicio · Ver detalle · Detalle %1$s (placeholders) |
| `CrashSafeScreen` | «Algo no salió bien» · «La app se cerró varias veces seguidas. Puedes volver a intentarlo.» · «Reintentar» |
| Acciones por code | `MAINTENANCE`, `RATE_LIMITED`, desconocido: Reintentar · `UNAUTHORIZED`, `VALIDATION_ERROR`: ninguna |
| Salida de la actualización obligatoria | **(a)** botón «Actualizar» que abre la tienda (URL por plataforma en la config remota; sin URL, sin botón) → `#PP24` |

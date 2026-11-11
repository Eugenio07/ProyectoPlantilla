package org.example.proyecto_plantilla.ui.components

/**
 * Qué ve quien usa la app ante un error (`docs/contracts/api-errors.md`). [canRetry]: si reintentar puede cambiar el
 * resultado (manual de operación §10: «retira la acción tras un rechazo que no cambia por reintentar»).
 */
enum class ErrorKind(
    val canRetry: Boolean,
) {
    NETWORK(true),
    MAINTENANCE(true),
    RATE_LIMITED(true),
    UNAUTHORIZED(false),
    VALIDATION(false),

    /** Code desconocido y todo lo que no trae code: no afirma causa, ofrece la acción neutra. */
    GENERIC(true),
}

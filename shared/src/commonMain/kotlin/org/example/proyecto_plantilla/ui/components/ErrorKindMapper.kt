package org.example.proyecto_plantilla.ui.components

import org.example.proyecto_plantilla.core.error.AppError
import org.example.proyecto_plantilla.core.log.Logger

/**
 * `AppError` → [ErrorKind] (`docs/contracts/api-errors.md`). Un code del servidor que no se conoce (incluido un `NETWORK`
 * que mandara el servidor: ese lo genera el cliente) es [ErrorKind.GENERIC] y **se registra como telemetría: el code,
 * nunca el `message`**.
 */
class ErrorKindMapper(private val logger: Logger) {
    fun map(error: AppError): ErrorKind = when (error) {
        AppError.Network -> ErrorKind.NETWORK
        is AppError.Server -> knownCodes[error.code] ?: unknownCode(error.code)
        is AppError.Http, AppError.Serialization, AppError.Unknown -> ErrorKind.GENERIC
    }

    private fun unknownCode(code: String): ErrorKind {
        logger.info(TAG, "code desconocido: $code")
        return ErrorKind.GENERIC
    }

    companion object {
        const val TAG = "ErrorCode"

        private val knownCodes = mapOf(
            "MAINTENANCE" to ErrorKind.MAINTENANCE,
            "RATE_LIMITED" to ErrorKind.RATE_LIMITED,
            "UNAUTHORIZED" to ErrorKind.UNAUTHORIZED,
            "VALIDATION_ERROR" to ErrorKind.VALIDATION,
        )
    }
}

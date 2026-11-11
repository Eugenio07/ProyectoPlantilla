package org.example.proyecto_plantilla.core.error

/**
 * Error de la app, independiente de la capa que lo produjo. Kotlin puro: sin dependencias.
 *
 * [Server.message] es texto del servidor: va a los registros y **nunca** se muestra. Lo que ve quien usa la app sale
 * del mapeo de [Server.code] a copy propia.
 */
sealed interface AppError {
    data object Network : AppError

    data class Http(val status: Int) : AppError

    data object Serialization : AppError

    data class Server(val code: String, val message: String) : AppError

    data object Unknown : AppError
}

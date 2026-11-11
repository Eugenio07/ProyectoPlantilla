package org.example.proyecto_plantilla.core.network

import org.example.proyecto_plantilla.core.result.Resource
import kotlin.coroutines.cancellation.CancellationException

/**
 * Ejecuta [block] y devuelve [Resource]. NUNCA lanza, salvo `CancellationException`, que se relanza siempre.
 *
 * Excepción deliberada a `TooGenericExceptionCaught`: captura todo lo que no sea cancelación y lo mapea a `AppError`
 * (v2 Fase 2, «nunca lanza»). Capturar tipos concretos dejaría escapar los que no se previeron.
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeApiCall(
    mapper: NetworkErrorMapper,
    block: suspend () -> T,
): Resource<T> =
    try {
        Resource.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Resource.Error(mapper.map(e))
    }

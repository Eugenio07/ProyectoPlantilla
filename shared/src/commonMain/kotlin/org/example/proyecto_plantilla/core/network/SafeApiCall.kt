package org.example.proyecto_plantilla.core.network

import org.example.proyecto_plantilla.core.result.Resource
import kotlin.coroutines.cancellation.CancellationException

/** Ejecuta [block] y devuelve [Resource]. NUNCA lanza, salvo `CancellationException`, que se relanza siempre. */
suspend fun <T> safeApiCall(mapper: NetworkErrorMapper, block: suspend () -> T): Resource<T> =
    try {
        Resource.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Resource.Error(mapper.map(e))
    }

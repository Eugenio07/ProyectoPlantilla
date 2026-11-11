package org.example.proyecto_plantilla.core.network

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.ContentConvertException
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.example.proyecto_plantilla.core.error.AppError
import kotlin.coroutines.cancellation.CancellationException

/**
 * Traduce un `Throwable` de la capa de red a [AppError].
 *
 * Ktor NO lanza `SerializationException` al deserializar una respuesta: lanza `JsonConvertException`, subtipo de
 * [ContentConvertException]. Se capturan las dos; si no, la rama `Serialization` sería código muerto.
 */
class NetworkErrorMapper(private val json: Json = AppJson) {
    suspend fun map(throwable: Throwable): AppError = when (throwable) {
        is ResponseException -> fromResponse(throwable)
        is HttpRequestTimeoutException -> AppError.Network
        is ContentConvertException -> AppError.Serialization
        is SerializationException -> AppError.Serialization
        is IOException -> AppError.Network
        else -> AppError.Unknown
    }

    /** Un 4xx/5xx con el envelope `{code, message}` del contrato es `Server`; sin él, `Http(status)`. */
    private suspend fun fromResponse(e: ResponseException): AppError {
        val body = try {
            e.response.bodyAsText()
        } catch (c: CancellationException) {
            throw c
        } catch (_: Exception) {
            null
        }
        val envelope = body?.let {
            try {
                json.decodeFromString(ErrorEnvelope.serializer(), it)
            } catch (_: SerializationException) {
                null
            } catch (_: IllegalArgumentException) {
                null
            }
        }
        return envelope?.let { AppError.Server(it.code, it.message) } ?: AppError.Http(e.response.status.value)
    }

    @Serializable
    private data class ErrorEnvelope(val code: String, val message: String)
}

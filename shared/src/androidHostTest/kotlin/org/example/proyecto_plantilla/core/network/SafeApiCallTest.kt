package org.example.proyecto_plantilla.core.network

import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import org.example.proyecto_plantilla.core.error.AppError
import org.example.proyecto_plantilla.core.result.Resource
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Pipeline Ktor real con `MockEngine`, en `runBlocking`: el reloj virtual de `runTest` dispara el `HttpTimeout` antes de
 * que el mock responda (v2 §2, «Tests»).
 */
class SafeApiCallTest {
    @Serializable
    private data class Ping(val ok: Boolean)

    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private val mapper = NetworkErrorMapper()

    private fun client(timeoutMs: Long = 15_000, handler: suspend (io.ktor.client.engine.mock.MockRequestHandleScope) -> io.ktor.client.request.HttpResponseData) =
        HttpClientFactory(NetworkConfig("https://example.test/", enableLogging = false, requestTimeoutMs = timeoutMs))
            .create(MockEngine { handler(this) })

    private fun call(client: io.ktor.client.HttpClient): Resource<Ping> = runBlocking<Resource<Ping>> {
        safeApiCall(mapper) { client.get("v1/ping").body<Ping>() }
    }

    @Test
    fun validBodyIsSuccess() {
        val c = client { it.respond("""{"ok":true,"extra":1}""", HttpStatusCode.OK, json) }
        assertEquals(Resource.Success(Ping(true)), call(c))
    }

    @Test
    fun malformedBodyOn200IsSerialization() {
        val c = client { it.respond("""{"ok":"no-es-booleano"""", HttpStatusCode.OK, json) }
        assertEquals(Resource.Error(AppError.Serialization), call(c))
    }

    @Test
    fun statusWithoutEnvelopeIsHttp() {
        val c = client { it.respond("not found", HttpStatusCode.NotFound) }
        assertEquals(Resource.Error(AppError.Http(404)), call(c))
    }

    @Test
    fun statusWithEnvelopeIsServer() {
        val c = client {
            it.respond("""{"code":"MAINTENANCE","message":"texto del servidor"}""", HttpStatusCode.ServiceUnavailable, json)
        }
        assertEquals(Resource.Error(AppError.Server("MAINTENANCE", "texto del servidor")), call(c))
    }

    @Test
    fun ioFailureIsNetwork() {
        val c = client { throw IOException("sin red") }
        assertEquals(Resource.Error(AppError.Network), call(c))
    }

    @Test
    fun timeoutIsNetwork() {
        val c = client(timeoutMs = 100) {
            delay(2_000)
            it.respond("""{"ok":true}""", HttpStatusCode.OK, json)
        }
        assertEquals(Resource.Error(AppError.Network), call(c))
    }

    @Test
    fun unexpectedThrowableIsUnknown() {
        val c = client { throw IllegalStateException("otra cosa") }
        assertEquals(Resource.Error(AppError.Unknown), call(c))
    }

    @Test
    fun plainSerializationExceptionIsSerialization() {
        val r = runBlocking { safeApiCall(mapper) { throw kotlinx.serialization.SerializationException("x") } }
        assertEquals(Resource.Error(AppError.Serialization), r)
    }

    @Test
    fun cancellationIsRethrownNotMapped() {
        assertFailsWith<CancellationException> {
            runBlocking { safeApiCall(mapper) { throw CancellationException("cancelado") } }
        }
    }
}

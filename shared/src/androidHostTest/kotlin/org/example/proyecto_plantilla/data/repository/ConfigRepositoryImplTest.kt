package org.example.proyecto_plantilla.data.repository

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.example.proyecto_plantilla.core.error.AppError
import org.example.proyecto_plantilla.core.network.HttpClientFactory
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.network.NetworkErrorMapper
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.data.remote.api.createConfigApi
import org.example.proyecto_plantilla.domain.model.AppConfig
import kotlin.test.Test
import kotlin.test.assertEquals

/** Repositorio con Ktorfit REAL (código generado) sobre `MockEngine`: mide `#PP1` en JVM. */
class ConfigRepositoryImplTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val baseUrl = "https://example.test/"
    var lastPath: String? = null

    private fun repo(status: HttpStatusCode, body: String): ConfigRepositoryImpl {
        val client = HttpClientFactory(NetworkConfig(baseUrl, enableLogging = false)).create(
            MockEngine { request ->
                lastPath = request.url.encodedPath
                respond(body, status, jsonHeaders)
            },
        )
        val api = Ktorfit.Builder().baseUrl(baseUrl).httpClient(client).build().createConfigApi()
        return ConfigRepositoryImpl(api, NetworkErrorMapper())
    }

    private fun get(r: ConfigRepositoryImpl) = runBlocking { r.getConfig() }

    @Test
    fun validConfigIsSuccessAndHitsTheContractPath() {
        assertEquals(Resource.Success(AppConfig("1.4.0")), get(repo(HttpStatusCode.OK, """{"minVersion":"1.4.0"}""")))
        assertEquals("/v1/config", lastPath)
    }

    @Test
    fun emptyObjectIsSuccessWithNullMinVersion() =
        assertEquals(Resource.Success(AppConfig(null)), get(repo(HttpStatusCode.OK, "{}")))

    @Test
    fun serverErrorWithEnvelopeIsErrorNotThrow() = assertEquals(
        Resource.Error(AppError.Server("MAINTENANCE", "m")),
        get(repo(HttpStatusCode.ServiceUnavailable, """{"code":"MAINTENANCE","message":"m"}""")),
    )

    @Test
    fun emptyJsonBodyIsSerializationNotThrow() =
        assertEquals(Resource.Error(AppError.Serialization), get(repo(HttpStatusCode.OK, "")))
}

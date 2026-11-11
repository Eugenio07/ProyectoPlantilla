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

/** Ktorfit (código generado) en Kotlin/Native, en el simulador: mide `#PP1` en iOS. */
class ConfigRepositoryIosTest {
    private fun get(status: HttpStatusCode, body: String): Resource<AppConfig> {
        val base = "https://example.test/"
        val client = HttpClientFactory(NetworkConfig(base, enableLogging = false)).create(
            MockEngine { respond(body, status, headersOf(HttpHeaders.ContentType, "application/json")) },
        )
        val repo = ConfigRepositoryImpl(Ktorfit.Builder().baseUrl(base).httpClient(client).build().createConfigApi(), NetworkErrorMapper())
        return runBlocking { repo.getConfig() }
    }

    @Test
    fun ktorfitCallWorksOnNative() =
        assertEquals(Resource.Success(AppConfig("1.4.0")), get(HttpStatusCode.OK, """{"minVersion":"1.4.0"}"""))

    /** Brazo de control: el mismo camino con un 500 no puede dar Success. */
    @Test
    fun ktorfitErrorOnNativeIsError() =
        assertEquals(Resource.Error(AppError.Http(500)), get(HttpStatusCode.InternalServerError, "boom"))
}

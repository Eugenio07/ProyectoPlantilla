package org.example.proyecto_plantilla.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** El `Json` de la app: tolera claves desconocidas del servidor. */
val AppJson: Json =
    Json {
        ignoreUnknownKeys = true
        isLenient = false
    }

/**
 * Crea el `HttpClient` con el ENGINE INYECTADO: OkHttp en Android, Darwin en iOS y `MockEngine` en los tests y en el
 * stub
 * de depuración. `expectSuccess = true`: un 4xx/5xx llega como `ResponseException` y lo mapea [NetworkErrorMapper].
 */
class HttpClientFactory(
    private val config: NetworkConfig,
    private val json: Json = AppJson,
) {
    fun create(engine: HttpClientEngine): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(json) }
            install(HttpTimeout) {
                requestTimeoutMillis = config.requestTimeoutMs
                connectTimeoutMillis = config.requestTimeoutMs
                socketTimeoutMillis = config.requestTimeoutMs
            }
            if (config.enableLogging) {
                install(Logging) { level = LogLevel.INFO }
            }
            defaultRequest { url(config.baseUrl) }
        }
}

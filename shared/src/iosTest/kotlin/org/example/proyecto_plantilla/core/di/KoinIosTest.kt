package org.example.proyecto_plantilla.core.di

import io.ktor.client.HttpClient
import org.example.proyecto_plantilla.core.network.NetworkErrorMapper
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

/** El grafo con el módulo de iOS, arrancado como lo arranca Swift. Un solo test: Koin es global en el proceso. */
class KoinIosTest {
    @Test
    fun startKoinIosIsIdempotentAndResolvesTheIosGraph() {
        val first = startKoinIos(baseUrl = "https://example.test/", isDebug = false)
        val second = startKoinIos(baseUrl = "https://example.test/", isDebug = false)
        assertSame(first, second, "startKoinIos no es idempotente")
        assertNotNull(first.getOrNull<HttpClient>(), "HttpClient")
        assertNotNull(first.getOrNull<NetworkErrorMapper>(), "NetworkErrorMapper")
    }
}

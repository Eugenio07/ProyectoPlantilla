package org.example.proyecto_plantilla.core.di

import android.content.Context
import io.ktor.client.HttpClient
import io.mockk.mockk
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.koin.android.ext.koin.androidContext
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.network.NetworkErrorMapper
import org.example.proyecto_plantilla.domain.repository.ConfigRepository
import org.example.proyecto_plantilla.domain.usecase.CheckForceUpdateUseCase
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/** El grafo con el módulo de ANDROID. El de iOS tiene su propio test en iosTest (trampa 1 del catálogo de v2). */
class AndroidGraphTest {
    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun androidGraphResolvesHttpClientAndMapper() {
        val koin = initKoin(listOf(androidPlatformModule(NetworkConfig("https://example.test/", false)))) {
            androidContext(mockk<Context>(relaxed = true))
        }.koin
        assertNotNull(koin.getOrNull<HttpClient>(), "HttpClient")
        assertNotNull(koin.getOrNull<NetworkErrorMapper>(), "NetworkErrorMapper")
        assertNotNull(koin.getOrNull<ConfigRepository>(), "ConfigRepository")
        assertNotNull(koin.getOrNull<AppVersionProvider>(), "AppVersionProvider")
        assertNotNull(koin.getOrNull<CheckForceUpdateUseCase>(), "CheckForceUpdateUseCase")
    }
}

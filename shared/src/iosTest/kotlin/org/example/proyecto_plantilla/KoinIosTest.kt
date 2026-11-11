package org.example.proyecto_plantilla

import io.ktor.client.HttpClient
import org.example.proyecto_plantilla.core.network.NetworkErrorMapper
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.domain.repository.ConfigRepository
import org.example.proyecto_plantilla.domain.usecase.CheckForceUpdateUseCase
import org.example.proyecto_plantilla.ui.components.ErrorKindMapper
import org.example.proyecto_plantilla.ui.detail.DetailViewModel
import org.example.proyecto_plantilla.ui.home.HomeViewModel
import org.example.proyecto_plantilla.ui.splash.SplashViewModel
import org.koin.core.parameter.parametersOf
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

/** El grafo con el módulo de iOS, arrancado como lo arranca Swift. Un solo test: Koin es global en el proceso. */
class KoinIosTest {
    @Test
    fun startKoinIosIsIdempotentAndResolvesTheIosGraph() {
        val first = startKoinIos(baseUrl = "https://example.test/", useMockConfig = false, isDebug = false)
        val second = startKoinIos(baseUrl = "https://example.test/", useMockConfig = false, isDebug = false)
        assertSame(first, second, "startKoinIos no es idempotente")
        assertNotNull(first.getOrNull<HttpClient>(), "HttpClient")
        assertNotNull(first.getOrNull<NetworkErrorMapper>(), "NetworkErrorMapper")
        assertNotNull(first.getOrNull<ConfigRepository>(), "ConfigRepository")
        assertNotNull(first.getOrNull<AppVersionProvider>(), "AppVersionProvider")
        assertNotNull(first.getOrNull<CheckForceUpdateUseCase>(), "CheckForceUpdateUseCase")
        assertNotNull(first.getOrNull<ErrorKindMapper>(), "ErrorKindMapper")
        assertNotNull(first.getOrNull<SplashViewModel>(), "SplashViewModel")
        assertNotNull(first.getOrNull<HomeViewModel>(), "HomeViewModel")
        assertNotNull(first.getOrNull<DetailViewModel> { parametersOf("42") }, "DetailViewModel")
    }
}

package org.example.proyecto_plantilla.core.di

import android.content.Context
import io.ktor.client.HttpClient
import io.mockk.mockk
import org.example.proyecto_plantilla.core.crash.CrashBreakerConfig
import org.example.proyecto_plantilla.core.crash.CrashCircuitBreaker
import org.example.proyecto_plantilla.core.crash.CrashReporter
import org.example.proyecto_plantilla.core.crash.GlobalCrashHandler
import org.example.proyecto_plantilla.core.crash.HealthyUptimeReset
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.network.NetworkErrorMapper
import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.domain.repository.ConfigRepository
import org.example.proyecto_plantilla.domain.usecase.CheckForceUpdateUseCase
import org.example.proyecto_plantilla.ui.components.ErrorKindMapper
import org.example.proyecto_plantilla.ui.detail.DetailViewModel
import org.example.proyecto_plantilla.ui.home.HomeViewModel
import org.example.proyecto_plantilla.ui.splash.SplashViewModel
import org.example.proyecto_plantilla.ui.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** El grafo con el módulo de ANDROID. El de iOS tiene su propio test en iosTest (trampa 1 del catálogo de v2). */
class AndroidGraphTest {
    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun androidGraphResolvesHttpClientAndMapper() {
        val koin =
            initKoin(
                listOf(
                    androidPlatformModule(
                        NetworkConfig("https://example.test/", false),
                        engineOverride = null,
                        buildInfo = AppBuildInfo(isDebug = false),
                    ),
                    uiModule,
                ),
            ) {
                androidContext(mockk<Context>(relaxed = true))
            }.koin
        assertNotNull(koin.getOrNull<HttpClient>(), "HttpClient")
        assertNotNull(koin.getOrNull<GlobalCrashHandler>(), "GlobalCrashHandler")
        assertNotNull(koin.getOrNull<CrashReporter>(), "CrashReporter")
        assertNotNull(koin.getOrNull<CrashCircuitBreaker>(), "CrashCircuitBreaker")
        assertNotNull(koin.getOrNull<HealthyUptimeReset>(), "HealthyUptimeReset")
        assertEquals(10_000, koin.get<CrashBreakerConfig>().windowMs, "ventana de Android")
        assertNotNull(koin.getOrNull<NetworkErrorMapper>(), "NetworkErrorMapper")
        assertNotNull(koin.getOrNull<ConfigRepository>(), "ConfigRepository")
        assertNotNull(koin.getOrNull<AppVersionProvider>(), "AppVersionProvider")
        assertNotNull(koin.getOrNull<CheckForceUpdateUseCase>(), "CheckForceUpdateUseCase")
        assertNotNull(koin.getOrNull<ErrorKindMapper>(), "ErrorKindMapper")
        assertNotNull(koin.getOrNull<SplashViewModel>(), "SplashViewModel")
        assertNotNull(koin.getOrNull<HomeViewModel>(), "HomeViewModel")
        assertNotNull(koin.getOrNull<DetailViewModel> { parametersOf("42") }, "DetailViewModel")
    }
}

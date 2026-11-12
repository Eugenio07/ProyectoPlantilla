package org.example.proyecto_plantilla.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.example.proyecto_plantilla.core.crash.CrashBreakerConfig
import org.example.proyecto_plantilla.core.crash.CrashCounterStore
import org.example.proyecto_plantilla.core.crash.CrashReporter
import org.example.proyecto_plantilla.core.crash.GlobalCrashHandler
import org.example.proyecto_plantilla.core.crash.IosGlobalCrashHandler
import org.example.proyecto_plantilla.core.crash.UserDefaultsCrashCounterStore
import org.example.proyecto_plantilla.core.log.IosLogger
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import org.example.proyecto_plantilla.core.platform.AppPlatform
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.core.platform.IosAppVersionProvider
import org.koin.core.module.Module
import org.koin.dsl.module

/** Lo que iOS aporta al grafo: su config (de Swift), su engine (o el stub), y sus proveedores. */
fun iosPlatformModule(
    config: NetworkConfig,
    engineOverride: HttpClientEngine?,
    buildInfo: AppBuildInfo,
    crashReporter: CrashReporter,
): Module =
    module {
        single { config }
        single { buildInfo }
        single { crashReporter }
        single<GlobalCrashHandler> { IosGlobalCrashHandler(get(), get(), get()) }
        single<CrashCounterStore> { UserDefaultsCrashCounterStore() }
        // Override de las ventanas comunes: en iOS la reapertura es MANUAL, así que la ventana es más larga.
        single { CrashBreakerConfig(threshold = 3, windowMs = 60_000, healthyUptimeMs = 75_000) }
        single<HttpClientEngine> { engineOverride ?: Darwin.create() }
        single<AppVersionProvider> { IosAppVersionProvider() }
        single { AppPlatform.IOS }
        single<Logger> { IosLogger() }
    }

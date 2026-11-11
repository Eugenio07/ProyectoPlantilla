package org.example.proyecto_plantilla.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.example.proyecto_plantilla.core.crash.GlobalCrashHandler
import org.example.proyecto_plantilla.core.crash.IosGlobalCrashHandler
import org.example.proyecto_plantilla.core.log.IosLogger
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.core.platform.IosAppVersionProvider
import org.koin.core.module.Module
import org.koin.dsl.module

/** Lo que iOS aporta al grafo: su config (de Swift), su engine (o el stub), y sus proveedores. */
fun iosPlatformModule(config: NetworkConfig, engineOverride: HttpClientEngine?, buildInfo: AppBuildInfo): Module = module {
    single { config }
    single { buildInfo }
    single<GlobalCrashHandler> { IosGlobalCrashHandler(get()) }
    single<HttpClientEngine> { engineOverride ?: Darwin.create() }
    single<AppVersionProvider> { IosAppVersionProvider() }
    single<Logger> { IosLogger() }
}

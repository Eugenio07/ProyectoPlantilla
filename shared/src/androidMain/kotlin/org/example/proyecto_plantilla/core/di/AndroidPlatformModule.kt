package org.example.proyecto_plantilla.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.example.proyecto_plantilla.core.crash.AndroidGlobalCrashHandler
import org.example.proyecto_plantilla.core.crash.GlobalCrashHandler
import org.example.proyecto_plantilla.core.log.AndroidLogger
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.platform.AndroidAppVersionProvider
import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Lo que Android aporta al grafo: su config (de `BuildConfig`) y su engine. [engineOverride] es el stub de la variante
 * debug (`provideConfigEngine()` de `androidApp`); en release es `null` y se usa OkHttp.
 */
fun androidPlatformModule(config: NetworkConfig, engineOverride: HttpClientEngine?, buildInfo: AppBuildInfo): Module = module {
    single { config }
    single { buildInfo }
    single<GlobalCrashHandler> { AndroidGlobalCrashHandler(androidContext(), get(), get()) }
    single<HttpClientEngine> { engineOverride ?: OkHttp.create() }
    single<AppVersionProvider> { AndroidAppVersionProvider(androidContext()) }
    single<Logger> { AndroidLogger() }
}

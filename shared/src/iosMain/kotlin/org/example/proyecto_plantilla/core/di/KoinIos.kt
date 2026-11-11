package org.example.proyecto_plantilla.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.example.proyecto_plantilla.core.log.IosLogger
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.core.platform.IosAppVersionProvider
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.core.KoinApplication
import org.koin.dsl.module

/** Lo que iOS aporta al grafo: su config (recibida de Swift) y su engine. */
fun iosPlatformModule(config: NetworkConfig): Module = module {
    single { config }
    single<HttpClientEngine> { Darwin.create() }
    single<AppVersionProvider> { IosAppVersionProvider() }
    single<Logger> { IosLogger() }
}

private var koinApplication: KoinApplication? = null

/**
 * Arranca Koin en iOS, que no tiene `Application`. Swift la llama en el `init()` del `@main`, con TODOS los argumentos
 * explícitos y el gate `#if DEBUG` en las dos ramas (los valores por defecto de Kotlin no llegan a Swift).
 * Idempotente: una segunda llamada devuelve el mismo `Koin`. Guarda el `Koin` aquí porque en iOS no se usa
 * `GlobalContext` (trampa 5 de v2).
 */
fun startKoinIos(baseUrl: String, isDebug: Boolean): Koin {
    koinApplication?.let { return it.koin }
    val app = initKoin(listOf(iosPlatformModule(NetworkConfig(baseUrl = baseUrl, enableLogging = isDebug))))
    koinApplication = app
    return app.koin
}

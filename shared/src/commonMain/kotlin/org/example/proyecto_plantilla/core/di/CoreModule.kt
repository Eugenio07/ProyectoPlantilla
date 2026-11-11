package org.example.proyecto_plantilla.core.di

import io.ktor.client.HttpClient
import org.example.proyecto_plantilla.core.crash.CrashBreakerConfig
import org.example.proyecto_plantilla.core.crash.CrashCircuitBreaker
import org.example.proyecto_plantilla.core.network.AppJson
import org.example.proyecto_plantilla.core.network.HttpClientFactory
import org.example.proyecto_plantilla.core.network.NetworkErrorMapper
import org.koin.core.KoinApplication
import kotlin.time.Clock
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * El núcleo común. Necesita que cada plataforma aporte `NetworkConfig` y `HttpClientEngine`: lo que falte en el módulo
 * de una plataforma solo revienta en esa plataforma (trampa 1 de v2), por eso cada una tiene su test de grafo.
 */
val coreModule: Module = module {
    single { AppJson }
    single { NetworkErrorMapper(get()) }
    single { HttpClientFactory(get(), get()) }
    single<HttpClient> { get<HttpClientFactory>().create(get()) }
    // Ventanas de ANDROID (relanzamiento automático en ~1 s). iOS las sobrescribe en su módulo (Koin: el último gana).
    single { CrashBreakerConfig(threshold = 3, windowMs = 10_000, healthyUptimeMs = 15_000) }
    single { CrashCircuitBreaker(get(), get()) { Clock.System.now().toEpochMilliseconds() } }
}

/** Arranca Koin con el núcleo común y los módulos que aporta cada plataforma (config, engine, proveedores). */
fun initKoin(platformModules: List<Module>, appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()
        modules(listOf(coreModule, dataModule) + platformModules)
    }

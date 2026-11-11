package org.example.proyecto_plantilla.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.koin.core.module.Module
import org.koin.dsl.module

/** Lo que Android aporta al grafo: su config (de `BuildConfig`) y su engine. */
fun androidPlatformModule(config: NetworkConfig): Module = module {
    single { config }
    single<HttpClientEngine> { OkHttp.create() }
}

package org.example.proyecto_plantilla.core.di

import de.jensklingenberg.ktorfit.Ktorfit
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.data.remote.api.ConfigApi
import org.example.proyecto_plantilla.data.remote.api.createConfigApi
import org.example.proyecto_plantilla.data.repository.ConfigRepositoryImpl
import org.example.proyecto_plantilla.domain.repository.ConfigRepository
import org.example.proyecto_plantilla.domain.usecase.CheckForceUpdateUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

/** APIs (Ktorfit), repositorios y casos de uso. Común a las dos plataformas. */
val dataModule: Module = module {
    single { Ktorfit.Builder().baseUrl(get<NetworkConfig>().baseUrl).httpClient(get<io.ktor.client.HttpClient>()).build() }
    single<ConfigApi> { get<Ktorfit>().createConfigApi() }
    single<ConfigRepository> { ConfigRepositoryImpl(get(), get()) }
    factory { CheckForceUpdateUseCase(get(), get(), get()) }
}

package org.example.proyecto_plantilla.data.repository

import org.example.proyecto_plantilla.core.network.NetworkErrorMapper
import org.example.proyecto_plantilla.core.network.safeApiCall
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.data.remote.api.ConfigApi
import org.example.proyecto_plantilla.data.remote.mapper.toDomain
import org.example.proyecto_plantilla.domain.model.AppConfig
import org.example.proyecto_plantilla.domain.repository.ConfigRepository

class ConfigRepositoryImpl(
    private val api: ConfigApi,
    private val errorMapper: NetworkErrorMapper,
) : ConfigRepository {
    override suspend fun getConfig(): Resource<AppConfig> = safeApiCall(errorMapper) { api.getConfig().toDomain() }
}

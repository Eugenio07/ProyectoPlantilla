package org.example.proyecto_plantilla.domain.repository

import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.domain.model.AppConfig

interface ConfigRepository {
    /** Nunca lanza: un fallo de red o de formato llega como `Resource.Error`. */
    suspend fun getConfig(): Resource<AppConfig>
}

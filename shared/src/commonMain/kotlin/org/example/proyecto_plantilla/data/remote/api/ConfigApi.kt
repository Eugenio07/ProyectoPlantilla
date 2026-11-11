package org.example.proyecto_plantilla.data.remote.api

import de.jensklingenberg.ktorfit.http.GET
import org.example.proyecto_plantilla.data.remote.dto.AppConfigDto

interface ConfigApi {
    /** Ruta relativa al `baseUrl` (que termina en `/`). */
    @GET("v1/config")
    suspend fun getConfig(): AppConfigDto
}

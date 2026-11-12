package org.example.proyecto_plantilla.domain.model

sealed interface ForceUpdateStatus {
    data object UpToDate : ForceUpdateStatus

    /**
     * La app instalada es más vieja que [minVersion]: pantalla bloqueante. [storeUrl]: la ficha de la tienda de ESTA
     * plataforma, ya validada (`#PP24`); `null` → sin botón «Actualizar».
     */
    data class UpdateRequired(
        val minVersion: String,
        val storeUrl: String? = null,
    ) : ForceUpdateStatus
}

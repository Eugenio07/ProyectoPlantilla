package org.example.proyecto_plantilla.domain.model

sealed interface ForceUpdateStatus {
    data object UpToDate : ForceUpdateStatus

    /** La app instalada es más vieja que [minVersion]: pantalla bloqueante. */
    data class UpdateRequired(
        val minVersion: String,
    ) : ForceUpdateStatus
}

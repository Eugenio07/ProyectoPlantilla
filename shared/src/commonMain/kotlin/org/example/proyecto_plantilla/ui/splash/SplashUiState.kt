package org.example.proyecto_plantilla.ui.splash

import org.example.proyecto_plantilla.ui.components.ErrorKind

sealed interface SplashUiState {
    data object Loading : SplashUiState

    data object Ready : SplashUiState

    /** Bloqueante: sin actualizar no se sigue. [storeUrl] → botón «Actualizar» (`#PP24`); `null`, sin botón. */
    data class ForceUpdate(
        val minVersion: String,
        val storeUrl: String? = null,
    ) : SplashUiState

    /** No se pudo traer la config: lo pinta `ErrorState`, con Reintentar si el tipo lo admite. */
    data class Error(
        val kind: ErrorKind,
    ) : SplashUiState
}

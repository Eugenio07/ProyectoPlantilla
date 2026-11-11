package org.example.proyecto_plantilla.ui.splash

import org.example.proyecto_plantilla.ui.components.ErrorKind

sealed interface SplashUiState {
    data object Loading : SplashUiState

    data object Ready : SplashUiState

    /** Bloqueante: no hay forma de seguir sin actualizar. */
    data class ForceUpdate(
        val minVersion: String,
    ) : SplashUiState

    /** No se pudo traer la config: lo pinta `ErrorState`, con Reintentar si el tipo lo admite. */
    data class Error(
        val kind: ErrorKind,
    ) : SplashUiState
}

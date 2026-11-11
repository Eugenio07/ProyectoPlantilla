package org.example.proyecto_plantilla.ui.splash

import org.example.proyecto_plantilla.core.error.AppError

sealed interface SplashUiState {
    data object Loading : SplashUiState

    data object Ready : SplashUiState

    /** Bloqueante: no hay forma de seguir sin actualizar. */
    data class ForceUpdate(val minVersion: String) : SplashUiState

    /** No se pudo traer la config: se ofrece Reintentar. */
    data class Error(val error: AppError) : SplashUiState
}

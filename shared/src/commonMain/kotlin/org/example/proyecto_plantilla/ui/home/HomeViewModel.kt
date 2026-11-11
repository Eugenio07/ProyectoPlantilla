package org.example.proyecto_plantilla.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.proyecto_plantilla.core.platform.AppBuildInfo

/**
 * Placeholder: el id de ejemplo que abre Detail.
 * CRASH-TEST-SCAFFOLD: [showCrashTest] solo en depuración. Retirar: `grep -rn CRASH-TEST-SCAFFOLD` y borrar cada
 * bloque.
 */
data class HomeUiState(
    val sampleDetailId: String,
    val showCrashTest: Boolean,
)

class HomeViewModel(
    buildInfo: AppBuildInfo,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            HomeUiState(sampleDetailId = "42", showCrashTest = buildInfo.isDebug), // CRASH-TEST-SCAFFOLD
        )
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
}

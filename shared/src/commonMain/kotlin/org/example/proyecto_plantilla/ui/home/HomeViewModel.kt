package org.example.proyecto_plantilla.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Placeholder: el id de ejemplo que abre Detail. */
data class HomeUiState(val sampleDetailId: String)

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState(sampleDetailId = "42"))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
}

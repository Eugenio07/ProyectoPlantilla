package org.example.proyecto_plantilla.ui.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DetailUiState(val id: String)

/** Placeholder: recibe el id de la ruta `DetailRoute` (por `parametersOf` desde el NavHost). */
class DetailViewModel(id: String) : ViewModel() {
    private val _state = MutableStateFlow(DetailUiState(id = id))
    val state: StateFlow<DetailUiState> = _state.asStateFlow()
}

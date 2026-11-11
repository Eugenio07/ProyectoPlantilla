package org.example.proyecto_plantilla.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.domain.model.ForceUpdateStatus
import org.example.proyecto_plantilla.domain.usecase.CheckForceUpdateUseCase
import org.example.proyecto_plantilla.ui.components.ErrorKindMapper

class SplashViewModel(
    private val checkForceUpdate: CheckForceUpdateUseCase,
    private val errorKind: ErrorKindMapper,
) : ViewModel() {
    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        _state.value = SplashUiState.Loading
        viewModelScope.launch {
            _state.value =
                when (val result = checkForceUpdate()) {
                    is Resource.Error -> {
                        SplashUiState.Error(errorKind.map(result.error))
                    }

                    is Resource.Success -> {
                        when (val status = result.data) {
                            ForceUpdateStatus.UpToDate -> SplashUiState.Ready
                            is ForceUpdateStatus.UpdateRequired -> SplashUiState.ForceUpdate(status.minVersion)
                        }
                    }
                }
        }
    }
}

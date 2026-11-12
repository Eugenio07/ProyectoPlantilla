package org.example.proyecto_plantilla.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.domain.model.ForceUpdateStatus
import org.example.proyecto_plantilla.domain.usecase.CheckForceUpdateUseCase
import org.example.proyecto_plantilla.ui.components.ErrorKindMapper

class SplashViewModel(
    private val checkForceUpdate: CheckForceUpdateUseCase,
    private val errorKind: ErrorKindMapper,
    private val logger: Logger,
) : ViewModel() {
    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    /**
     * «Actualizar» (`#PP24`): abre la URL de la tienda con [open] (en la UI, `LocalUriHandler`). Si [open] falla (nada
     * que la abra), avisa y NO propaga: un botón que tumba la app es peor que no tenerlo.
     */
    @Suppress("TooGenericExceptionCaught")
    fun openStore(open: (String) -> Unit) {
        val url = (_state.value as? SplashUiState.ForceUpdate)?.storeUrl ?: return
        try {
            open(url)
        } catch (e: Exception) {
            logger.warn(TAG, "$WARN_STORE_OPEN_FAILED: ${e::class.simpleName}")
        }
    }

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
                            ForceUpdateStatus.UpToDate -> {
                                SplashUiState.Ready
                            }

                            is ForceUpdateStatus.UpdateRequired -> {
                                SplashUiState.ForceUpdate(
                                    status.minVersion,
                                    status.storeUrl,
                                )
                            }
                        }
                    }
                }
        }
    }

    companion object {
        // Diagnóstico para los registros: no es texto de producto.
        private const val TAG = "ForceUpdate"
        const val WARN_STORE_OPEN_FAILED = "no se pudo abrir la URL de la tienda"
    }
}

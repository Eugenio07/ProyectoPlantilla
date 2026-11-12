package org.example.proyecto_plantilla.ui.splash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.example.proyecto_plantilla.core.error.AppError
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.platform.AppPlatform
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.domain.model.AppConfig
import org.example.proyecto_plantilla.domain.repository.ConfigRepository
import org.example.proyecto_plantilla.domain.usecase.CheckForceUpdateUseCase
import org.example.proyecto_plantilla.ui.components.ErrorKind
import org.example.proyecto_plantilla.ui.components.ErrorKindMapper
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private var config: Resource<AppConfig> = Resource.Success(AppConfig("1.0.0"))

    private val warnings = mutableListOf<String>()

    private val logger =
        object : Logger {
            override fun info(
                tag: String,
                message: String,
            ) = Unit

            override fun warn(
                tag: String,
                message: String,
            ) {
                warnings += message
            }

            override fun error(
                tag: String,
                message: String,
                throwable: Throwable?,
            ) = Unit
        }

    private val useCase =
        CheckForceUpdateUseCase(
            repository =
                object : ConfigRepository {
                    override suspend fun getConfig() = config
                },
            appVersion =
                object : AppVersionProvider {
                    override fun versionName() = "1.0.0"
                },
            platform = AppPlatform.ANDROID,
            logger = logger,
        )

    private fun vm() = SplashViewModel(useCase, ErrorKindMapper(logger), logger)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun upToDateIsReady() {
        assertEquals(SplashUiState.Ready, vm().state.value)
    }

    @Test
    fun olderVersionIsForceUpdate() {
        config = Resource.Success(AppConfig("2.0.0"))
        assertEquals(SplashUiState.ForceUpdate("2.0.0"), vm().state.value)
    }

    @Test
    fun configThatCannotBeFetchedIsError() {
        config = Resource.Error(AppError.Network)
        assertEquals(SplashUiState.Error(ErrorKind.NETWORK), vm().state.value)
    }

    @Test
    fun retryAfterErrorLoadsAgain() {
        config = Resource.Error(AppError.Network)
        val vm = vm()
        config = Resource.Success(AppConfig("1.0.0"))
        vm.retry()
        assertEquals(SplashUiState.Ready, vm.state.value)
    }

    // #PP24 · el botón «Actualizar».
    private val url = "https://play.google.com/store/apps/details?id=x"

    @Test
    fun forceUpdateCarriesStoreUrl() {
        config = Resource.Success(AppConfig("2.0.0", storeUrlAndroid = url))
        assertEquals(SplashUiState.ForceUpdate("2.0.0", url), vm().state.value)
    }

    @Test
    fun openStoreCallsOpenWithUrl() {
        config = Resource.Success(AppConfig("2.0.0", storeUrlAndroid = url))
        val opened = mutableListOf<String>()
        vm().openStore { opened += it }
        assertEquals(listOf(url), opened)
    }

    @Test
    fun openStoreFailureWarnsAndDoesNotThrow() {
        config = Resource.Success(AppConfig("2.0.0", storeUrlAndroid = url))
        val vm = vm()
        warnings.clear()
        vm.openStore { throw IllegalStateException("nada que la abra") }
        assertEquals(listOf("${SplashViewModel.WARN_STORE_OPEN_FAILED}: IllegalStateException"), warnings)
    }
}

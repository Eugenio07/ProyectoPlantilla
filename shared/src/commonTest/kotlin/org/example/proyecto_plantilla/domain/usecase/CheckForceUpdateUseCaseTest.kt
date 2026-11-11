package org.example.proyecto_plantilla.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.proyecto_plantilla.core.error.AppError
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.domain.model.AppConfig
import org.example.proyecto_plantilla.domain.model.ForceUpdateStatus
import org.example.proyecto_plantilla.domain.repository.ConfigRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckForceUpdateUseCaseTest {
    private class FakeLogger : Logger {
        val warnings = mutableListOf<String>()

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

    private val logger = FakeLogger()

    private fun useCase(
        config: Resource<AppConfig>,
        local: String,
    ) = CheckForceUpdateUseCase(
        repository =
            object : ConfigRepository {
                override suspend fun getConfig() = config
            },
        appVersion =
            object : AppVersionProvider {
                override fun versionName() = local
            },
        logger = logger,
    )

    @Test
    fun configThatCannotBeFetchedIsAnErrorNotUpToDate() =
        runTest {
            assertEquals(Resource.Error(AppError.Network), useCase(Resource.Error(AppError.Network), "1.0.0")())
            assertEquals(emptyList(), logger.warnings)
        }

    @Test
    fun olderLocalVersionRequiresUpdate() =
        runTest {
            assertEquals(
                Resource.Success(ForceUpdateStatus.UpdateRequired("2.0.0")),
                useCase(Resource.Success(AppConfig("2.0.0")), "1.9.9")(),
            )
        }

    @Test
    fun sameVersionIsUpToDateWithoutWarning() =
        runTest {
            assertEquals(
                Resource.Success(ForceUpdateStatus.UpToDate),
                useCase(Resource.Success(AppConfig("1.0.0")), "1.0.0")(),
            )
            assertEquals(emptyList(), logger.warnings)
        }

    @Test
    fun newerLocalVersionIsUpToDate() =
        runTest {
            assertEquals(
                Resource.Success(ForceUpdateStatus.UpToDate),
                useCase(Resource.Success(AppConfig("1.0.0")), "1.2.0")(),
            )
        }

    @Test
    fun absentMinVersionFailsOpenWithItsOwnWarning() =
        runTest {
            assertEquals(
                Resource.Success(ForceUpdateStatus.UpToDate),
                useCase(Resource.Success(AppConfig(null)), "1.0.0")(),
            )
            assertEquals(listOf(CheckForceUpdateUseCase.WARN_MIN_VERSION_ABSENT), logger.warnings)
        }

    @Test
    fun malformedMinVersionFailsOpenWithItsOwnWarning() =
        runTest {
            assertEquals(
                Resource.Success(ForceUpdateStatus.UpToDate),
                useCase(Resource.Success(AppConfig("abc")), "1.0.0")(),
            )
            assertEquals(listOf(CheckForceUpdateUseCase.WARN_MIN_VERSION_INVALID), logger.warnings)
        }

    @Test
    fun invalidLocalVersionFailsOpenWithItsOwnWarning() =
        runTest {
            assertEquals(
                Resource.Success(ForceUpdateStatus.UpToDate),
                useCase(Resource.Success(AppConfig("9.0.0")), "")(),
            )
            assertEquals(listOf(CheckForceUpdateUseCase.WARN_LOCAL_VERSION_INVALID), logger.warnings)
        }
}

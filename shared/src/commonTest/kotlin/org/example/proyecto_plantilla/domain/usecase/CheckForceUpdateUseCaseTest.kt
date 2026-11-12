package org.example.proyecto_plantilla.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.proyecto_plantilla.core.error.AppError
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.platform.AppPlatform
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
        platform: AppPlatform = AppPlatform.ANDROID,
    ) = CheckForceUpdateUseCase(
        repository =
            object : ConfigRepository {
                override suspend fun getConfig() = config
            },
        appVersion =
            object : AppVersionProvider {
                override fun versionName() = local
            },
        platform = platform,
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

    // #PP24 · la URL de la tienda: solo la válida de ESTA plataforma; si no hay, null y un aviso con su motivo.
    private val android = "https://play.google.com/store/apps/details?id=x"
    private val ios = "https://apps.apple.com/app/id1"

    private suspend fun urlFor(
        platform: AppPlatform,
        androidUrl: String? = android,
        iosUrl: String? = ios,
    ): String? {
        val config = Resource.Success(AppConfig("2.0.0", storeUrlAndroid = androidUrl, storeUrlIos = iosUrl))
        val result = useCase(config, "1.0.0", platform)()
        return ((result as Resource.Success).data as ForceUpdateStatus.UpdateRequired).storeUrl
    }

    @Test
    fun androidGetsItsUrl() = runTest { assertEquals(android, urlFor(AppPlatform.ANDROID)) }

    @Test
    fun iosGetsItsUrl() = runTest { assertEquals(ios, urlFor(AppPlatform.IOS)) }

    @Test
    fun otherPlatformOnlyIsAbsentWithWarning() =
        runTest {
            assertEquals(null, urlFor(AppPlatform.IOS, iosUrl = null))
            assertEquals(listOf(CheckForceUpdateUseCase.WARN_STORE_URL_ABSENT), logger.warnings)
        }

    private suspend fun assertInvalid(url: String) {
        assertEquals(null, urlFor(AppPlatform.ANDROID, androidUrl = url), url)
        assertEquals(listOf(CheckForceUpdateUseCase.WARN_STORE_URL_INVALID), logger.warnings, url)
        logger.warnings.clear()
    }

    @Test
    fun httpIsInvalid() = runTest { assertInvalid("http://play.google.com/store/apps/details?id=x") }

    @Test
    fun javascriptIsInvalid() = runTest { assertInvalid("javascript:alert(1)") }

    @Test
    fun emptyIsInvalid() = runTest { assertInvalid("") }

    @Test
    fun spacesAreInvalid() = runTest { assertInvalid("https://play.google.com/store apps") }

    @Test
    fun noHostIsInvalid() =
        runTest {
            assertInvalid("https://")
            assertInvalid("https:///store")
        }

    @Test
    fun upToDateDoesNotWarnAboutUrl() =
        runTest {
            val config = Resource.Success(AppConfig("0.1.0", storeUrlAndroid = null, storeUrlIos = null))
            assertEquals(Resource.Success(ForceUpdateStatus.UpToDate), useCase(config, "1.0.0")())
            assertEquals(emptyList(), logger.warnings)
        }
}

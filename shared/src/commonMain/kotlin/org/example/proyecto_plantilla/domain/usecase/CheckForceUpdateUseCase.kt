package org.example.proyecto_plantilla.domain.usecase

import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.platform.AppPlatform
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.core.version.SemanticVersionComparator
import org.example.proyecto_plantilla.domain.model.AppConfig
import org.example.proyecto_plantilla.domain.model.ForceUpdateStatus
import org.example.proyecto_plantilla.domain.repository.ConfigRepository

/**
 * ¿Hay que bloquear la app hasta que se actualice? (`docs/contracts/config-endpoint.md`)
 *
 * Distingue dos cosas que no son la misma:
 * - **No pude traer la config** → `Resource.Error`: la Splash ofrece Reintentar. No es «al día».
 * - **Llegó, pero con `minVersion` ausente o malformado**, o la versión local es inválida → FAIL-OPEN:
 *   `Success(UpToDate)` y un aviso que dice **cuál** de las tres ramas fue (cada una con su test).
 */
class CheckForceUpdateUseCase(
    private val repository: ConfigRepository,
    private val appVersion: AppVersionProvider,
    private val platform: AppPlatform,
    private val logger: Logger,
) {
    suspend operator fun invoke(): Resource<ForceUpdateStatus> =
        when (val config = repository.getConfig()) {
            is Resource.Error -> config
            is Resource.Success -> Resource.Success(decide(config.data, appVersion.versionName()))
        }

    private fun decide(
        config: AppConfig,
        local: String,
    ): ForceUpdateStatus {
        val minVersion = config.minVersion
        val v = SemanticVersionComparator
        return when {
            !v.isValid(local) -> failOpen(WARN_LOCAL_VERSION_INVALID)
            minVersion == null -> failOpen(WARN_MIN_VERSION_ABSENT)
            !v.isValid(minVersion) -> failOpen(WARN_MIN_VERSION_INVALID)
            v.compare(local, minVersion) < 0 -> ForceUpdateStatus.UpdateRequired(minVersion.trim(), storeUrl(config))
            else -> ForceUpdateStatus.UpToDate
        }
    }

    /**
     * La URL de la tienda de ESTA plataforma, si es válida (`#PP24`): `https://`, sin espacios y con host. Si no,
     * `null` (la pantalla sale sin botón) y un aviso que dice si faltaba o era inválida. Nunca decide si se bloquea.
     */
    private fun storeUrl(config: AppConfig): String? {
        val url =
            when (platform) {
                AppPlatform.ANDROID -> config.storeUrlAndroid
                AppPlatform.IOS -> config.storeUrlIos
            }
        return when {
            url == null -> warn(WARN_STORE_URL_ABSENT)
            !url.startsWith(HTTPS) -> warn(WARN_STORE_URL_INVALID)
            url.any(Char::isWhitespace) -> warn(WARN_STORE_URL_INVALID)
            url.removePrefix(HTTPS).substringBefore('/').isEmpty() -> warn(WARN_STORE_URL_INVALID)
            else -> url
        }
    }

    private fun warn(warning: String): String? {
        logger.warn(TAG, warning)
        return null
    }

    private fun failOpen(warning: String): ForceUpdateStatus {
        logger.warn(TAG, warning)
        return ForceUpdateStatus.UpToDate
    }

    companion object {
        private const val TAG = "ForceUpdate"
        private const val HTTPS = "https://"

        // Diagnóstico para los registros: no es texto de producto.
        const val WARN_MIN_VERSION_ABSENT = "minVersion ausente en la config: fail-open"
        const val WARN_MIN_VERSION_INVALID = "minVersion malformado en la config: fail-open"
        const val WARN_LOCAL_VERSION_INVALID = "versión local inválida: fail-open"
        const val WARN_STORE_URL_ABSENT = "sin URL de tienda para esta plataforma: actualización sin botón"
        const val WARN_STORE_URL_INVALID = "URL de tienda inválida para esta plataforma: actualización sin botón"
    }
}

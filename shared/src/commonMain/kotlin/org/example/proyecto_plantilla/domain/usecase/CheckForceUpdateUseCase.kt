package org.example.proyecto_plantilla.domain.usecase

import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.platform.AppVersionProvider
import org.example.proyecto_plantilla.core.result.Resource
import org.example.proyecto_plantilla.core.version.SemanticVersionComparator
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
    private val logger: Logger,
) {
    suspend operator fun invoke(): Resource<ForceUpdateStatus> =
        when (val config = repository.getConfig()) {
            is Resource.Error -> config
            is Resource.Success -> Resource.Success(decide(config.data.minVersion, appVersion.versionName()))
        }

    private fun decide(minVersion: String?, local: String): ForceUpdateStatus {
        val v = SemanticVersionComparator
        return when {
            !v.isValid(local) -> failOpen(WARN_LOCAL_VERSION_INVALID)
            minVersion == null -> failOpen(WARN_MIN_VERSION_ABSENT)
            !v.isValid(minVersion) -> failOpen(WARN_MIN_VERSION_INVALID)
            v.compare(local, minVersion) < 0 -> ForceUpdateStatus.UpdateRequired(minVersion.trim())
            else -> ForceUpdateStatus.UpToDate
        }
    }

    private fun failOpen(warning: String): ForceUpdateStatus {
        logger.warn(TAG, warning)
        return ForceUpdateStatus.UpToDate
    }

    companion object {
        private const val TAG = "ForceUpdate"

        // Diagnóstico para los registros: no es texto de producto.
        const val WARN_MIN_VERSION_ABSENT = "minVersion ausente en la config: fail-open"
        const val WARN_MIN_VERSION_INVALID = "minVersion malformado en la config: fail-open"
        const val WARN_LOCAL_VERSION_INVALID = "versión local inválida: fail-open"
    }
}

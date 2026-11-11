package org.example.proyecto_plantilla.core.crash

import org.example.proyecto_plantilla.core.log.Logger
import kotlin.experimental.ExperimentalNativeApi

/**
 * iOS: SOLO registra; no hay recuperación. `setUnhandledExceptionHook` y no `NSSetUncaughtExceptionHandler`, que exige
 * `staticCFunction` y no captura closures.
 */
class IosGlobalCrashHandler(private val logger: Logger) : GlobalCrashHandler {
    private var installed = false

    @OptIn(ExperimentalNativeApi::class)
    override fun install() {
        if (installed) return
        installed = true
        setUnhandledExceptionHook { throwable ->
            logger.error(TAG, "crash no gestionado (iOS: solo se registra)", throwable)
        }
    }

    companion object {
        const val TAG = "CrashHandler"
    }
}

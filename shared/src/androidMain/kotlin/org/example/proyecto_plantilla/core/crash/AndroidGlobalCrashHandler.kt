package org.example.proyecto_plantilla.core.crash

import android.content.Context
import android.content.Intent
import org.example.proyecto_plantilla.core.log.Logger
import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import kotlin.system.exitProcess

class AndroidGlobalCrashHandler(
    private val context: Context,
    private val buildInfo: AppBuildInfo,
    private val logger: Logger,
    private val breaker: CrashCircuitBreaker,
    private val reporter: CrashReporter,
) : GlobalCrashHandler {
    private var installed = false

    override fun install() {
        if (installed) return
        installed = true
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (buildInfo.isDebug) {
                // Depuración: el crash se ve, con su diálogo y su traza.
                previous?.uncaughtException(thread, throwable)
                return@setDefaultUncaughtExceptionHandler
            }
            try {
                logger.error(TAG, "crash no gestionado; se relanza en Home", throwable)
                reporter.recordException(throwable)
                breaker.recordCrash() // síncrono: antes de morir
                if (reporter.isActive) {
                    // recordException escribe en un worker de fondo sin flush público: espera ACOTADA antes de morir.
                    Thread.sleep(REPORT_FLUSH_WAIT_MS)
                }
                relaunchAtHome()
            } finally {
                exitProcess(0)
            }
        }
    }

    private fun relaunchAtHome() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(EXTRA_START_AT_HOME, true)
        context.startActivity(intent)
    }

    companion object {
        const val TAG = "CrashHandler"
        const val EXTRA_START_AT_HOME = "startAtHome"

        /** Espera entre `recordException` y `exitProcess(0)`: el informe se escribe en segundo plano. */
        const val REPORT_FLUSH_WAIT_MS = 300L
    }
}

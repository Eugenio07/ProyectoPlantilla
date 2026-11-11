package org.example.proyecto_plantilla.core.crash

/**
 * Informe de crashes a un servicio (Crashlytics). Android: GitLive. iOS: lo implementa Swift con el SDK nativo y lo
 * pasa a `startKoinIos` (contrato de callback: v2, trampa 12).
 *
 * TRADE-OFF de recover-to-home (Android, release): el handler relanza la app, así que TODO crash recuperado llega a
 * Crashlytics como NO FATAL. «Crashes: 0» en el panel NO significa salud: hay que mirar los no fatales.
 */
interface CrashReporter {
    /** `false` sin Firebase inicializado (la plantilla no tiene proyecto): no se informa y no se espera. */
    val isActive: Boolean

    fun recordException(throwable: Throwable)
}

/** Sin servicio de informes. */
object NoOpCrashReporter : CrashReporter {
    override val isActive: Boolean = false

    override fun recordException(throwable: Throwable) = Unit
}

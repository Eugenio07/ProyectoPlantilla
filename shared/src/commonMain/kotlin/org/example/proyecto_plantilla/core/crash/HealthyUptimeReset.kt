package org.example.proyecto_plantilla.core.crash

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Android: si el proceso sigue vivo `healthyUptimeMs`, la racha se borra. Una vez por proceso. En iOS el único reset es
 * el botón de la pantalla segura (la reapertura es manual y no hay «vida sana» que medir antes de que la persona vuelva).
 */
class HealthyUptimeReset(
    private val breaker: CrashCircuitBreaker,
    private val config: CrashBreakerConfig,
    private val scope: CoroutineScope,
) {
    private var started = false

    fun start() {
        if (started) return
        started = true
        scope.launch {
            delay(config.healthyUptimeMs)
            breaker.reset()
        }
    }
}

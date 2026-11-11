package org.example.proyecto_plantilla.core.crash

/** Ventanas del breaker. Una vida sana más corta que la ventana borraría la racha antes de que pudiera contarse. */
data class CrashBreakerConfig(val threshold: Int, val windowMs: Long, val healthyUptimeMs: Long) {
    init {
        require(healthyUptimeMs > windowMs) { "healthyUptimeMs ($healthyUptimeMs) debe ser mayor que windowMs ($windowMs)" }
    }
}

/** Persistencia del contador. SÍNCRONA: el proceso muere justo después de escribir (`exitProcess(0)`). */
interface CrashCounterStore {
    val count: Int

    /** Hora (ms) del primer crash de la racha; 0 si no hay racha. */
    val firstCrashAtMs: Long

    fun save(count: Int, firstCrashAtMs: Long)

    fun clear()
}

class CrashCircuitBreaker(
    private val store: CrashCounterStore,
    private val config: CrashBreakerConfig,
    private val now: () -> Long,
) {
    /** Cuenta un crash. Ventana DESLIZANTE anclada al primer crash de la racha: fuera de ella, empieza una racha nueva. */
    fun recordCrash() {
        val t = now()
        val first = store.firstCrashAtMs
        if (store.count == 0 || t - first > config.windowMs) {
            store.save(count = 1, firstCrashAtMs = t)
        } else {
            store.save(count = store.count + 1, firstCrashAtMs = first)
        }
    }

    /** `threshold` crashes dentro de la ventana del primero → la app muestra la pantalla segura. */
    fun isTripped(): Boolean = store.count >= config.threshold && now() - store.firstCrashAtMs <= config.windowMs

    fun reset() = store.clear()
}

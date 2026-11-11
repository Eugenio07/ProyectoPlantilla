package org.example.proyecto_plantilla.core.crash

/** Ventanas del breaker. Una vida sana más corta que la ventana borraría la racha antes de que pudiera contarse. */
data class CrashBreakerConfig(
    val threshold: Int,
    /**
     * Ventana de la racha, anclada al primer crash; `null` = SIN ventana: la racha son los crashes seguidos sin
     * vida sana entre medias (Android, donde `HealthyUptimeReset` la borra). Con ventana, un bucle lento (un crash
     * a los 5 s de cada arranque) nunca junta los crashes dentro de ella (#PP10).
     */
    val windowMs: Long?,
    val healthyUptimeMs: Long,
) {
    init {
        require(windowMs == null || healthyUptimeMs > windowMs) {
            "healthyUptimeMs ($healthyUptimeMs) debe ser mayor que windowMs ($windowMs)"
        }
    }
}

/** Persistencia del contador. SÍNCRONA: el proceso muere justo después de escribir (`exitProcess(0)`). */
interface CrashCounterStore {
    val count: Int

    /** Hora (ms) del primer crash de la racha; 0 si no hay racha. */
    val firstCrashAtMs: Long

    fun save(
        count: Int,
        firstCrashAtMs: Long,
    )

    fun clear()
}

class CrashCircuitBreaker(
    private val store: CrashCounterStore,
    private val config: CrashBreakerConfig,
    private val now: () -> Long,
) {
    /**
     * Cuenta un crash. Con ventana (iOS): la racha se ancla al primer crash y uno fuera de ella empieza otra. Sin
     * ventana (Android): la racha solo la borra un reset (la vida sana). Una vez disparado, ningún crash empieza
     * racha nueva.
     */
    fun recordCrash() {
        val t = now()
        val first = store.firstCrashAtMs
        val window = config.windowMs
        val nuevaRacha = store.count < config.threshold && (store.count == 0 || (window != null && t - first > window))
        if (nuevaRacha) {
            store.save(count = 1, firstCrashAtMs = t)
        } else {
            store.save(count = store.count + 1, firstCrashAtMs = first)
        }
    }

    /** `threshold` crashes en racha → pantalla segura, que se MANTIENE hasta un reset (#PP10). */
    fun isTripped(): Boolean = store.count >= config.threshold

    fun reset() = store.clear()
}

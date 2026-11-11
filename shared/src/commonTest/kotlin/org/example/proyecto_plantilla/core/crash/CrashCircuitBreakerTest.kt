package org.example.proyecto_plantilla.core.crash

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CrashCircuitBreakerTest {
    private class MemoryStore : CrashCounterStore {
        override var count = 0
        override var firstCrashAtMs = 0L

        override fun save(
            count: Int,
            firstCrashAtMs: Long,
        ) {
            this.count = count
            this.firstCrashAtMs = firstCrashAtMs
        }

        override fun clear() = save(0, 0L)
    }

    private val config = CrashBreakerConfig(threshold = 3, windowMs = 10_000, healthyUptimeMs = 15_000)
    private val store = MemoryStore()
    private var clock = 1_000_000L
    private val breaker = CrashCircuitBreaker(store, config) { clock }

    private fun crashAt(t: Long) {
        clock = t
        breaker.recordCrash()
    }

    @Test
    fun threeCrashesInsideTheWindowTrip() {
        crashAt(1_000_000)
        crashAt(1_003_000)
        crashAt(1_006_000)
        assertTrue(breaker.isTripped())
    }

    @Test
    fun twoCrashesDoNotTrip() {
        crashAt(1_000_000)
        crashAt(1_003_000)
        assertFalse(breaker.isTripped())
    }

    @Test
    fun windowIsAnchoredToTheFirstCrashOfTheStreak() {
        // 0 s, 6 s, 12 s: cada uno a menos de 10 s del anterior, pero el tercero cae fuera de la ventana del primero.
        crashAt(1_000_000)
        crashAt(1_006_000)
        crashAt(1_012_000)
        assertFalse(breaker.isTripped())
        assertEquals(1, store.count, "el crash fuera de la ventana abre una racha nueva")
    }

    @Test
    fun trippedStaysUntilReset() {
        crashAt(1_000_000)
        crashAt(1_001_000)
        crashAt(1_002_000)
        clock = 1_000_000 + 3_600_000 // una hora después: sigue disparado hasta un reset (#PP10)
        assertTrue(breaker.isTripped())
    }

    @Test
    fun aCrashAfterTrippingDoesNotStartANewStreak() {
        crashAt(1_000_000)
        crashAt(1_001_000)
        crashAt(1_002_000)
        crashAt(1_100_000) // fuera de la ventana, pero ya estaba disparado
        assertTrue(breaker.isTripped())
    }

    @Test
    fun withoutWindowASlowLoopTrips() {
        // Android (#PP10): un crash a los ~5 s de cada arranque. Con ventana de 10 s nunca se juntarían 3.
        val sinVentana =
            CrashCircuitBreaker(
                store,
                CrashBreakerConfig(threshold = 3, windowMs = null, healthyUptimeMs = 15_000),
            ) { clock }
        clock = 1_000_000
        sinVentana.recordCrash()
        clock = 1_006_000
        sinVentana.recordCrash()
        clock = 1_012_000
        sinVentana.recordCrash()
        assertTrue(sinVentana.isTripped())
    }

    @Test
    fun withoutWindowTwoCrashesDoNotTrip() {
        val sinVentana =
            CrashCircuitBreaker(
                store,
                CrashBreakerConfig(threshold = 3, windowMs = null, healthyUptimeMs = 15_000),
            ) { clock }
        clock = 1_000_000
        sinVentana.recordCrash()
        clock = 1_600_000
        sinVentana.recordCrash()
        assertFalse(sinVentana.isTripped())
    }

    @Test
    fun resetClearsTheStreak() {
        crashAt(1_000_000)
        crashAt(1_001_000)
        crashAt(1_002_000)
        breaker.reset()
        assertFalse(breaker.isTripped())
        assertEquals(0, store.count)
    }

    @Test
    fun configRejectsHealthyUptimeNotLongerThanWindow() {
        assertFailsWith<IllegalArgumentException> { CrashBreakerConfig(3, windowMs = 10_000, healthyUptimeMs = 10_000) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun healthyUptimeResetsOncePerProcess() =
        runTest {
            crashAt(1_000_000)
            crashAt(1_001_000)
            val reset = HealthyUptimeReset(breaker, config, this)
            reset.start()
            advanceTimeBy(14_999)
            runCurrent()
            assertEquals(2, store.count, "antes de la vida sana, la racha sigue")
            advanceTimeBy(2)
            runCurrent()
            assertEquals(0, store.count, "tras la vida sana, la racha se borra")
            // Segunda llamada DESPUÉS del primer reset: si programara otro, borraría esta racha nueva.
            crashAt(1_100_000)
            reset.start()
            advanceTimeBy(60_000)
            runCurrent()
            assertEquals(1, store.count, "una vez por proceso: no hay un segundo reset")
        }
}

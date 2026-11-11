package org.example.proyecto_plantilla.core.crash

import platform.Foundation.NSUserDefaults

/** `synchronize()` tras cada escritura: el proceso termina justo después del hook de crash. */
class UserDefaultsCrashCounterStore : CrashCounterStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override val count: Int get() = defaults.integerForKey(KEY_COUNT).toInt()

    override val firstCrashAtMs: Long get() = defaults.doubleForKey(KEY_FIRST).toLong()

    override fun save(
        count: Int,
        firstCrashAtMs: Long,
    ) {
        defaults.setInteger(count.toLong(), KEY_COUNT)
        defaults.setDouble(firstCrashAtMs.toDouble(), KEY_FIRST)
        defaults.synchronize()
    }

    override fun clear() {
        defaults.removeObjectForKey(KEY_COUNT)
        defaults.removeObjectForKey(KEY_FIRST)
        defaults.synchronize()
    }

    private companion object {
        const val KEY_COUNT = "crash_breaker.count"
        const val KEY_FIRST = "crash_breaker.firstCrashAtMs"
    }
}

package org.example.proyecto_plantilla.core.crash

import android.content.Context

/** `commit()` SÍNCRONO: `apply()` y DataStore son asíncronos, y el `exitProcess(0)` del handler mataría la escritura. */
class SharedPrefsCrashCounterStore(context: Context) : CrashCounterStore {
    private val prefs = context.getSharedPreferences("crash_breaker", Context.MODE_PRIVATE)

    override val count: Int get() = prefs.getInt(KEY_COUNT, 0)

    override val firstCrashAtMs: Long get() = prefs.getLong(KEY_FIRST, 0L)

    override fun save(count: Int, firstCrashAtMs: Long) {
        prefs.edit().putInt(KEY_COUNT, count).putLong(KEY_FIRST, firstCrashAtMs).commit()
    }

    override fun clear() {
        prefs.edit().clear().commit()
    }

    private companion object {
        const val KEY_COUNT = "count"
        const val KEY_FIRST = "firstCrashAtMs"
    }
}

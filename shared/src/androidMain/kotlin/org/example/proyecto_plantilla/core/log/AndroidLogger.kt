package org.example.proyecto_plantilla.core.log

import android.util.Log

class AndroidLogger : Logger {
    override fun info(
        tag: String,
        message: String,
    ) {
        Log.i(tag, message)
    }

    override fun warn(
        tag: String,
        message: String,
    ) {
        Log.w(tag, message)
    }

    override fun error(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        Log.e(tag, message, throwable)
    }
}

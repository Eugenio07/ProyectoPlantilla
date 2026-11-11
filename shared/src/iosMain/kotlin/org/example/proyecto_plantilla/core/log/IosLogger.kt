package org.example.proyecto_plantilla.core.log

import platform.Foundation.NSLog

class IosLogger : Logger {
    override fun info(tag: String, message: String) = NSLog("I/%@: %@", tag, message)

    override fun warn(tag: String, message: String) = NSLog("W/%@: %@", tag, message)

    override fun error(tag: String, message: String, throwable: Throwable?) =
        NSLog("E/%@: %@ %@", tag, message, throwable?.toString() ?: "")
}

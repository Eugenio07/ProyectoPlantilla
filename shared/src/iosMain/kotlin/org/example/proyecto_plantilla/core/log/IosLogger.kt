package org.example.proyecto_plantilla.core.log

/**
 * Registro en iOS por la salida estándar (la consola de Xcode y `simctl launch --console`).
 *
 * NO `NSLog("…%@…", tag, message)`: un `String` de Kotlin como argumento variádico de Objective-C da `EXC_BAD_ACCESS`
 * (medido el 2026-11-11; lo fija `IosLoggerTest`). Y tampoco `NSLog(message)`: un `%` dentro del mensaje se leería como
 * formato. El mensaje se compone en Kotlin y sale entero.
 */
class IosLogger : Logger {
    override fun info(tag: String, message: String) = println("I/$tag: $message")

    override fun warn(tag: String, message: String) = println("W/$tag: $message")

    override fun error(tag: String, message: String, throwable: Throwable?) =
        println("E/$tag: $message" + (throwable?.let { " — $it" } ?: ""))
}

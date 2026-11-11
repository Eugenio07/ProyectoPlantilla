package org.example.proyecto_plantilla.core.log

import kotlin.test.Test

/**
 * Reproduce el crash medido el 2026-11-11: `NSLog("W/%@: %@", tag, message)` con `String` de Kotlin como argumento
 * variádico daba `EXC_BAD_ACCESS` en el simulador (escenarios de fail-open del stub). Si vuelve, el proceso de test
 * muere y la tarea sale roja.
 */
class IosLoggerTest {
    @Test
    fun everyLevelLogsWithoutCrashingIncludingPercentSigns() {
        val logger = IosLogger()
        logger.info("Tag", "info %@ %d 100%")
        logger.warn("Tag", "warn %@ %d 100%")
        logger.error("Tag", "error %@ %d 100%", IllegalStateException("x"))
        logger.error("Tag", "error sin throwable", null)
    }
}

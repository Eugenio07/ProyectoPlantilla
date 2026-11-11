package org.example.proyecto_plantilla.ui.components

import org.example.proyecto_plantilla.core.error.AppError
import org.example.proyecto_plantilla.core.log.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ErrorKindMapperTest {
    private class FakeLogger : Logger {
        val infos = mutableListOf<String>()
        override fun info(tag: String, message: String) {
            infos += message
        }
        override fun warn(tag: String, message: String) = Unit
        override fun error(tag: String, message: String, throwable: Throwable?) = Unit
    }

    private val logger = FakeLogger()
    private val mapper = ErrorKindMapper(logger)

    @Test
    fun networkFailureIsNetwork() = assertEquals(ErrorKind.NETWORK, mapper.map(AppError.Network))

    @Test
    fun eachKnownServerCodeHasItsKind() {
        assertEquals(ErrorKind.MAINTENANCE, mapper.map(AppError.Server("MAINTENANCE", "m")))
        assertEquals(ErrorKind.RATE_LIMITED, mapper.map(AppError.Server("RATE_LIMITED", "m")))
        assertEquals(ErrorKind.UNAUTHORIZED, mapper.map(AppError.Server("UNAUTHORIZED", "m")))
        assertEquals(ErrorKind.VALIDATION, mapper.map(AppError.Server("VALIDATION_ERROR", "m")))
        assertEquals(emptyList(), logger.infos)
    }

    @Test
    fun unknownCodeIsGenericAndRecordsTheCodeNeverTheMessage() {
        assertEquals(ErrorKind.GENERIC, mapper.map(AppError.Server("BRAND_NEW", "secreto del servidor")))
        assertEquals(1, logger.infos.size)
        assertTrue("BRAND_NEW" in logger.infos.single())
        assertFalse("secreto" in logger.infos.single())
    }

    @Test
    fun networkCodeSentByTheServerIsUnknown() {
        assertEquals(ErrorKind.GENERIC, mapper.map(AppError.Server("NETWORK", "m")))
        assertEquals(1, logger.infos.size)
    }

    @Test
    fun errorsWithoutCodeAreGenericWithoutTelemetry() {
        assertEquals(ErrorKind.GENERIC, mapper.map(AppError.Http(500)))
        assertEquals(ErrorKind.GENERIC, mapper.map(AppError.Serialization))
        assertEquals(ErrorKind.GENERIC, mapper.map(AppError.Unknown))
        assertEquals(emptyList(), logger.infos)
    }

    @Test
    fun onlyRejectionsThatRetryCannotChangeHideRetry() {
        assertFalse(ErrorKind.UNAUTHORIZED.canRetry)
        assertFalse(ErrorKind.VALIDATION.canRetry)
        assertTrue(ErrorKind.entries.filter { it != ErrorKind.UNAUTHORIZED && it != ErrorKind.VALIDATION }.all { it.canRetry })
    }
}

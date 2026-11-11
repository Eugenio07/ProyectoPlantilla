package org.example.proyecto_plantilla.data.remote.mapper

import org.example.proyecto_plantilla.core.network.AppJson
import org.example.proyecto_plantilla.data.remote.dto.AppConfigDto
import kotlin.test.Test
import kotlin.test.assertEquals

/** Cada forma de `minVersion` que describe `docs/contracts/config-endpoint.md`. Corre en JVM y en el simulador. */
class AppConfigMapperTest {
    private fun minVersionOf(json: String): String? =
        AppJson.decodeFromString(AppConfigDto.serializer(), json).toDomain().minVersion

    @Test
    fun stringIsKeptAsIs() = assertEquals("1.4.0", minVersionOf("""{"minVersion":"1.4.0","extra":1}"""))

    @Test
    fun absentKeyIsNull() = assertEquals(null, minVersionOf("{}"))

    @Test
    fun jsonNullIsNull() = assertEquals(null, minVersionOf("""{"minVersion":null}"""))

    @Test
    fun numberKeepsItsTextSoValidationRejectsIt() = assertEquals("1.4", minVersionOf("""{"minVersion":1.4}"""))

    @Test
    fun objectKeepsItsJsonSoValidationRejectsIt() =
        assertEquals("""{"a":1}""", minVersionOf("""{"minVersion":{"a":1}}"""))

    @Test
    fun arrayKeepsItsJsonSoValidationRejectsIt() = assertEquals("[1,4,0]", minVersionOf("""{"minVersion":[1,4,0]}"""))
}

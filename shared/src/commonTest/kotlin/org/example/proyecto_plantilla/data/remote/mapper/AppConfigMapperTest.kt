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

    // #PP24 · storeUrl: las dos URLs crudas; la validez la decide el caso de uso.
    private fun storeUrlsOf(json: String): Pair<String?, String?> =
        AppJson
            .decodeFromString(
                AppConfigDto.serializer(),
                json,
            ).toDomain()
            .let { it.storeUrlAndroid to it.storeUrlIos }

    @Test
    fun bothStoreUrlsAreMapped() =
        assertEquals(
            "https://a.example/app" to "https://i.example/app",
            storeUrlsOf("""{"storeUrl":{"android":"https://a.example/app","ios":"https://i.example/app"}}"""),
        )

    @Test
    fun onlyOnePlatformUrl() =
        assertEquals(null to "https://i.example/app", storeUrlsOf("""{"storeUrl":{"ios":"https://i.example/app"}}"""))

    @Test
    fun absentStoreUrlIsNull() = assertEquals(null to null, storeUrlsOf("""{"minVersion":"1.0.0"}"""))

    @Test
    fun nonStringUrlKeepsItsJson() =
        assertEquals("42" to """{"x":1}""", storeUrlsOf("""{"storeUrl":{"android":42,"ios":{"x":1}}}"""))

    @Test
    fun storeUrlNotAnObjectGoesToBothAsJson() =
        assertEquals(
            "\"https://x.example\"" to "\"https://x.example\"",
            storeUrlsOf("""{"storeUrl":"https://x.example"}"""),
        )
}

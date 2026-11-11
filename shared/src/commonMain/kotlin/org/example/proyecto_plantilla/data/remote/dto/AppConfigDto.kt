package org.example.proyecto_plantilla.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * `GET v1/config` (`docs/contracts/config-endpoint.md`). `minVersion` es `JsonElement` y no `String?`: un número u otro
 * tipo es «malformado» (fail-open) según el contrato, y con `String?` haría fallar la deserialización entera.
 */
@Serializable
data class AppConfigDto(
    @SerialName("minVersion") val minVersion: JsonElement? = null,
)

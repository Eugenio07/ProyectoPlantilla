package org.example.proyecto_plantilla.data.remote.mapper

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.example.proyecto_plantilla.data.remote.dto.AppConfigDto
import org.example.proyecto_plantilla.domain.model.AppConfig

/**
 * `minVersion` sin validar: string → su contenido; número → su texto; objeto o array → su JSON (los tres los rechaza
 * `isValid` después: fail-open); ausente o `null` → `null`. (Un `null` JSON llega como `null` de Kotlin, no como
 * `JsonNull`: medido con una mutación, 2026-11-11.)
 */
fun AppConfigDto.toDomain(): AppConfig {
    // #PP24: `storeUrl` objeto → cada plataforma como `minVersion`; otro tipo → su JSON a las dos (el caso de uso lo
    // rechaza como inválido, no como ausente).
    // Una cadena suelta va con sus comillas (`toString`, no `content`): si no, pasaría por una URL válida.
    val urls = storeUrl
    val noObject = if (urls == null || urls is JsonNull) null else urls.toString()
    return AppConfig(
        minVersion = minVersion.raw(),
        storeUrlAndroid = if (urls is JsonObject) urls["android"].raw() else noObject,
        storeUrlIos = if (urls is JsonObject) urls["ios"].raw() else noObject,
    )
}

/** Sin validar: cadena → su contenido; otro primitivo → su texto; objeto o array → su JSON; ausente → `null`. */
private fun JsonElement?.raw(): String? =
    when (this) {
        null, is JsonNull -> null
        is JsonPrimitive -> content
        else -> toString()
    }

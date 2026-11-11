package org.example.proyecto_plantilla.data.remote.mapper

import kotlinx.serialization.json.JsonPrimitive
import org.example.proyecto_plantilla.data.remote.dto.AppConfigDto
import org.example.proyecto_plantilla.domain.model.AppConfig

/**
 * `minVersion` sin validar: string → su contenido; número → su texto; objeto o array → su JSON (los tres los rechaza
 * `isValid` después: fail-open); ausente o `null` → `null`. (Un `null` JSON llega como `null` de Kotlin, no como
 * `JsonNull`: medido con una mutación, 2026-11-11.)
 */
fun AppConfigDto.toDomain(): AppConfig =
    AppConfig(
        minVersion =
            when (val v = minVersion) {
                null -> null
                is JsonPrimitive -> v.content
                else -> v.toString()
            },
    )

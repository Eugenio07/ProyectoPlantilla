package org.example.proyecto_plantilla.core.network

/**
 * Escenarios del stub de config (v2 Fase 4; tabla en `docs/contracts/config-endpoint.md`). **La única fuente de los JSON
 * del stub**: el `MockEngine` de Android (variante debug) y el de iOS responden desde aquí.
 *
 * El escenario se lee en cada petición ([selected]), así que se elige al arrancar sin rehacer el grafo: en Android con el
 * extra `mockScenario` del intent (solo en depuración); en iOS con el argumento de lanzamiento `-mockScenario` (solo en
 * `#if DEBUG`).
 */
enum class ConfigMockScenario(val status: Int, val body: String) {
    UP_TO_DATE(200, """{"minVersion":"0.0.1"}"""),
    UPDATE_REQUIRED(200, """{"minVersion":"999.0.0"}"""),
    MIN_VERSION_ABSENT(200, "{}"),
    MIN_VERSION_MALFORMED(200, """{"minVersion":"abc"}"""),
    SERVER_ERROR(503, """{"code":"MAINTENANCE","message":"stub: mantenimiento"}"""),
    ;

    companion object {
        var selected: ConfigMockScenario = UP_TO_DATE

        /** Por nombre; uno desconocido deja [UP_TO_DATE]. */
        fun select(name: String?) {
            selected = entries.firstOrNull { it.name == name } ?: UP_TO_DATE
        }
    }
}

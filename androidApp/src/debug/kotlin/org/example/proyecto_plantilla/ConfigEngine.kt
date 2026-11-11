package org.example.proyecto_plantilla

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import org.example.proyecto_plantilla.core.network.ConfigMockScenario

/** Variante debug: el stub de config. Responde el escenario elegido en cada petición. */
fun provideConfigEngine(): HttpClientEngine? = MockEngine {
    val scenario = ConfigMockScenario.selected
    respond(scenario.body, HttpStatusCode.fromValue(scenario.status), headersOf(HttpHeaders.ContentType, "application/json"))
}

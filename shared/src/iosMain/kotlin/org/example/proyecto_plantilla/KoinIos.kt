package org.example.proyecto_plantilla

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import org.example.proyecto_plantilla.core.crash.GlobalCrashHandler
import org.example.proyecto_plantilla.core.di.initKoin
import org.example.proyecto_plantilla.core.di.iosPlatformModule
import org.example.proyecto_plantilla.core.network.ConfigMockScenario
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import org.example.proyecto_plantilla.ui.uiModule
import org.koin.core.Koin
import org.koin.core.KoinApplication

private var koinApplication: KoinApplication? = null

/**
 * Arranca Koin en iOS, que no tiene `Application`. Vive en el paquete raíz (raíz de composición) porque registra
 * `uiModule`, y nada de `core` depende de `ui`.
 *
 * Swift la llama en el `init()` del `@main` con TODOS los argumentos y el gate `#if DEBUG` en las dos ramas: `true/true`
 * en DEBUG, `false/false` en el else (los valores por defecto de Kotlin no llegan a Swift). Idempotente; guarda el `Koin`
 * aquí porque en iOS no se usa `GlobalContext` (trampa 5 de v2).
 */
fun startKoinIos(baseUrl: String, useMockConfig: Boolean, isDebug: Boolean): Koin {
    koinApplication?.let { return it.koin }
    val engine = if (useMockConfig) configMockEngine() else null
    val app = initKoin(
        listOf(
            iosPlatformModule(NetworkConfig(baseUrl = baseUrl, enableLogging = isDebug), engine, AppBuildInfo(isDebug)),
            uiModule,
        ),
    )
    koinApplication = app
    app.koin.get<GlobalCrashHandler>().install()
    return app.koin
}

/** Elige el escenario del stub (solo tiene efecto con `useMockConfig: true`). Swift la llama solo en DEBUG. */
fun selectConfigMockScenario(name: String?) = ConfigMockScenario.select(name)

private fun configMockEngine() = MockEngine {
    val scenario = ConfigMockScenario.selected
    respond(scenario.body, HttpStatusCode.fromValue(scenario.status), headersOf(HttpHeaders.ContentType, "application/json"))
}

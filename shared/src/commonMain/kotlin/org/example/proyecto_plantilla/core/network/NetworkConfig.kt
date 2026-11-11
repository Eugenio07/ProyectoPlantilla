package org.example.proyecto_plantilla.core.network

/**
 * Configuración de red que cada plataforma provee: Android desde su build, iOS desde Swift (`startKoinIos`).
 * Swift no recibe los valores por defecto de Kotlin: allí se pasan todos.
 */
data class NetworkConfig(
    val baseUrl: String,
    val enableLogging: Boolean,
    val requestTimeoutMs: Long = 15_000,
)

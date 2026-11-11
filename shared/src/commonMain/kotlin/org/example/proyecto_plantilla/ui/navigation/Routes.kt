package org.example.proyecto_plantilla.ui.navigation

import kotlinx.serialization.Serializable

// Rutas type-safe. NINGÚN argumento es un enum: un enum como argumento mata la app iOS al arrancar (trampa 9 de v2).
// Si alguna vez hace falta: @Serializable en el enum Y un NavType explícito con su typeMap, serializado por name.

@Serializable
data object SplashRoute

@Serializable
data object HomeRoute

@Serializable
data class DetailRoute(
    val id: String,
)

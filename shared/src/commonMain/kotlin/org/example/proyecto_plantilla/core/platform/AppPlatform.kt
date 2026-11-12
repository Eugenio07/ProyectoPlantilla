package org.example.proyecto_plantilla.core.platform

/**
 * La plataforma que corre. Cada módulo de plataforma la registra en Koin (`#PP24`: elige la URL de su tienda). No va en
 * [AppBuildInfo]: no es un dato del build, y con su propio tipo cada test de grafo comprueba su valor.
 */
enum class AppPlatform { ANDROID, IOS }

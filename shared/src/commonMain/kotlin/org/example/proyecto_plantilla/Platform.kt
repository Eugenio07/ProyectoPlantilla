package org.example.proyecto_plantilla

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
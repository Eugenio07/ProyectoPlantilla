package org.example.projecto_plantilla

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
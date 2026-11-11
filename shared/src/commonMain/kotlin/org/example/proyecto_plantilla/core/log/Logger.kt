package org.example.proyecto_plantilla.core.log

/** Registro de la app. Interfaz pura (la lista de `domain` la admite); cada plataforma inyecta su implementación. */
interface Logger {
    fun info(
        tag: String,
        message: String,
    )

    fun warn(
        tag: String,
        message: String,
    )

    fun error(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )
}

package org.example.proyecto_plantilla.domain.model

/** La config remota. `minVersion` llega sin validar: decide el caso de uso (fail-open si es nula o inválida). */
data class AppConfig(val minVersion: String?)

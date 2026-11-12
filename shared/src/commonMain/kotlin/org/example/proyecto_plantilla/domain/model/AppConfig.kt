package org.example.proyecto_plantilla.domain.model

/**
 * La config remota. Todo llega SIN validar: decide el caso de uso. `minVersion`: fail-open si es nula o inválida.
 * `storeUrlAndroid` / `storeUrlIos`: la ficha de cada tienda (`#PP24`); nula o inválida → sin botón «Actualizar».
 */
data class AppConfig(
    val minVersion: String?,
    val storeUrlAndroid: String? = null,
    val storeUrlIos: String? = null,
)

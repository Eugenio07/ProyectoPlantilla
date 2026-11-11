package org.example.proyecto_plantilla.core.platform

/** La versión instalada de la app (la que se compara con `minVersion`). Cada plataforma la inyecta con Koin. */
interface AppVersionProvider {
    /** La versión de cara al usuario (`versionName` en Android, `CFBundleShortVersionString` en iOS); "" si no hay. */
    fun versionName(): String
}

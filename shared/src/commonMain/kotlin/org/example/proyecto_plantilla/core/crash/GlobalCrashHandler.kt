package org.example.proyecto_plantilla.core.crash

/**
 * Captura los crashes no gestionados. **Asimétrico, a propósito:**
 * - **Android** (release): registra, persiste el estado y RELANZA la app en Home. Tras ese «reintentar» el proceso
 *   anterior muere (`exitProcess(0)`), pero la app sigue: quien la usa vuelve a Home, no al launcher. En depuración
 *   delega en el handler previo, para no esconder bugs.
 * - **iOS**: SOLO registra. En iOS no hay recuperación: el proceso termina y la persona reabre la app a mano.
 */
interface GlobalCrashHandler {
    /** Idempotente: se llama una vez al arrancar, desde la raíz de composición de cada plataforma. */
    fun install()
}

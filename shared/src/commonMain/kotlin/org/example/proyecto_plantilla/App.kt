package org.example.proyecto_plantilla

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.example.proyecto_plantilla.core.crash.CrashCircuitBreaker
import org.example.proyecto_plantilla.ui.navigation.AppNavHost
import org.example.proyecto_plantilla.ui.safemode.CrashSafeScreen
import org.example.proyecto_plantilla.ui.theme.AppTheme
import org.koin.compose.koinInject

/**
 * Raíz de la UI compartida: el NavHost (Splash → Home → Detail). [startAtHome]: Android, al relanzar tras un crash en
 * release, va directo a Home **sin** el chequeo de force-update de la Splash (decisión del ejecutor, Fase 6 A: la
 * recuperación vuelve a donde se puede usar la app; el chequeo vuelve en el siguiente arranque normal).
 */
@Composable
fun App(startAtHome: Boolean = false) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val breaker = koinInject<CrashCircuitBreaker>()
            var safeMode by remember { mutableStateOf(breaker.isTripped()) }
            if (safeMode) {
                // Reintentar: el ÚNICO reset en iOS; en Android también lo hace la vida sana.
                CrashSafeScreen(onRetry = {
                    breaker.reset()
                    safeMode = false
                })
            } else {
                AppNavHost(startAtHome = startAtHome)
            }
        }
    }
}

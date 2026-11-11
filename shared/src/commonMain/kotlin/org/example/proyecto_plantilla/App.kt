package org.example.proyecto_plantilla

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.proyecto_plantilla.ui.navigation.AppNavHost

/**
 * Raíz de la UI compartida: el NavHost (Splash → Home → Detail). [startAtHome]: Android, al relanzar tras un crash en
 * release, va directo a Home **sin** el chequeo de force-update de la Splash (decisión del ejecutor, Fase 6 A: la
 * recuperación vuelve a donde se puede usar la app; el chequeo vuelve en el siguiente arranque normal).
 */
@Composable
fun App(startAtHome: Boolean = false) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavHost(startAtHome = startAtHome)
        }
    }
}

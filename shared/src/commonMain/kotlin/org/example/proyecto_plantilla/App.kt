package org.example.proyecto_plantilla

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.proyecto_plantilla.ui.navigation.AppNavHost

/** Raíz de la UI compartida: el NavHost (Splash → Home → Detail). */
@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavHost()
        }
    }
}

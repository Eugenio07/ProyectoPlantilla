package org.example.proyecto_plantilla

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.proyecto_plantilla.ui.splash.SplashScreen
import org.koin.compose.viewmodel.koinViewModel

/** Raíz de la UI compartida. Hasta la navegación (Fase 5) solo muestra la Splash. */
@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SplashScreen(viewModel = koinViewModel())
        }
    }
}

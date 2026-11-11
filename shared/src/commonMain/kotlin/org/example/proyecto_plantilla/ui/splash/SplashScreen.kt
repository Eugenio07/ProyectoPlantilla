package org.example.proyecto_plantilla.ui.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import proyectoplantilla.shared.generated.resources.Res
import proyectoplantilla.shared.generated.resources.action_retry
import proyectoplantilla.shared.generated.resources.home_placeholder
import proyectoplantilla.shared.generated.resources.splash_error_body
import proyectoplantilla.shared.generated.resources.splash_force_update_body
import proyectoplantilla.shared.generated.resources.splash_force_update_title
import proyectoplantilla.shared.generated.resources.splash_loading

/** Splash SIN diseño (v2 Fase 4): cada estado, su pantalla. La navegación a Home llega en la Fase 5. */
@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SplashContent(state = state, onRetry = viewModel::retry)
}

@Composable
fun SplashContent(state: SplashUiState, onRetry: () -> Unit) {
    // liveRegion: el lector de pantalla anuncia el paso de Cargando a Error o a Actualización.
    Box(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp).semantics { liveRegion = LiveRegionMode.Polite },
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            SplashUiState.Loading -> {
                val label = stringResource(Res.string.splash_loading)
                CircularProgressIndicator(modifier = Modifier.semantics { contentDescription = label })
            }
            SplashUiState.Ready -> Text(stringResource(Res.string.home_placeholder), style = MaterialTheme.typography.titleLarge)
            is SplashUiState.ForceUpdate -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    stringResource(Res.string.splash_force_update_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    stringResource(Res.string.splash_force_update_body, state.minVersion),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
            is SplashUiState.Error -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(stringResource(Res.string.splash_error_body), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Button(onClick = onRetry) { Text(stringResource(Res.string.action_retry)) }
            }
        }
    }
}

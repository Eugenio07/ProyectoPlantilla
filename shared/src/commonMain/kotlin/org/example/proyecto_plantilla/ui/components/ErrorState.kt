package org.example.proyecto_plantilla.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import proyectoplantilla.shared.generated.resources.Res
import proyectoplantilla.shared.generated.resources.action_retry
import proyectoplantilla.shared.generated.resources.error_generic
import proyectoplantilla.shared.generated.resources.error_maintenance
import proyectoplantilla.shared.generated.resources.error_network
import proyectoplantilla.shared.generated.resources.error_rate_limited
import proyectoplantilla.shared.generated.resources.error_unauthorized
import proyectoplantilla.shared.generated.resources.error_validation

/** Error a pantalla completa, reutilizable. Reintentar solo si [ErrorKind.canRetry]. SIN diseño (v2 Fase 5). */
@Composable
fun ErrorState(
    kind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(stringResource(kind.message()), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        if (kind.canRetry) {
            Button(onClick = onRetry) { Text(stringResource(Res.string.action_retry)) }
        }
    }
}

private fun ErrorKind.message(): StringResource =
    when (this) {
        ErrorKind.NETWORK -> Res.string.error_network
        ErrorKind.MAINTENANCE -> Res.string.error_maintenance
        ErrorKind.RATE_LIMITED -> Res.string.error_rate_limited
        ErrorKind.UNAUTHORIZED -> Res.string.error_unauthorized
        ErrorKind.VALIDATION -> Res.string.error_validation
        ErrorKind.GENERIC -> Res.string.error_generic
    }

package org.example.proyecto_plantilla.ui.safemode

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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Pantalla segura tras varios crashes seguidos. AUTOCONTENIDA: sin DI, sin red, sin config remota y SIN RECURSOS
 * (cualquiera de esas piezas puede ser la que está fallando). Por eso su copy está escrita aquí: es la ÚNICA excepción
 * a «la copy vive en los recursos» (excepción declarada en `docs/ARCHITECTURE.md`). Textos aprobados como
 * provisionales el 2026-11-11 (`docs/mediciones/2026-11-11-aprobacion-pp5.md`).
 */
@Composable
fun CrashSafeScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(
            "Algo no salió bien",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            "La app se cerró varias veces seguidas. Puedes volver a intentarlo.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

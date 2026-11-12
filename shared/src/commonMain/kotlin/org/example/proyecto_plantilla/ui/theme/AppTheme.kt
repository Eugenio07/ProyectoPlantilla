package org.example.proyecto_plantilla.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// PROVISIONAL (#PP25): los esquemas base de Material, elegidos por quien decide como relleno de la plantilla. Cada app
// derivada pone AQUÍ su marca (CLAUDE.md §2), con dirección estética de frontend-design. Sin color dinámico (decisión).
internal val ProvisionalLight: ColorScheme = lightColorScheme()
internal val ProvisionalDark: ColorScheme = darkColorScheme()

/** El esquema según el modo: claro u oscuro. */
fun appColorScheme(dark: Boolean): ColorScheme = if (dark) ProvisionalDark else ProvisionalLight

/** El tema de la app: sigue al modo del sistema (claro u oscuro). */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = appColorScheme(darkTheme), content = content)
}

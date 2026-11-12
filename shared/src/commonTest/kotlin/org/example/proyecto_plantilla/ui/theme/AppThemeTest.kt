package org.example.proyecto_plantilla.ui.theme

import kotlin.test.Test
import kotlin.test.assertSame

/** #PP25: el modo del sistema elige el esquema. `ColorScheme` no tiene `equals`: se compara la identidad. */
class AppThemeTest {
    @Test
    fun darkSystemGetsDarkScheme() = assertSame(ProvisionalDark, appColorScheme(dark = true))

    @Test
    fun lightSystemGetsLightScheme() = assertSame(ProvisionalLight, appColorScheme(dark = false))
}

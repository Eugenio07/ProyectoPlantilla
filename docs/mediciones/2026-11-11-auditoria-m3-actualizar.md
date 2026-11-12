# Auditoría MD3 · pantalla de actualización obligatoria con «Actualizar» (`#PP24`)

- **Fecha:** 2026-11-11 · **Commit:** `eaacdf3` + la pantalla (sin commitear al capturar) · **Objetivo:** el estado
  `ForceUpdate` de `shared/src/commonMain/kotlin/org/example/proyecto_plantilla/ui/splash/SplashScreen.kt`.
- **Capturas** (depuración, stub `UPDATE_REQUIRED` / `UPDATE_REQUIRED_NO_STORE`): Android (emulador) e iOS (iPhone 18 Pro,
  simulador por UDID), con y sin botón, claro y oscuro, y tras pulsar.

**Puntuación global: 67/80 aplicables** (Navigation y Motion no aplican).

| Categoría | Nota | Estado | Por qué |
|---|---|---|---|
| Color tokens | 8 | pass | Sin `Color(...)`; el `Button` usa `primary`/`onPrimary`; el texto, `onSurface` del `Surface` de `App.kt` |
| Typography | 8 | pass | Título `headlineSmall` con `heading()`; cuerpo `bodyLarge`; la etiqueta del botón, la del componente |
| Shape | 9 | pass | `Button` con su forma completa por defecto |
| Elevation | 8 | pass | Plano; el botón relleno sin sombra |
| Components | 9 | pass | Un `Button` relleno para la ÚNICA acción, como «Reintentar» en `ErrorState`; ausente cuando no hay URL |
| Layout | 7 | pass | Centrado, `safeDrawingPadding`, `spacedBy(12.dp)` existente; espaciado a mano (sin tokens), sin ancho máximo |
| Navigation | — | n/a | Pantalla bloqueante, sin navegación |
| Motion | — | n/a | Sin animación |
| Accessibility | 7 | pass | `liveRegion` anuncia la pantalla; título marcado como encabezado; el botón tiene texto y su objetivo táctil ≥ 48 dp por defecto. Aviso: en Android con el sistema en oscuro, la barra de estado no se lee (`#PP25`) |
| Theming | 5 | warn | `MaterialTheme {}` sin esquema propio ni oscuro: deliberado en la plantilla (`#PP25`) |

**Tras pulsar** (lo que se midió): en Android abre la Play Store (`com.android.vending`, «Item not found»: la plantilla no
tiene ficha); en iOS abre Safari («la dirección no es válida»: el simulador no tiene App Store y `apps.apple.com` redirige
a él). En los dos, la URL de la plataforma llega a quien la abre; la ficha real la comprueba cada app derivada con el paso
nuevo de las listas de release.

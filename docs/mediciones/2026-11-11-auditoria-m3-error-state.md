# Auditoría MD3 · `ErrorState` (cambio de copy de `#PP5`)

- **Fecha:** 2026-11-11 · **Commit:** `3f39454` + la copy nueva de `error_network` (sin commitear al capturar) ·
  **Objetivo:** `shared/src/commonMain/kotlin/org/example/proyecto_plantilla/ui/components/ErrorState.kt`, con las
  capturas `2026-11-11-error-red-android.png` (`releaseLocal`, emulador con el sistema en modo oscuro) y
  `2026-11-11-error-red-ios.png` (Release, simulador por UDID, sistema en claro).
- **Alcance del cambio:** solo el texto. La pantalla es «SIN diseño» a propósito (v2 Fase 5): el tema y la dirección
  visual son de cada app derivada. Las notas bajas son del andamiaje, no de este cambio.

**Puntuación global: 66/100**

| Categoría | Nota | Estado | Por qué |
|---|---|---|---|
| Color tokens | 7 | pass | Sin `Color(...)` crudos; `Text` hereda `onSurface` del `Surface` de `App.kt:25`; `Button` usa `primary`/`onPrimary` |
| Typography | 8 | pass | `bodyLarge` para el mensaje; la etiqueta del botón, la del componente |
| Shape | 9 | pass | `Button` con su forma completa por defecto; sin radios a mano |
| Elevation | 8 | pass | Nada elevado; superficie plana |
| Components | 8 | pass | `Button` (relleno) para la única acción; sin acción cuando `canRetry` es falso |
| Layout | 6 | warn | Centrado y `safeDrawingPadding`; `24.dp`/`16.dp` a mano (múltiplos de 8, sin tokens de espaciado); sin ancho máximo en pantallas grandes |
| Navigation | 0 | — | No aplica: es un estado, no una pantalla de navegación |
| Motion | 0 | — | No aplica: sin animaciones |
| Accessibility | 6 | warn | `liveRegion = Polite` anuncia el error; botón ≥ 48 dp; texto legible en ambas. **Pero** en Android con el sistema en oscuro la app sigue en claro y los iconos de la barra de estado salen blancos sobre fondo claro (casi invisibles en la captura) |
| Theming | 4 | warn | `MaterialTheme {}` sin esquema propio (`App.kt:24`): sin tema oscuro ni color de marca. Es deliberado en la plantilla, pero produce el problema de la barra de estado |

(Las dos «no aplica» puntúan 0 por la guía, no por un defecto: sobre 80 aplicables, 66.)

## Avisos

1. **Barra de estado ilegible en Android con el sistema en oscuro** (`androidApp/.../MainActivity.kt:12`,
   `enableEdgeToEdge()` + tema solo claro). No lo introduce este cambio; va a la cola (`#PP25`).
2. **Sin tema oscuro** (`App.kt:24`): de la app derivada (dirección estética con `frontend-design`).
3. **Espaciado a mano**: aceptable en el andamiaje; una app con sistema de espaciado lo sustituye.

## Lo que este cambio sí comprueba

La copy nueva cabe en dos líneas centradas en las dos plataformas, sin cortes, y «Reintentar» sigue debajo (las dos
capturas).

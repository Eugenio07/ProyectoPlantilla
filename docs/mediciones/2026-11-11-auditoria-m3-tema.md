# Auditoría MD3 · AppTheme claro y oscuro (`#PP25` y `#PP3`)

- **Fecha:** 2026-11-11 · **Commit:** `1ae9590` + el tema en la app (sin commitear al capturar) · **Objetivo:**
  `ui/theme/AppTheme.kt`, `App.kt` y el tema de ventana de Android (`res/values{,-night}/themes.xml`), sobre todas las
  pantallas: Splash con «Actualizar», error, Home, Detail y la pantalla segura.
- **Capturas:** `2026-11-11-tema-*` (depuración con el stub; la pantalla segura de Android en `releaseLocal`, como la
  de `#PP10`), Android (emulador) e iOS (iPhone 18 Pro, por UDID), en claro y en oscuro, y un cambio de modo con la app
  abierta en Detail.

**Puntuación: 72/80 aplicables** (Navigation y Motion no aplican: el cambio es solo de tema).

| Categoría | Nota | Estado | Por qué |
|---|---|---|---|
| Color tokens | 9 | pass | Los dos esquemas completos (`lightColorScheme`/`darkColorScheme`); ningún `Color(0x…)` en `shared/src`; los componentes toman sus roles (`primary`/`onPrimary`, `surface`/`onSurface`) en los dos modos |
| Typography | 8 | pass | Sin cambios: escala de Material por defecto (la de marca, de la app derivada) |
| Shape | 9 | pass | Sin cambios: formas de los componentes |
| Elevation | 8 | pass | Superficies planas; en oscuro, `surface` tonal correcta |
| Components | 9 | pass | Botones rellenos y el `OutlinedButton` del andamiaje legibles en los dos modos (capturas) |
| Layout | 7 | pass | Sin cambios (espaciado a mano, sin ancho máximo: ya anotado en auditorías anteriores) |
| Navigation | — | n/a | |
| Motion | — | n/a | |
| Accessibility | 9 | pass | **La barra de estado se lee en los dos modos y en las dos plataformas** (antes no, en Android en oscuro). Contraste texto/fondo de los esquemas base de Material |
| Theming | 7 | pass | `AppTheme` sigue al sistema, con tema de ventana a juego en Android; sin color dinámico (decisión). Queda por debajo de 9 solo por la falta de marca, que es de cada app derivada (los colores son provisionales) |

**No medido:** la Splash «cargando» (el stub responde al instante: no hay fotograma que capturar).

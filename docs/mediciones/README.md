# Mediciones y capturas

Cada medición o captura se registra en el **índice** de abajo, con una fila por fichero:

| Campo | Qué va |
|---|---|
| Ruta | El fichero, relativo a este directorio |
| Plataforma | Android / iOS / JVM / repo |
| Build | depuración o release |
| Commit | El sha sobre el que se midió |
| Fecha | AAAA-MM-DD HH:MM en la zona del proyecto (America/Caracas) |
| Qué se ve | Para una captura: la clave del texto y el elemento que se ven, **escritos con la imagen abierta** |

Una medición guarda **las líneas que contestan su pregunta**, no todo lo que salió. Una captura se mira entera antes de
commitearla, por si trae un dato personal.

## Índice

| Ruta | Plataforma | Build | Commit | Fecha | Qué se ve / qué contesta |
|---|---|---|---|---|---|
| `2026-11-11-autorizacion-del-arranque.md` | repo | — | `c448186` | 2026-11-11 12:45 | La autorización literal de quien decide para el arranque |
| `2026-11-11-matriz-de-versiones.md` | repo | — | `00b30dd` | 2026-11-11 12:40 | La matriz de versiones con su evidencia (PROMPT 1) |
| `2026-11-11-app-vacia-android.png` | Android (Pixel_10_Pro, emulador) | depuración | `00b30dd` + rama `f0/catalogo` | 2026-11-11 12:52 | La app vacía tras borrar la plantilla del wizard: superficie clara a pantalla completa, sin texto; iconos de la barra de estado ilegibles (`#PP3`) |
| `2026-11-11-app-vacia-ios.png` | iOS (iPhone 17 Pro, simulador) | depuración | `00b30dd` + rama `f0/catalogo` | 2026-11-11 12:50 | La app vacía: superficie clara a pantalla completa, sin texto; barra de estado legible |

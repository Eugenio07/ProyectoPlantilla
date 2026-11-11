# Matriz de versiones (PROMPT 1 de v2, §1)

- **Fecha:** 2026-11-11 (America/Caracas) · **Commit de partida:** `00b30dd`
- **Método:** búsqueda web y lectura de `maven-metadata.xml`, notas de versión y documentación oficial, por un subagente.
  Cada fila: [verificado: fuente] o [no verificado].
- **Esto es un registro con fecha, no la fuente de las versiones.** La fuente es `gradle/libs.versions.toml`; las
  versiones no se copian a `CLAUDE.md` ni a otro documento vivo.

| Pieza | Elegida | Evidencia | Nota |
|---|---|---|---|
| Kotlin | 2.4.21 | kotlin-stdlib `maven-metadata.xml`; kotlinlang.org «multiplatform-compatibility-guide» | Última estable (2.5.0 en RC). Admite Gradle 7.6.3–9.7.0 y AGP 8.5.2–9.3.1 [verificado] |
| KSP | 2.3.12 | github.com/google/ksp/releases; google/ksp#3286 | Desde 2.3.0 no va atada a Kotlin; 2.3.10+ soporta Kotlin 2.4. **No 2.3.13**: regresión P1 sin versión con arreglo [verificado] |
| Compose Multiplatform | 1.12.2 | releases v1.12.2 | Última estable (1.13.0 en beta) [verificado] |
| material3 (JetBrains) | 1.12.0-alpha03 | releases v1.12.2 | La que declara CMP 1.12.2; no hay estable en esta línea [verificado] |
| lifecycle (JetBrains) | 2.11.0 | `maven-metadata.xml` | Última estable [verificado] |
| navigation-compose (JetBrains) | 2.10.0 | `maven-metadata.xml` | Estable; CMP 1.12.2 declara 2.10.0-rc01 [la pareja 1.12.2 + 2.10.0 no verificada] |
| Ktorfit | 2.7.5 (compilador 2.3.5) | Foso/Ktorfit releases, `KtorfitCompilerSubPlugin.kt`, PR #1100 | **No fija Kotlin**: «Kotlin ≥ 2.4 → compilador 2.3.5». Ese binario se compiló contra 2.4.0; que funcione con 2.4.21 [no verificado: lo decide un build real, entrada `#PP1`]. Artefactos iOS publicados [verificado] |
| Ktor | 3.5.2 | `maven-metadata.xml`; Ktorfit docs | Última es 3.6.1, pero Ktorfit 2.7.5 se construyó contra 3.5.0: se elige la línea 3.5 (decisión del ejecutor) |
| kotlinx-serialization | 1.12.0 | releases | Basada en Kotlin 2.4.10 [verificado] |
| kotlinx-coroutines | 1.11.0 | releases | [verificado] |
| Koin BOM | 4.2.2 | `maven-metadata.xml` | `koin-compose-viewmodel` con variante iOS [verificado]; compilada con CMP 1.10.2 / lifecycle 2.9.6 [la subida transitiva, no verificada] |
| AGP | 9.1.1 | developer.android.com notas AGP 9.1 | Dentro del rango de KGP 2.4.21 [verificado] |
| Gradle | 9.5.1 | gradle-wrapper | Dentro del rango [verificado] |
| GitLive Firebase (`firebase-app`, `firebase-crashlytics`) | 2.8.0 | releases; `.module` de 2.8.0 | Compilada con Kotlin 2.2.21; klibs compatibles hacia atrás [verificado], cinterop sin garantía [no verificado] |
| Firebase Android BoM | 34.18.0 | `.pom` de `firebase-crashlytics-android` 2.8.0 | La de GitLive 2.8.0. La 35.x es mayor y no está verificada con GitLive |
| Firebase iOS SDK (SwiftPM) | 12.19.3 | README de GitLive | Con la que se construye GitLive 2.8.0 [verificado] |
| google-services (plugin) | 4.6.0 | Google Maven | Soporte declarado de AGP 9 [no verificado] |
| firebase-crashlytics (plugin) | 3.0.8 | Google Maven | Exige `com.android.application`: va en `androidApp` [verificado] |
| Spotless / ktlint | 8.11.0 / 1.8.0 | plugins.gradle.org; Maven | ktlint lleva el compilador 2.2.21: sintaxis nueva de 2.4 [no verificado] |
| detekt | 1.23.8 estable (Kotlin 2.0.21) · 2.0.0-alpha.6 (Kotlin 2.4.10) | releases; detekt.dev compatibility | **No hay detekt estable para Kotlin 2.4** (entrada `#PP2`) |
| MockK | 1.14.12 | `maven-metadata.xml` | [verificado] |

**Lo que no se pudo verificar:** el compilador de Ktorfit 2.3.5 sobre Kotlin 2.4.21; AGP 9 con google-services 4.6.0 y
Crashlytics 3.0.8; GitLive 2.8.0 con BoM 35.x; Ktorfit con Ktor 3.6.x; la pareja CMP 1.12.2 + navigation 2.10.0.

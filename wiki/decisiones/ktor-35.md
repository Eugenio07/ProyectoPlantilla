# Ktor 3

- **Firma:** Ejecutor, en la matriz de versiones
- **Fecha:** 2026-11-11
- **Estado:** Vigente
- **descartada_porque:** la última línea de Ktor: compatibilidad binaria con Ktorfit sin verificar.

**Decisión.** Ktor se queda en la línea contra la que se construyó Ktorfit, no en la última (la pareja, en el comentario
de `gradle/libs.versions.toml`).

**Por qué.** La compatibilidad binaria de Ktorfit con una línea más nueva de Ktor no está verificada (los números, con su
fecha: `docs/mediciones/2026-11-11-matriz-de-versiones.md`).

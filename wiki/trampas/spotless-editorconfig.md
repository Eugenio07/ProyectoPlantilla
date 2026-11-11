# Una regla desactivada en `.editorconfig` sigue saliendo en `spotlessApply`.

- **Síntoma:** Una regla desactivada en `.editorconfig` sigue saliendo en `spotlessApply`.
- **Causa:** El ktlint de spotless no lee `.editorconfig` por su cuenta.
- **Regla:** CLAUDE.md §4 «La autoridad de formato es `build.gradle.kts`; `.editorconfig` es su copia».

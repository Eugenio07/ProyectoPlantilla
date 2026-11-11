package org.example.proyecto_plantilla.core.platform

/**
 * Lo que `commonMain` necesita saber del build. Cada plataforma lo aporta explícitamente: Android desde `BuildConfig`,
 * iOS desde Swift con `#if DEBUG` en las dos ramas (v2 §2, «Config-por-plataforma»).
 */
data class AppBuildInfo(
    val isDebug: Boolean,
)

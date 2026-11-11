plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt) apply false
}

// FORMATO: la autoridad es este bloque; su copia para el IDE vive en .editorconfig. Si cambias una, cambias la otra.
val ktlintRules =
    mapOf(
        "ktlint_code_style" to "ktlint_official",
        "max_line_length" to "120",
        // Los @Composable se nombran en PascalCase por convención de Compose.
        "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
        // El paquete lleva guion bajo por decisión de quien decide (renombrado del 2026-11-11). Renombrarlo es producto.
        "ktlint_standard_package-name" to "disabled",
    )

spotless {
    kotlin {
        target("**/src/**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get()).editorConfigOverride(ktlintRules)
    }
    kotlinGradle {
        target("*.gradle.kts", "**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get()).editorConfigOverride(ktlintRules)
    }
}

subprojects {
    apply(plugin = "dev.detekt")
    extensions.configure<dev.detekt.gradle.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        baseline = file("detekt-baseline.xml")
        source.setFrom("src")
    }
    // El código generado (Ktorfit, recursos de Compose) no es nuestro: fuera del análisis.
    tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
        exclude { it.file.absolutePath.contains("/build/") }
    }
}

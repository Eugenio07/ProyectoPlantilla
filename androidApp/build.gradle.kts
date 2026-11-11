import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
}

// FIREBASE SIN PROYECTO: la plantilla compila y arranca sin google-services.json (no se commitea: .gitignore + pre-commit).
// Con el fichero, se aplican los dos plugins, y van JUNTOS: con Firebase inicializado y sin el plugin de Crashlytics, la
// app crashea al arrancar (falta el build ID de Crashlytics). Sin el fichero, Firebase no se inicializa y la app sigue
// (medición: docs/mediciones/2026-11-11-firebase-android-sin-proyecto.md). Cómo dar de alta el proyecto: la lista de
// release de Android (docs/android-release-checklist.md, Fase 8, incremento 3).
//
// OJO: un build SIN el fichero (el CI, por ejemplo) no lleva Crashlytics. Antes de publicar, `./gradlew
// :androidApp:verificarFirebase` falla si falta: lo exige la lista de release.
val firebaseConfigurado = file("google-services.json").exists()
if (firebaseConfigurado) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}

tasks.register("verificarFirebase") {
    description = "Falla si falta androidApp/google-services.json (un release sin él no lleva Crashlytics)."
    val configurado = firebaseConfigurado
    doLast {
        check(configurado) { "Falta androidApp/google-services.json: este build no llevaría Crashlytics." }
        println("Firebase configurado: google-services.json presente.")
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.koin.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.koin.android)
    implementation(libs.ktor.client.core)
    // El stub de config, SOLO en debug (v2 Fase 4). Nunca en release: lo comprueba la dependencia de releaseRuntimeClasspath.
    debugImplementation(libs.ktor.client.mock)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "org.example.proyecto_plantilla"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "org.example.proyecto_plantilla"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0.0"
        // Sin backend todavía (Fase 3, rama A): dominio reservado, nunca resuelve. Se cambia al llegar el backend.
        buildConfigField("String", "BASE_URL", "\"https://api.example.invalid/\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        // releaseLocal: SOLO para probar en local el comportamiento de release (Fase 6: el crash reabre en Home).
        // Firmado con la clave de DEPURACIÓN: nunca se distribuye. `release` sigue sin firmar hasta la lista de release
        // de Android (keystore real, Fase 8). El CI no construye esta variante.
        create("releaseLocal") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets {
        // releaseLocal usa el engine de release (sin stub).
        getByName("releaseLocal").kotlin.directories.add("src/release/kotlin")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

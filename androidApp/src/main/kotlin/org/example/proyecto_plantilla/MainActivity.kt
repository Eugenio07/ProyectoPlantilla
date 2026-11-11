package org.example.proyecto_plantilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.example.proyecto_plantilla.core.crash.AndroidGlobalCrashHandler
import org.example.proyecto_plantilla.core.network.ConfigMockScenario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            // Stub de config: `adb shell am start -n <app>/.MainActivity --es mockScenario UPDATE_REQUIRED`.
            ConfigMockScenario.select(intent.getStringExtra("mockScenario"))
        }

        // Solo en el primer onCreate: una recreación (giro, tema) no vuelve a saltarse la Splash.
        val startAtHome =
            savedInstanceState == null &&
                intent.getBooleanExtra(AndroidGlobalCrashHandler.EXTRA_START_AT_HOME, false)
        setContent {
            App(startAtHome = startAtHome)
        }
    }
}

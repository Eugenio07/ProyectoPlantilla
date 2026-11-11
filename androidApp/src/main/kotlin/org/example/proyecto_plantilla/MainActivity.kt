package org.example.proyecto_plantilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.example.proyecto_plantilla.core.network.ConfigMockScenario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            // Stub de config: `adb shell am start -n <app>/.MainActivity --es mockScenario UPDATE_REQUIRED`.
            ConfigMockScenario.select(intent.getStringExtra("mockScenario"))
        }

        setContent {
            App()
        }
    }
}

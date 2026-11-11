package org.example.proyecto_plantilla

import android.app.Application
import org.example.proyecto_plantilla.core.crash.GlobalCrashHandler
import org.example.proyecto_plantilla.core.di.androidPlatformModule
import org.example.proyecto_plantilla.core.di.initKoin
import org.example.proyecto_plantilla.core.network.NetworkConfig
import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import org.example.proyecto_plantilla.ui.uiModule
import org.koin.android.ext.koin.androidContext

class ProyectoPlantillaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            platformModules = listOf(
                androidPlatformModule(
                    NetworkConfig(baseUrl = BuildConfig.BASE_URL, enableLogging = BuildConfig.DEBUG),
                    engineOverride = provideConfigEngine(),
                    buildInfo = AppBuildInfo(isDebug = BuildConfig.DEBUG),
                ),
                uiModule,
            ),
        ) { androidContext(this@ProyectoPlantillaApp) }.koin.get<GlobalCrashHandler>().install()
    }
}

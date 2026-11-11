package org.example.proyecto_plantilla.core.platform

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class AndroidAppVersionProvider(private val context: Context) : AppVersionProvider {
    override fun versionName(): String {
        val pm = context.packageManager
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION") // La sobrecarga con Int está deprecada desde API 33; minSdk es 24.
            pm.getPackageInfo(context.packageName, 0)
        }
        return info.versionName.orEmpty()
    }
}

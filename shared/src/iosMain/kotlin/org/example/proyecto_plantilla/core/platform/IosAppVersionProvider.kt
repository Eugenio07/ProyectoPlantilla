package org.example.proyecto_plantilla.core.platform

import platform.Foundation.NSBundle

class IosAppVersionProvider : AppVersionProvider {
    override fun versionName(): String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: ""
}

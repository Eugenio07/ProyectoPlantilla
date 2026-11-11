package org.example.proyecto_plantilla.core.crash

import android.content.Context
import com.google.firebase.FirebaseApp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import org.example.proyecto_plantilla.core.log.Logger

/**
 * Crashlytics por GitLive. Sin `google-services.json` Firebase no se inicializa y `Firebase.crashlytics` lanzaría: por
 * eso se mira antes si hay una app de Firebase, y si no, no se informa.
 */
class GitLiveCrashReporter(
    private val context: Context,
    private val logger: Logger,
) : CrashReporter {
    override val isActive: Boolean
        get() = FirebaseApp.getApps(context).isNotEmpty()

    override fun recordException(throwable: Throwable) {
        if (!isActive) {
            logger.warn(TAG, "Firebase no está inicializado: el crash no se informa")
            return
        }
        Firebase.crashlytics.recordException(throwable)
    }

    private companion object {
        const val TAG = "CrashReporter"
    }
}

import FirebaseCore
import FirebaseCrashlytics
import Shared

/// El `CrashReporter` de iOS, con el SDK nativo de Crashlytics (contrato de callback: v2, trampa 12). Lo recibe Kotlin en
/// `startKoinIos`. Sin `GoogleService-Info.plist` Firebase no se configura: no se informa.
final class SwiftCrashReporter: CrashReporter {
    var isActive: Bool { FirebaseApp.app() != nil }

    func recordException(throwable: KotlinThrowable) {
        guard isActive else {
            print("W/CrashReporter: Firebase no está configurado: el crash no se informa")
            return
        }
        let error = NSError(
            domain: "KotlinException",
            code: 0,
            userInfo: [NSLocalizedDescriptionKey: throwable.message ?? throwable.description()]
        )
        Crashlytics.crashlytics().record(error: error)
    }
}

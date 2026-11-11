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
        // ExceptionModel y no un NSError: Crashlytics agrupa por tipo y por pila. Un NSError fijo juntaría todos los crashes
        // en una sola incidencia sin traza (auditoría 2026-11-11, #PP11).
        let modelo = ExceptionModel(
            name: String(describing: type(of: throwable)),
            reason: throwable.message ?? throwable.description()
        )
        let pila = throwable.getStackTrace()
        var frames: [StackFrame] = []
        for i in 0..<pila.size {
            if let simbolo = pila.get(index: i) {
                frames.append(StackFrame(symbol: String(simbolo), file: "", line: 0))
            }
        }
        modelo.stackTrace = frames
        Crashlytics.crashlytics().record(exceptionModel: modelo)
    }
}

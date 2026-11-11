import FirebaseCore
import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Firebase ANTES de Koin (v2, trampa 6). Sin GoogleService-Info.plist en el bundle (la plantilla no tiene proyecto)
        // no se llama: FirebaseApp.configure() lanzaría una excepción de Objective-C que Swift no captura.
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
        }
        // Sin backend todavía (Fase 3, rama A): dominio reservado, nunca resuelve.
        let baseUrl = "https://api.example.invalid/"
        // Gate de depuración en las DOS ramas: los valores por defecto de Kotlin no llegan a Swift.
        #if DEBUG
        // Escenario del stub: `xcrun simctl launch booted <bundle> -mockScenario UPDATE_REQUIRED`.
        KoinIosKt.selectConfigMockScenario(name: UserDefaults.standard.string(forKey: "mockScenario"))
        _ = KoinIosKt.startKoinIos(baseUrl: baseUrl, useMockConfig: true, isDebug: true, crashReporter: SwiftCrashReporter())
        #else
        _ = KoinIosKt.startKoinIos(baseUrl: baseUrl, useMockConfig: false, isDebug: false, crashReporter: SwiftCrashReporter())
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

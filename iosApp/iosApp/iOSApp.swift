import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Sin backend todavía (Fase 3, rama A): dominio reservado, nunca resuelve.
        let baseUrl = "https://api.example.invalid/"
        // Gate de depuración en las DOS ramas: los valores por defecto de Kotlin no llegan a Swift.
        #if DEBUG
        // Escenario del stub: `xcrun simctl launch booted <bundle> -mockScenario UPDATE_REQUIRED`.
        KoinIosKt.selectConfigMockScenario(name: UserDefaults.standard.string(forKey: "mockScenario"))
        _ = KoinIosKt.startKoinIos(baseUrl: baseUrl, useMockConfig: true, isDebug: true)
        #else
        _ = KoinIosKt.startKoinIos(baseUrl: baseUrl, useMockConfig: false, isDebug: false)
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

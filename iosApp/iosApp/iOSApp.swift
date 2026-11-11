import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Sin backend todavía (Fase 3, rama A): dominio reservado, nunca resuelve.
        let baseUrl = "https://api.example.invalid/"
        // Gate de depuración en las DOS ramas: los valores por defecto de Kotlin no llegan a Swift.
        #if DEBUG
        _ = KoinIosKt.startKoinIos(baseUrl: baseUrl, isDebug: true)
        #else
        _ = KoinIosKt.startKoinIos(baseUrl: baseUrl, isDebug: false)
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

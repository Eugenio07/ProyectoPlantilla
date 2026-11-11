# Arquitectura

Clean Architecture en **un solo módulo Gradle** (`shared`), con una sola UI en Compose Multiplatform para Android e
iOS. `androidApp` y `iosApp` solo arrancan la app y aportan lo que cada plataforma tiene que dar (config, proveedores).

Ante conflicto entre este documento y el código, **gana el código**: se corrige el documento.

## Paquetes

Raíz: `shared/src/commonMain/kotlin/org/example/proyecto_plantilla/`

| Paquete | Qué vive ahí |
|---|---|
| `core/di` | Módulos de Koin comunes, `initKoin()` |
| `core/network` | `NetworkConfig`, `HttpClientFactory`, `NetworkErrorMapper`, `safeApiCall` |
| `core/error` | `AppError` (Kotlin puro) |
| `core/result` | `Resource<T>` (Kotlin puro) |
| `core/log` | La **interfaz** de logging (la implementación puede vivir fuera) |
| `core/platform` | **Contratos** de proveedores de plataforma (interfaces). Las implementaciones van en `androidMain`/`iosMain`, inyectadas con Koin |
| `core/crash` | `GlobalCrashHandler`, `CrashCircuitBreaker`, `CrashCounterStore`, `CrashReporter` (contratos y lógica) |
| `data/remote/dto` | DTOs serializables; la clave JSON es la del contrato (`@SerialName` si difiere) |
| `data/remote/api` | Interfaces Ktorfit |
| `data/remote/mapper` | DTO → modelo de dominio |
| `data/repository` | Implementaciones de los repositorios: devuelven `Resource<T>` y **nunca lanzan** (solo relanzan `CancellationException`) |
| `domain/model` | Modelos de dominio |
| `domain/repository` | Interfaces de repositorio |
| `domain/usecase` | Casos de uso. **Uno existe solo si hace algo que el repositorio no puede** |
| `ui/navigation` | NavHost y rutas type-safe (`@Serializable`) |
| `ui/components` | Componentes reutilizables (`ErrorState`…) |
| `ui/splash` | Splash y su ViewModel |
| `ui/safemode` | `CrashSafeScreen`, autocontenida |

## Regla de dependencias

- **`domain` depende solo de estos paquetes de `core`**, enumerados: `core/error`, `core/result`, `core/log` (solo la
  interfaz) y `core/platform` (solo contratos). **Nunca** de `core/network` ni de `core/di`, ni de `data` ni de `ui`.
- `data` → `domain` (y `core`).
- `ui` → `domain` (y `core`).
- **Nada depende de `ui`.**

**Cómo se amplía la lista de `domain`:** con una decisión escrita aquí, con firma y fecha, que diga qué paquete entra y
por qué es puro. No por costumbre. La lista está enumerada a propósito: la versión corta («domain solo depende de cosas
puras») genera falsos positivos y «correcciones» de código sano.

| Fecha | Paquete añadido | Firma | Por qué es puro |
|---|---|---|---|
| — | — | — | (sin ampliaciones todavía) |

## Excepciones deliberadas

(Ninguna todavía. Cada excepción se enumera aquí junto a la regla que exceptúa; una regla sin sus excepciones hace que un
agente «corrija» código sano.)

## Configuración por plataforma

Todo lo que Android obtiene de su build system (baseUrl, flags de depuración) **iOS lo recibe explícitamente** desde
Swift, con el gate de depuración `#if DEBUG` en las dos ramas, pasado como parámetro a `startKoinIos(...)`: los valores
por defecto de Kotlin no llegan a Swift.

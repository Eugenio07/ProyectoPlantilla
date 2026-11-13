# CLAUDE.md · Proyecto Plantilla

Reglas operativas: cada una es el enunciado, su **prueba** (el verificador que la mira o la pregunta que hay que
contestar) y un puntero de una línea a su porqué en `wiki/`. Los números de sección no se renumeran: los citan `wiki/` y
los guiones. Ante conflicto entre un documento y el código, **gana el código**: se corrige el documento.

## §1 · Qué es

- Plantilla Kotlin Multiplatform (Compose Multiplatform, Android + iOS) sobre la que se construyen apps. Manuales en
  `docs/manuales/` (v3): el de arranque montó esto; el de operación dice cómo se trabaja.
- Para trabajar una tarea, sigue el ciclo del manual de operación §2: ficha y alcance autorizado (provisional) → plan
  con `docs/plans/_plantilla.md` → auditor sin contexto → autorización escrita → rama → puerta → PR. Prueba: ¿el plan
  tiene `Implementación autorizada: Sí (…)` antes del primer commit de código?
- Lanza el subagente `auditor-sin-contexto` con SOLO la ruta del plan, siempre y sin preguntar, antes de enseñarlo.
  Prueba: ¿*Revisiones* tiene la pasada con su fecha antes de la revisión?
- Salta plan y auditor solo en el carril ligero: un cambio S que toca documentación o configuración sin conducta nueva.
  La revisión de pre-PR no se salta nunca. Prueba: ¿el PR sin plan toca solo eso, y cita su revisión?
- Guarda cada pasada del auditor, literal y antes de aplicar ningún hallazgo, en
  `.claude/reviews/review-<rama>-<fecha>[-pasada-N].md` (local, sin rastrear). Prueba: ¿existe el fichero de la pasada?

## §2 · Ranuras

- Máquina de verificación: una Mac (Android + iOS). Sin ella no corren iOS ni la puerta; la válvula es el CI alojado.
- CI: runners alojados de GitHub (`.github/workflows/ci.yml`). Prueba: los checks del PR. Por qué:
  `wiki/decisiones/ci-alojado.md`.
- Protección de ramas: no disponible (403). Rigen las reglas de fusión de §9. Por qué:
  `wiki/decisiones/fusion-sin-proteccion.md`.
- Backend: ninguno → contratos en rama A (§12). Personas: una. Quien decide: Eugenio Martínez. Revisor externo: un
  agente en otra conversación que NO lee el código (hoy, la revisión de Rumba, por medio de quien decide).
- El agente `revisor-externo` NO es esa ranura: lee el repositorio, así que es un segundo auditor con otro modelo, que
  juzga la Parte 1 si quien decide lo pide. Prueba: su `tools:` incluye Read y Bash.
- Ruta crítica, activo crítico y dónde vive: no aplican a la plantilla; **cada app derivada los rellena antes de su
  primera ficha**. Publicada: no. El ejecutor prueba contra: local (stub de §6). Bandeja: incidencias de GitHub
  (confirmada por quien decide, 2026-11-11). Zona horaria: America/Caracas.
- Revisa, en cada app derivada y antes de su primera ficha, los textos provisionales de la plantilla (`strings.xml`,
  `CrashSafeScreen`, acciones de `api-errors.md`) y los colores provisionales de `AppTheme` (`ui/theme/`). Prueba: ¿su
  primera ficha los cita?
- Auditoría visual de UI: la skill `compose-visual-loop` (captura real en las dos plataformas) más `/material-3 audit`
  con sus 10 categorías. Prueba: ¿el cierre de una tarea de UI cita las capturas en `docs/mediciones/` y el informe?
- Herramienta de grafo: ninguna. Escribe una afirmación de alcanzabilidad como «no comprobada: sin herramienta de grafo».

## §3 · Stack

- Pon las versiones solo en `gradle/libs.versions.toml`; nunca las copies a otro documento. Prueba:
  `config/checks/versiones.py` (documentos vivos; las mediciones y la cola son registros con fecha).
- Antes de subir Kotlin, comprueba Ktorfit, KSP y los klibs de iOS (`docs/mediciones/2026-11-11-matriz-de-versiones.md`).
  Prueba: la puerta (los tests de iOS enlazan y corren).
- Mantén Ktor en la línea con la que se construyó Ktorfit (el comentario del catálogo dice cuál). Por qué:
  `wiki/decisiones/ktor-35.md`.
- Usa la alpha de detekt del catálogo hasta que haya una estable para nuestra Kotlin (`#PP2`). Por qué:
  `wiki/decisiones/detekt-alpha.md`.

## §4 · La puerta y el commit

- Commitea con la puerta: `config/checks/puerta.sh --commit <fichero-del-mensaje> [nuevos…]`, con el mensaje en un
  fichero (heredoc `<<'EOF'`). Prueba: la puerta se niega si queda algo fuera del índice.
- Pasa la puerta entera (sin `--rapido`) antes de abrir un PR. Prueba: el PR cita «PUERTA VERDE» y el recuento de tests.
- Trata cualquier salida distinta de 0 como rojo, incluido el 3 («no pude medir»).
- No corras Gradle sobre el árbol mientras la puerta corre: la puerta es dueña del árbol. Prueba: `config/checks/candado.sh
  estado`; `config/checks/candado_test.sh`.
- Escribe la autoridad de formato en `build.gradle.kts` (`ktlintRules`) y su copia en `.editorconfig`; cambia las dos.
  Por qué: `wiki/trampas/spotless-editorconfig.md`.
- Comprueba que un verde es de una ejecución: `config/checks/corrio.sh` (puerta y CI). Por qué:
  `wiki/trampas/puerta-tarea-sin-acciones.md`.

- Deja que los hooks de `.claude/` informen (Stop: fallan abiertos) y que el guard deniegue (falla cerrado). Prueba:
  `config/checks/hooks_test.sh --completo`.

- Prefija todo command del proyecto con `plantilla-` (los built-ins como `/review` tienen precedencia). Revisa antes
  de abrir un PR con `/plantilla-review`; sin backend, `/plantilla-contract-sync` reporta divergencias DTO-contrato.
- Valida cada skill, command o agente nuevo en tres pasos (positiva, control, negativa) en una sesión nueva, con su fila
  en `.claude/validaciones.md`. Prueba: `config/checks/validaciones.py` (declarada, no verificada; `#PP7`).
- Declara una propiedad de una pieza («solo lee», «no modifica») solo con el mecanismo que la sostiene, o «por
  instrucción». Prueba: `config/checks/validaciones.py`. Por qué: `wiki/decisiones/propiedad-con-mecanismo.md`.

## §5 · Arquitectura y red

- Respeta la regla de dependencias de `domain`: depende solo de `core/error`, `core/result`, `core/log` (interfaz),
  `core/platform` (contratos) y `core/version`; nunca de `core/network`, `core/di`, `data` ni `ui`. `data` y `ui` →
  `domain`; nada depende de `ui`; el paquete raíz es la raíz de composición. La lista se amplía con una decisión
  escrita en `docs/ARCHITECTURE.md`. Prueba: `grep -rn "^import" shared/src/commonMain/kotlin/**/domain`.
- Crea un use case solo si hace algo que el repositorio no puede. Prueba: ¿qué decide que el repositorio no decida?
- Haz que un repositorio devuelva `Resource<T>` y nunca lance (solo `CancellationException`): usa `safeApiCall`. Prueba:
  `SafeApiCallTest`.
- Mapea `ContentConvertException` y `SerializationException` a `AppError.Serialization`. Prueba: `SafeApiCallTest`. Por
  qué: `wiki/trampas/json-convert-exception.md`.

## §6 · Config por plataforma

- Da a iOS explícitamente lo que Android saca de su build: `startKoinIos(baseUrl:useMockConfig:isDebug:crashReporter:)`
  con `#if DEBUG` en las dos ramas, todos los argumentos (los valores por defecto de Kotlin no llegan a Swift). Prueba:
  `KoinIosTest` y `AndroidGraphTest` (un test de grafo por plataforma).
- Inyecta los proveedores de plataforma con Koin (interfaz en `commonMain`), no con `expect/actual`.
- Usa `ConfigMockScenario` como única fuente de los JSON del stub: Android `--es mockScenario <X>` en depuración; iOS
  `-mockScenario <X>` solo en `#if DEBUG`.
- En iOS no registres con `NSLog` y argumentos variádicos: compón el mensaje en Kotlin. Prueba: `IosLoggerTest`. Por qué:
  `wiki/trampas/nslog-variadico.md`.

## §7 · Tests y verificación

- Sigue el orden por capa: lógica pura, repositorios, use cases y ViewModels con test primero (rojo por una ASERCIÓN,
  no por no compilar); UI con captura real; un bug, con el test que lo reproduce primero. Prueba: el plan dice el orden
  de cada tarea.
- Usa `MockEngine` + pipeline Ktor real + `runBlocking` (en `androidHostTest`/`iosTest`); `runTest` para lógica suspend
  con fakes; MockK nunca en `commonTest`.
- Prueba cada guarda con `config/checks/mutar.sh` y lee el rojo hasta la aserción; 3 = la mutación no se aplicó o no
  compiló. Prueba: el plan pega el diff y la aserción que cayó.
- Usa el UDID explícito con `simctl`, nunca `booted`. Por qué: `wiki/trampas/simctl-booted-ambiguo.md`.

## §8 · Crash, breaker y Firebase

- Mantén la asimetría: Android en release relanza en Home (en depuración delega); iOS solo registra. Prueba: capturas
  de `docs/mediciones/2026-11-11-crash-release-*`.
- Recuerda que tras un crash recuperado la app va a Home sin force-update. Por qué:
  `wiki/decisiones/recuperacion-salta-force-update.md`.
- Mantén `CrashSafeScreen` autocontenida (sin DI, red ni recursos). Prueba: sus imports.
- Prueba release en local con `releaseLocal`; nunca firmes `release` con la clave de depuración. Por qué:
  `wiki/decisiones/release-local.md`.
- Aplica los plugins de google-services y Crashlytics juntos y solo con `google-services.json`; antes de publicar,
  `./gradlew :androidApp:verificarFirebase`. Por qué: `wiki/trampas/crashlytics-sin-plugin.md`,
  `wiki/decisiones/firebase-sin-proyecto.md`.
- Pon GitLive solo en `androidMain`; en iOS el reporter es `SwiftCrashReporter`. Por qué:
  `wiki/trampas/gitlive-ios-enlace.md`, `wiki/decisiones/gitlive-solo-android.md`.
- Usa los `.example` de Firebase como forma, no para probar. Por qué: `wiki/trampas/plist-ejemplo-crashea.md`.
- Di desde qué build se provocó un evento antes de leer un cero de Crashlytics: la colección está apagada en depuración.

## §9 · CI y Git Flow

- Trabaja una tarea en una rama, súbela al crearla y ábrela en un PR contra `develop` cuando no quede nada por cambiar.
  `main` es la rama de versiones. Por qué: `wiki/decisiones/main-es-master.md`.
- Fusiona a mano: nunca `gh pr merge --auto`; espera el verde; comprueba la cabeza (`--match-head-commit`). Prueba:
  `config/checks/ramas_varadas.sh`.
- Ante un check rojo, pregunta primero si corrió.

## §10 · Idioma

- Escribe identificadores en inglés y copy en español, siempre en `composeResources` (la pantalla segura es la única
  excepción, §13). Las claves JSON del contrato son intocables.
- Propón el texto que lee quien usa la app y para: su versión final es de quien decide. Los de la plantilla están
  aprobados como provisionales (`docs/mediciones/2026-11-11-aprobacion-pp5.md`).

## §11 · No-hacer

- No toques `settings.gradle.kts`, el `build.gradle.kts` raíz ni `gradle/libs.versions.toml` sin confirmación.
- No hardcodees versiones ni secretos; no commitees `google-services.json`, `GoogleService-Info.plist`, keystores ni
  `local.properties`. Prueba: `.githooks/pre-commit` (una vez por clon: `git config core.hooksPath .githooks`).
- No uses `git add -A` ni `git add .`: nombra los ficheros nuevos.
- Lee una lista `-z` por sustitución de proceso, nunca de una variable. Por qué: `wiki/trampas/nul-en-sustitucion.md`.
- Corre en bash los comandos que manejan listas de ficheros o de tareas (`bash -c '…'` o un guion): zsh no parte una
  variable en palabras. Prueba: ¿el comando con la lista empieza por `bash`? Por qué: `wiki/trampas/zsh-no-parte-variables.md`.

## §12 · Contratos

- Construye los DTO sobre los borradores de `docs/contracts/` (rama A, sin backend); la clave JSON es la del contrato
  (`@SerialName` si la propiedad difiere). Al llegar un backend, rama B o C con su verificador en la puerta.

## §13 · Excepciones deliberadas

- El paquete lleva guion bajo (`proyecto_plantilla`): ktlint `package-name` y detekt `PackageNaming` lo admiten. Por qué:
  `wiki/decisiones/paquete-con-guion-bajo.md`.
- `ktor-client-mock` va en `iosMain` (iOS no tiene variantes; lo apaga Swift); en Android es `debugImplementation`.
- `CrashSafeScreen` lleva su copy en el código.
- Supresiones en el sitio con su motivo: `TooGenericExceptionCaught` en `safeApiCall`, `FunctionOnlyReturningConstant`
  en el `provideConfigEngine` de release, `FunctionNaming` en `MainViewController` (Swift lo llama así).
- El `pbxproj` y `Package.resolved` fijan la versión del SDK de Firebase de iOS: SwiftPM no lee el catálogo de Gradle.
  Se sube a mano junto con GitLive (el comentario del catálogo dice la pareja).
- El guard mira `Bash` por heurística: deniega un comando que nombra un protegido y escribe. No ve uno que no se
  nombra (variable, glob); el pre-commit y el CI siguen siendo la red. Prueba: `config/checks/hooks_test.sh`.
- La puerta corre también los tests de iOS en el simulador. Por qué: `wiki/decisiones/tests-ios-en-la-puerta.md`.

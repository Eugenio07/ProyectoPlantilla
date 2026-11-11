# Fase 8 · Firebase en Android (sin proyecto)

- **Estado:** Implementado
- **Tamaño:** M
- **Entrada:** v2, FASE 8, incremento 1 de 3 (puntos 1, 2 y 6 del prompt). **Condición de quien decide** (2026-11-11,
  `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`): no hay proyecto de Firebase; la construcción entera queda
  montada «como si lo hubiera» y la app compila y arranca sin él.
- **Implementación autorizada:** Sí (quien decide, 2026-11-11, el arranque entero)

## Parte 2 · Plan

### Verificación previa

Contra el commit: `011e9f5`

| Afirmación | Cómo se comprobó | HECHO / SUPUESTO |
|---|---|---|
| GitLive 2.8.0 + BoM 34.18.0 compilan en Android e iOS con Kotlin 2.4.21 | `assembleDebug` + `compileKotlinIosSimulatorArm64` con las dependencias añadidas [medido 2026-11-11] | HECHO (BoM → Crashlytics 20.1.0) |
| Sin `google-services.json` y sin plugins, la app arranca | instalada y lanzada: pid vivo; logcat «Default FirebaseApp failed to initialize… FirebaseApp initialization unsuccessful»; sin crash [medido] | HECHO |
| google-services 4.6.0 y Crashlytics 3.0.8 se aplican con AGP 9 (la matriz lo dejó «no verificado») | con un `google-services.json` de ejemplo: `processDebugGoogleServices` e `injectCrashlytics…` corren, BUILD SUCCESSFUL [medido] | HECHO |
| Con config (de ejemplo) y los dos plugins, la app arranca | pid vivo; «Initializing Firebase Crashlytics 20.1.0»; solo avisos de clave inválida [medido] | HECHO |
| El prompt dice «el plugin de Crashlytics es OBLIGATORIO: sin él la app crashea en FirebaseInitProvider» | config de ejemplo + solo google-services: crash al arrancar, «The Crashlytics build ID is missing» [medido] | HECHO **con config**; sin config no se inicializa nada. Los dos plugins van juntos y condicionados al fichero |

Las tres mediciones de arranque, con sus líneas de logcat: `docs/mediciones/2026-11-11-firebase-android-sin-proyecto.md`.

### Solución

- `shared`: `firebase-app` y `firebase-crashlytics` (GitLive) **en `androidMain`**, con el BoM; BoM también en
  `androidApp`. **Desviación del prompt** («impl GitLive en las dos plataformas»): en `commonMain`, el klib de iOS de GitLive
  hizo fallar el enlace del binario de tests de iOS (`ld: framework 'FirebaseCore' not found`, medido por la puerta
  2026-11-11). En iOS, el `CrashReporter` lo implementa Swift con el SDK nativo (incrementos 2 y 3).
- `androidApp`: plugins `google-services` y `crashlytics` declarados `apply false` y **aplicados juntos solo si existe
  `androidApp/google-services.json`** (comentado en el fichero, con el porqué medido).
- `google-services.json` en `.gitignore` (y ya en el pre-commit); `androidApp/google-services.json.example` versionado,
  con valores que no son claves.
- `androidApp/src/debug/AndroidManifest.xml`: `firebase_crashlytics_collection_enabled=false`. Anotado en
  `docs/para-claude-md.md`: lo provocado desde un build de depuración no existe para Crashlytics.

### Verificación por criterio

| Criterio | Clase | Evidencia | Resultado |
|---|---|---|---|
| Compila y arranca sin proyecto de Firebase | Comprobación humana declarada | Ejecutor, 2026-11-11, depuración, Pixel_10_Pro: pid vivo, logcat arriba | Declarado |
| Con un proyecto (simulado con el `.example`), los plugins corren y la app arranca | Comprobación humana declarada | Mismo día: tareas de los plugins en el log de Gradle; Crashlytics se inicializa | Declarado |
| La colección está apagada en depuración y no en release | Verificable | Manifiesto fusionado: debug contiene `firebase_crashlytics_collection_enabled`; `release` y `releaseLocal`, 0 líneas | Pasó |
| Un release sin config no pasa desapercibido | Verificable | `:androidApp:verificarFirebase`: sin el fichero → exit 1 («Falta androidApp/google-services.json»); con el `.example` → 0 | Pasó |
| El `.json` real no puede entrar al repo | Verificable | `.gitignore` + el pre-commit (probado en la Fase 0 con `androidApp/google-services.json`) | Pasó (heredada sin abrir: `docs/plans/2026-11-11-f0-defensas-dia-0.md`) |

### Lo que no podrá comprobarse aquí

- **Nada contra el panel real**: no hay proyecto (condición de quien decide). La prueba «crash → reabrir → visible en el
  dashboard» queda como paso declarado no ejecutable en la lista de reparto (incremento 3).

## Revisiones
- **2026-11-11 · Auditor sin contexto (pasada de la Fase 8).** Condición de salida: sin defecto de forma → se aplica.
  1. Mediciones sin evidencia versionada → `docs/mediciones/2026-11-11-firebase-android-sin-proyecto.md`.
  2. Un build sin el fichero (el CI) no lleva Crashlytics → **diferencia declarada** en `build.gradle.kts`; tarea
     `verificarFirebase` que la lista de release exige (no se engancha al build: la plantilla tiene que compilar sin
     proyecto, condición de quien decide).
  3. (a) `firebase-app` y no `firebase-common`: el módulo que se declara hoy (la matriz: `firebase-common` es interna);
     (b) el caso «config sin el plugin de Crashlytics» → **medido: crashea** (caso 3); (c) la nota → en
     `docs/para-claude-md.md`; (d) la lista de release → el comentario dice que nace en el incremento 3; (e) el enlace de
     iOS con cinterop → se mide en el incremento 2 (hoy enlaza solo porque `iosMain` no llama a Firebase).
  4. `release` además de `releaseLocal` en el manifiesto → medido.
  5 y 5b. iOS (`configure()` sin plist, SwiftPM, dSYM) y el reporter sin Firebase inicializado → incrementos 2 y 3.
  6. Listas de release y de reparto → incremento 3 (diferidas, declarado).
- 2026-11-11 · **La puerta paró la entrega**: con GitLive en `commonMain`, `linkDebugTestIosSimulatorArm64` falla
  (`ld: framework 'FirebaseCore' not found`). La medición de antes solo compiló iOS, no enlazó sus tests. GitLive pasa a
  `androidMain`; iOS por Swift.

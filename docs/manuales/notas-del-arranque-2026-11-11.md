# Notas del arranque · 2026-11-11

Primera prueba de los dos manuales («Cómo se sabe si este manual funciona»): un agente (Claude Code) construyó la
plantilla con el manual de arranque v2 y el de operación v1. Aquí van, por fase, **lo que el manual no dijo** (preguntas),
**lo que se hizo distinto** de un prompt por la regla «gana lo que encuentras», y **lo que quien decide tuvo que hacer**.
Cada nota entra en la versión siguiente del manual: como texto en el prompt de su fase, o como ranura.

Marcas: [medido] se corrió en este proyecto · [leído] está en el código o en un documento · [sin medir] propuesta.

## Transversal

1. **Falta la ranura «es una plantilla»** (v2, «Primera página»; operación, «Las ranuras»). La ruta crítica, el activo
   crítico y dónde vive no existen en una plantilla sin dominio; la escala de peso (§5) y las paradas (§6) los suponen.
   Se rellenaron como «no aplica: lo rellena cada app derivada». Propuesta: una ranura «tipo de proyecto» (app ·
   plantilla) que diga qué secciones aplazan.
2. **La autorización por incremento choca con un encargo autónomo.** El ciclo exige `Implementación autorizada: Sí` por
   plan; quien decide autorizó el arranque entero de una vez. Se registró el mensaje literal
   (`docs/mediciones/2026-11-11-autorizacion-del-arranque.md`) y cada plan lo cita. Propuesta: una forma escrita de
   «autorización de alcance» (granularidad: el arranque) en el manual de operación §2.
3. **El auditor «una vez por fase» no se corrió en las Fases 0 y 6.5** (omisión del ejecutor) y **los planes no se movieron
   al histórico tras fusionar hasta la Fase 9** (omisión, corregida allí). Ninguna de las dos reglas tiene verificador:
   las dos se saltaron. Propuesta: un paso de `cola.py` o de un `planes.py` que falle si en `docs/plans/` queda un plan con
   «Estado: Implementado» cuya rama está fusionada, y una marca «pasada del auditor» que `tope_plan.py` exija en cada plan.
4. **El ayudante de entrega entregó una vez con la puerta roja** (PR #20, Fase 6.5) antes de existir `--commit`. Desde
   `--commit` no puede pasar. Propuesta: en v2, que el sustituto de la puerta (§0, «Plan antes de aplicar») diga
   «ningún PR sin los cuatro verdes pegados en su descripción».
5. **Partición de §0.1**: la Fase 7 cupo en 2 PR y no en 5 (cada PR gasta minutos de CI); la Fase 10 en 1 y no en 3 (sus
   piezas se prueban juntas). La Fase 0 hizo el catálogo después de las defensas, la Fase 1 y la 2.1 (esperaba la tabla
   de versiones, que tardó ~8 min de búsqueda). Propuesta: decir en §0.1 qué incrementos no dependen de la tabla.

## §1 · Versiones

6. **Ktorfit ya no fija Kotlin** [leído: `KtorfitCompilerSubPlugin.kt`]: su plugin de Gradle elige compilador por
   «Kotlin ≥ 2.4». La lección de ABI sigue valiendo, pero «si Ktorfit exige una versión exacta» es de otra época.
7. **KSP ya no va emparejada con Kotlin** desde 2.3.0 [leído]: «KSP emparejada con esa Kotlin» confunde. Y la última KSP
   tenía una regresión publicada: el prompt debería pedir mirar los issues abiertos de la última versión, no solo su
   número.
8. **No hay material3 multiplataforma estable** en la línea de CMP 1.12 [leído]: «últimas estables» no siempre existe.
9. **No hay detekt estable para Kotlin 2.4** [leído]; la alpha funciona [medido] (`#PP2`).

## Fase 0

10. **El wizard crea `main`, no `master`** [medido]. El manual dice `master` en las Fases 0 y 7. Propuesta: «la rama de
    versiones (`main` o `master`)».
11. **El pre-commit de la Fase 0, escrito como dice el prompt, no podía fallar** [medido]: guardar `git diff -z` en
    `$(…)` elimina los NUL. Propuesta: que el prompt pida su brazo rojo en el mismo commit (ya lo pide la 6.5 para los
    verificadores, no la 0 para el hook).

## Fase 2

12. **`runBlocking` no existe en `commonTest`** [medido]: los tests con `MockEngine` + `runBlocking` van en
    `androidHostTest` (y en `iosTest`). El prompt no lo dice.

## Fase 4

13. **El wizard deja `versionName = "1.0"` y `MARKETING_VERSION = 1.0`**, que no son `x.y.z`: con el `isValid` estricto
    del prompt, el force-update NUNCA saltaría [medido por el auditor, corregido a 1.0.0]. Propuesta: el prompt de la
    Fase 4 pide subirlas a `x.y.z`.
14. **El stub de iOS no puede ser «solo debug» como en Android**: iOS no tiene variantes de Gradle, así que
    `ktor-client-mock` viaja en el framework de release y lo apaga Swift [leído]. Excepción declarada.
15. **Registrar ViewModels desde `core/di` rompe «nada depende de `ui`»** [leído]. Falta el concepto de **raíz de
    composición** (aquí, el paquete raíz). Propuesta: añadirlo a la Fase 1.
16. **Un `null` JSON en un campo `JsonElement?` llega como `null` de Kotlin, no como `JsonNull`** [medido con una
    mutación]: una rama de mapper que el auditor marcó como inalcanzable lo era.
17. **Nueva trampa para el catálogo (§4): `NSLog` variádico con `String` de Kotlin → `EXC_BAD_ACCESS`** [medido]. Solo
    la cazó arrancar la app en iOS en un escenario de fail-open: los cuatro verdes estaban en verde.

## Fase 6

18. **Probar release en local exige firmar**, y el manual no dice cómo. Se creó la variante `releaseLocal` (firma de
    depuración, nunca se distribuye) [leído]; firmar `release` con la de depuración haría que un CI la publicara.
19. **`commonMain` necesita saber si es depuración** para el andamiaje de la Fase 6 C: `AppBuildInfo(isDebug)` aportado
    por cada plataforma. El prompt de C no lo dice.
20. **`adb shell am crash` sí pasa por el handler no capturado** [medido]: sirve para la prueba de release.
21. **El reset por vida sana «una vez por proceso» necesita un test que llame dos veces después del primer reset**: con las
    dos llamadas juntas, la mutación de la guarda sigue verde [medido].

## Fase 6.5

22. **El ktlint de spotless no lee `.editorconfig`** [medido]: la autoridad va en `editorConfigOverride`, que es justo lo
    que el prompt pide («la autoridad en `build.gradle.kts`»); conviene decir que sin eso el `.editorconfig` no hace nada.
23. **detekt en KMP corre por source set** (`detekt<SourceSet>SourceSet`); la tarea `detekt` sola no cubre todo, y analiza
    el código generado (Ktorfit, recursos) si no se excluye `build/` por ruta absoluta [medido].
24. **detekt cuenta los comentarios para `MaxLineLength`; ktlint no** [medido]: 12 comentarios largos aparecieron al
    meter detekt.
25. **La puerta mejoró dos huecos del origen** [medido]: borra los XML antes de correr (el recuento ya no lee una
    ejecución anterior) y falla si una tarea que compila o testea sale `UP-TO-DATE`/`FROM-CACHE` (`corrio.sh`). Y corre
    los tests de iOS en el simulador. Propuesta: subirlo a v2.
26. **Una guarda nueva de la puerta dio un falso rojo** (casaba con `preAndroidHostTestBuild`, una tarea sin acciones).
    Propuesta: que el prompt de la puerta exija también el brazo de control (algo que NO debe ponerla roja).
27. **`--commit`: «ya rastreado» es ambiguo** [medido]: un fichero que un intento rechazado dejó en el índice no está
    rastreado en `HEAD`. Se resolvió como «presente en HEAD».

## Fase 7

28. **Subir workflows exige el permiso `workflow` del token** [medido]: sin él, GitHub rechaza el push de
    `.github/workflows/`. El manual no lo menciona; los workflows esperan en `config/ci-pendiente/` (`#PP6`).
29. **`gradle/gradle-daemon-jvm.properties` del wizard pide Azul 21**, no Temurin 17 [leído].
30. **`limpia-diaria` no aplica con runners alojados**: su motivo (verificar fuera de la máquina) ya lo cumple el CI.

## Fase 8

31. **«El plugin de Crashlytics es obligatorio desde el primer build» solo es cierto con config** [medido: sin
    `google-services.json` no se inicializa nada y la app arranca; con config y sin el plugin, crashea]. Con «sin
    proyecto» (condición de quien decide), los dos plugins se aplican juntos y solo con el fichero.
32. **GitLive en `commonMain` rompe el enlace de los tests de iOS** (`ld: framework 'FirebaseCore' not found`)
    [medido]. Se dejó GitLive en Android y el reporter de iOS en Swift (patrón de la trampa 12). Propuesta: el prompt de
    la Fase 8 debería contemplarlo, o decir cómo dar los frameworks a los tests de Kotlin/Native.
33. **El módulo de GitLive para inicializar es `firebase-app`**, no `firebase-common` (interno) [leído].
34. **Xcode 16 usa grupos sincronizados**: un fichero copiado desde Finder a `iosApp/iosApp/` entra solo en el target, así
    que «copiar desde Finder, nunca Add Files» se cumple sin más [leído].
35. **Un `GoogleService-Info.plist` con valores de ejemplo hace crashear iOS** al arrancar (FirebaseInstallations valida
    la clave) [medido]; en Android solo avisa.

## Fase 9

36. **El prompt no fija los umbrales de `claude_md.py`**; se tomaron del fichero de partida (54 reglas, máximo 4, media
    1,78 → umbrales 3 y 2,0), escritos con su fecha en el guion.

## Fase 11

37. **La validación en tres pasos exige una sesión nueva**, y la de la CLI había caducado [medido]: no pude medir
    (`#PP7`). **Y `validaciones.py` en la puerta bloquearía todo commit** hasta que alguien valide: es un huevo y la
    gallina. Se dejó fuera de la puerta con su condición de entrada. Propuesta: que v2 diga cómo arranca esa guarda (por
    ejemplo, entrar en la puerta en el mismo PR que la primera validación pasada).
38. **`skills_de_cierre.py` necesita que la lista de cierre nombre las herramientas**; la plantilla de plan de la Fase 0
    decía «auditoría visual (ranura)» sin nombrarlas, y el verificador salía verde con cero nombradas [medido].

## Fase 12

39. **Los hooks en el frontmatter de un agente** (`hooks:` en `.claude/agents/*.md`) se usaron según la documentación de
    Claude Code [sin medir]: la prueba del agente entero quedó en `#PP7`.

## Lo que quien decide tuvo que hacer o decidir

- Conceder el permiso `workflow` al token (`#PP6`) e iniciar sesión en la CLI (`#PP7`).
- Aprobar los textos y las acciones por code (`#PP5`), y la salida de la pantalla de actualización obligatoria.
- Aprobar (o no) la fase B de la auditoría integral.

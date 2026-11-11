# La cola

**El orden del trabajo vive aquí y en ningún otro sitio.** Esta cabecera no lleva la cuenta de qué cambió: eso está en
`git log`. «Qué va ahora» es la vista que genera `config/checks/que_va_ahora.py`. Cómo se escribe, se pesa y se cierra
una entrada: manual de operación §5 (`docs/manuales/kmp_manual_de_operacion_v1.md`). Los identificadores (`#PPn`) no se
reutilizan nunca: lo comprueba `config/checks/cola.py` contra el historial.

<!--
Plantilla de una entrada (copiar bajo «Abiertos»):

### #PPn · AAAA-MM-DD · <qué se rompe, no un área> <marcas: 🔴|🟡|(requisito de salida)|(funcionalidad)|(higiene)|(peso por decidir)|(por medir)|(por partir)>

- **Mecanismo:** <uno solo; si hay varios, son varias entradas>
- **Quién lo desbloquea:** nosotros | quien decide | otro equipo
- **Disparador:** cuando <evento observable> — estado medido: <…, con fecha> | **Fecha de revisión:** AAAA-MM-DD
- **Vigente hasta que:**
  1. <condición de cierre>
  2. <…>
- **Peso:** <firma de quien decide> | (peso por decidir) — propuesta: (a) … (b) … (c) … (d) … ; sistema donde ocurre el daño: …
-->

## Abiertos

### #PP2 · 2026-11-11 · No hay detekt estable que analice Kotlin 2.4 (higiene)

- **Mecanismo:** detekt 1.23.8 (estable) está construido con Kotlin 2.0.21; el soporte de 2.4 solo existe en
  2.0.0-alpha.6 [leído: la medición de la matriz].
- **Quién lo desbloquea:** otro equipo (detekt).
- **Disparador:** cuando detekt publique una 2.0.0 estable — estado medido 2026-11-11: no publicada. **Fecha de
  revisión:** 2027-01-11.
- **Vigente hasta que:**
  1. el proyecto usa un detekt estable que analiza Kotlin 2.4, o
  2. quien decide acepta a conciencia la alpha (y la entrada pasa a «Aceptado a conciencia» con su firma).
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): cuatro «no»; solo lo ve el equipo.

### #PP3 · 2026-11-11 · Los iconos de la barra de estado de Android no se leen sobre el fondo claro (por medir)

- **Mecanismo:** con `enableEdgeToEdge()` y el tema `Theme.Material.Light.NoActionBar` del manifiesto, la barra de
  estado pinta iconos claros sobre la superficie clara de la app [medido 2026-11-11: captura
  `docs/mediciones/2026-11-11-app-vacia-android.png`, build de depuración, commit `00b30dd`]. El mecanismo exacto
  (estilo de la barra frente al tema) está **sin medir**.
- **Quién lo desbloquea:** nosotros.
- **Disparador:** cuando se cree el tema de la app (etapa de producto, «design system y tokens de marca») — estado
  medido 2026-11-11: no existe tema propio.
- **Vigente hasta que:**
  1. una captura de Android en tema claro y oscuro muestra los iconos de la barra de estado legibles.
- **Peso:** (peso por decidir) — propuesta: 🟡: (a) no; (b) no; (c) no en la ruta crítica (la hora y la batería no son
  necesarias para recorrerla); (d) no; alguien queda peor (no lee su barra de estado dentro de la app).

### #PP4 · 2026-11-11 · Android Studio no ofrece simuladores iOS para correr `iosApp` (higiene)

- **Mecanismo:** el plugin Kotlin Multiplatform de Android Studio (261.25134.80-AS) lanza un `NullPointerException` en
  `SimulatorDeviceType.intFromNumberOrString` al leer los simuladores, deja la lista vacía y Run deshabilitado
  [leído: `idea.log` de Android Studio, 7 apariciones el 2026-11-11]. Disparador probable, **sin medir**: runtimes
  iOS 27.2 registrados pero no disponibles.
- **Quién lo desbloquea:** otro equipo (JetBrains) o quien decide (limpiar runtimes en Xcode).
- **Disparador:** cuando se actualice el plugin o se limpien los runtimes — **Fecha de revisión:** 2026-12-11.
- **Vigente hasta que:**
  1. Android Studio lista un simulador y corre `iosApp`. Mientras tanto, iOS se corre desde Xcode.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

### #PP5 · 2026-11-11 · Los textos de la Splash y las acciones por code no están aprobados (requisito de salida)

- **Mecanismo:** el ejecutor propuso el texto de producto de `shared/src/commonMain/composeResources/values/strings.xml`
  (Splash: carga, actualización obligatoria, error, Reintentar, «Inicio») y las acciones por code de
  `docs/contracts/api-errors.md`; su versión final es de quien decide (manual de operación §6).
- **Quién lo desbloquea:** quien decide.
- **Disparador:** cuando quien decide revise los textos — estado medido 2026-11-11: no revisados. **Fecha de revisión:**
  antes de la primera versión publicada.
- **Vigente hasta que:**
  1. quien decide aprueba o cambia cada texto de `strings.xml` (incluida la copy de cada `ErrorKind` y los
     placeholders de Home y Detail) y los tres de `CrashSafeScreen` (escritos en el código), con su firma;
  2. quien decide aprueba o cambia la acción por code de `api-errors.md`;
  3. quien decide dice qué salida ofrece la pantalla de actualización obligatoria (hoy ninguna: no hay URL de tienda).
     Hasta entonces la persona queda sin salida dentro de la app, que es lo que «bloqueante» pide.
  4. la copy de `NETWORK` («No hay conexión…») afirma una causa que no siempre es la real: también cae ahí un servidor
     que no resuelve (medido 2026-11-11: build `releaseLocal` contra `api.example.invalid` muestra «No hay conexión»
     con la red del emulador funcionando). Manual de operación §10: el texto no afirma lo que no sabe.
- **Peso:** requisito de salida (fuera de escala): no daña a nadie hoy; impide publicar.

### #PP6 · 2026-11-11 · El CI no existe en el remoto: el token no puede subir workflows (requisito de salida)

- **Mecanismo:** GitHub rechaza crear `.github/workflows/*` con un token OAuth sin el permiso `workflow` [medido
  2026-11-11: push de `f7/ci` rechazado con ese mensaje]. `ci.yml` y `.github/actions/calidad/action.yml` están escritos
  y auditados (`docs/historico/planes/2026-11-11-f7-ci.md`). **2026-11-11: quien decide concedió el permiso**
  (`gh auth status`: `workflow`); suben en `f7/subir-ci` con las condiciones 3–5 aplicadas.
- **Quién lo desbloquea:** quien decide: `gh auth refresh -h github.com -s workflow` (paso interactivo en el navegador).
- **Disparador:** cuando el token tenga el permiso `workflow` — estado medido 2026-11-11: no lo tiene.
- **Vigente hasta que:**
  1. `ci.yml` y la acción compuesta están en `develop`, y
  2. un PR los ha visto correr: `android`, `calidad` e `ios` en verde, con los verdes ejecutados (`corrio.sh`).
- **Añadido 2026-11-11 (auditoría integral, `docs/historico/auditorias/AUDIT-2026-11-11.md`; mismo mecanismo: el
  contenido del CI aún no ejecutado; el peso no cambia):**
  3. los tres verdes de `android` en UNA invocación de Gradle (o con `--rerun-tasks`): separados, `testAndroidHostTest`
     encuentra `UP-TO-DATE` lo que compiló `assembleDebug` y `corrio.sh` sale 1 siempre (P0-1, [leído]);
  4. la acción `calidad` corre los mismos verificadores que la puerta salvo `skills_de_cierre.py` (P1-6);
  5. `alcance` escribe en `$GITHUB_OUTPUT` solo la línea `solo_docs=…`, e `ios` lleva `!cancelled()` (P2-10).
- **Peso:** requisito de salida (fuera de escala): sin CI nada verifica fuera de la máquina de trabajo antes de una versión.

### #PP7 · 2026-11-11 · Las skills y commands del proyecto no están validados en tres pasos (requisito de salida)

- **Mecanismo:** la validación (positiva, control, negativa) exige una sesión NUEVA de Claude Code; la lanzada con
  `claude -p` respondió «Failed to authenticate: OAuth session expired and could not be refreshed» [medido 2026-11-11].
  Las cuatro filas de `.claude/validaciones.md` están en «no pude medir», y por eso `validaciones.py` no está todavía en
  la puerta (saldría rojo y bloquearía todo commit).
- **Quién lo desbloquea:** quien decide (iniciar sesión en la CLI, o correr la validación en una sesión nueva).
- **Disparador:** cuando la CLI tenga sesión válida — estado medido 2026-11-11: caducada.
- **Vigente hasta que:**
  1. las cuatro filas tienen sus tres pasos en «pasó», sobre el hash actual de cada fichero, y
  2. `validaciones.py` está en `VERIFICADORES` de la puerta.
- **Peso:** requisito de salida (fuera de escala): una skill que no se activa no ayuda a nadie, y sin validar no se sabe.

### #PP8 · 2026-11-11 · El APK de release exporta `PreviewActivity` de ui-tooling (peso por decidir)

- **Mecanismo:** `shared/build.gradle.kts` declara `compose.uiTooling` en `androidMain` y en `androidRuntimeClasspath` (heredado del wizard); el manifiesto fusionado de `release` lleva `PreviewActivity` con `exported="true"` [medido por la auditoría]. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. quitar las dos líneas (androidApp ya lo tiene como `debugImplementation`) y ver que el manifiesto de release no la lleva; las previews siguen funcionando.
- **Peso:** (peso por decidir) — propuesta: 🟡: (a) no; (b) no; (c) no; (d) no hay activo crítico en la plantilla, pero cualquier app del dispositivo puede lanzar composables: alguien queda peor.

### #PP9 · 2026-11-11 · `puerta.sh --commit` busca marcadores de conflicto en un índice viejo (peso por decidir)

- **Mecanismo:** el paso f) hace `git grep --cached` ANTES del `git add -u` del commit [leído]: un marcador en un fichero no indexado entra con «PUERTA VERDE». Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. en modo `--commit`, el paso f) mira lo que se va a commitear, con una mutación que lo pruebe por ese camino.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): solo lo ve el equipo.

### #PP10 · 2026-11-11 · El breaker no corta un bucle de crash lento en Android (peso por decidir)

- **Mecanismo:** la ventana deslizante de 10 s anclada al primer crash exige 3 crashes en 10 s; con relanzamiento ~1 s y un crash a T s del arranque, solo se dispara si 2T+3 ≤ 10 (T ≲ 3,5 s) [leído y derivado; sin medir en dispositivo]. Toca la conducta del breaker: [DECISIÓN-DE-PRODUCTO] Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. un bucle relanzar-crashear con crash a los 5 s acaba en la pantalla segura (test primero y prueba en `releaseLocal`), con la decisión de quien decide sobre cómo (disparo hasta `reset()` o ventana mayor).
- **Peso:** (peso por decidir) — propuesta: 🔴 propuesto: (b) quien usa la app queda sin salida en la ruta crítica de la plantilla (el arranque).

### #PP11 · 2026-11-11 · Crashlytics de iOS recibiría todos los crashes como un único error sin traza (peso por decidir)

- **Mecanismo:** `SwiftCrashReporter` registra un `NSError` con dominio fijo, `code: 0` y solo el mensaje [leído; sin medir: no hay proyecto]. [TOCA-iOS] Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. el reporter de iOS manda un `ExceptionModel` con los frames de la pila de Kotlin, y un crash de prueba en un proyecto real se ve agrupado por su causa.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): lo ve el equipo en el panel.

### #PP12 · 2026-11-11 · Nada impide que el stub de config vuelva a entrar en release de Android (peso por decidir)

- **Mecanismo:** «el stub nunca en release» se midió una vez (`releaseRuntimeClasspath`), sin guarda continua [leído]. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. un paso de la puerta (o del CI) falla si `ktor-client-mock` aparece en `releaseRuntimeClasspath`, con su mutación.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

### #PP13 · 2026-11-11 · Las variantes release no se compilan en ninguna verificación (peso por decidir)

- **Mecanismo:** `VERDES` solo tiene `assembleDebug`: `src/release`, `releaseLocal` y la rama `#else` de `iOSApp.swift` no se compilan en la puerta ni en el CI [leído]. [TOCA-iOS] Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. la puerta compila la variante release de Android y hay una medición de `xcodebuild -configuration Release`.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

### #PP14 · 2026-11-11 · Un segmento de versión más largo que un `Int` se compara como 0 (peso por decidir)

- **Mecanismo:** `SemanticVersionComparator` usa `toIntOrNull() ?: 0` y `isValid` acepta `\\d+` de cualquier longitud [leído]: un `minVersion` con un segmento enorme desactiva el force-update sin aviso. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. test primero con un segmento de más de 10 dígitos; la comparación lo ordena bien.
- **Peso:** (peso por decidir) — propuesta: 🟡 propuesto: (b) fuera de la ruta crítica una versión vieja no bloquea cuando debía.

### #PP15 · 2026-11-11 · El recuento de tests no exige resultados de cada plataforma (peso por decidir)

- **Mecanismo:** `recuento_tests.py` suma el total; un `iosSimulatorArm64Test` saltado pasaría con los tests JVM [leído; sin medir que ocurra]. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. el recuento falla si falta XML de `testAndroidHostTest` o de `iosSimulatorArm64Test`, con su mutación.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

### #PP16 · 2026-11-11 · Hay versiones copiadas fuera del catálogo (peso por decidir)

- **Mecanismo:** `CLAUDE.md` §3 nombra la versión de detekt; el `pbxproj` y `Package.resolved` fijan Firebase iOS [medido por la auditoría con `git grep`]; la regla §3 de `CLAUDE.md` dice «nunca las copies». Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. `CLAUDE.md` sin números de versión, y el `pbxproj` declarado como excepción (SwiftPM no lee el catálogo de Gradle).
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

### #PP17 · 2026-11-11 · El guard de ficheros protegidos no ve las escrituras por Bash (peso por decidir)

- **Mecanismo:** su matcher es `Edit|Write|MultiEdit` [leído]: un comando de shell puede escribir `local.properties` sin pasar por él. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. el guard cubre también `Bash` (heurística) o la limitación queda declarada en `CLAUDE.md` §13.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

### #PP18 · 2026-11-11 · El filtro del auditor deja pasar `gh` entre comillas (peso por decidir)

- **Mecanismo:** `'gh' pr list` no casa con el patrón de palabra [leído]. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. el filtro deniega `gh` entrecomillado, con su caso en `hooks_test.sh`.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

### #PP19 · 2026-11-11 · El README es el del wizard (peso por decidir)

- **Mecanismo:** cita `jvmMain` (no existe) y no dice nada de `CLAUDE.md`, la puerta ni `git config core.hooksPath .githooks` [leído]. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. el README explica qué es la plantilla, cómo se arranca un clon (hook) y dónde están los manuales.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

### #PP20 · 2026-11-11 · `.gitignore` no ignora keystores (peso por decidir)

- **Mecanismo:** solo el pre-commit los rechaza; `*.jks` y `*.keystore` no están en `.gitignore` [leído]. Hallazgo de `docs/historico/auditorias/AUDIT-2026-11-11.md`; búsqueda previa del mecanismo en la
  cola: sin entrada.
- **Quién lo desbloquea:** nosotros, tras la aprobación de la fase B por quien decide.
- **Fecha de revisión:** cuando quien decide apruebe la fase B de la auditoría.
- **Vigente hasta que:**
  1. `.gitignore` los ignora.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene).

## Cerrados

| Id | Qué era | Commit que la cerró |
|---|---|---|
| `#PP1` | El compilador de Ktorfit podía no compilar contra Kotlin 2.4.21. Condición 1 cumplida: Ktorfit corre en JVM y en el simulador (`ConfigRepositoryImplTest`, `ConfigRepositoryIosTest`) | `2b95a60` |

## Aceptado a conciencia

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

### #PP1 · 2026-11-11 · El compilador de Ktorfit puede no compilar contra Kotlin 2.4.21 (por medir) (higiene)

- **Mecanismo:** el plugin de Gradle de Ktorfit 2.7.5 elige el plugin de compilador 2.3.5 para toda Kotlin ≥ 2.4; ese
  binario se construyó contra Kotlin 2.4.0, y el arreglo para 2.4.21 (Foso/Ktorfit PR #1100) no está publicado
  [leído: `docs/mediciones/2026-11-11-matriz-de-versiones.md`].
- **Quién lo desbloquea:** nosotros (medirlo); otro equipo (Ktorfit) si falla.
- **Disparador:** cuando el incremento «DTO→repositorio» de la Fase 4 compile y ejecute la primera interfaz Ktorfit en
  Android e iOS — estado medido 2026-11-11: no ocurrido. (Movido desde la Fase 2 por la decisión de
  `docs/plans/2026-11-11-f2-cliente-red.md`, «Solución y decisiones», «Ktorfit»: la Fase 2 no crea ninguna interfaz.)
- **Vigente hasta que:**
  1. una llamada Ktorfit generada corre en un test JVM y la app iOS compila con ella, o
  2. si falla: Kotlin se baja al baseline que Ktorfit soporte, o se fija `compilerPluginVersion` a uno publicado que sí.
- **Peso:** (peso por decidir) — propuesta: fuera de escala (higiene): (a) no, (b) no, (c) no, (d) no; solo lo ve el
  equipo, en el build.

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

## Cerrados

| Id | Qué era | Commit que la cerró |
|---|---|---|

## Aceptado a conciencia

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
  1. quien decide aprueba o cambia cada texto de `strings.xml`, con su firma;
  2. quien decide aprueba o cambia la acción por code de `api-errors.md`;
  3. quien decide dice qué salida ofrece la pantalla de actualización obligatoria (hoy ninguna: no hay URL de tienda).
     Hasta entonces la persona queda sin salida dentro de la app, que es lo que «bloqueante» pide.
- **Peso:** requisito de salida (fuera de escala): no daña a nadie hoy; impide publicar.

## Cerrados

| Id | Qué era | Commit que la cerró |
|---|---|---|
| `#PP1` | El compilador de Ktorfit podía no compilar contra Kotlin 2.4.21. Condición 1 cumplida: Ktorfit corre en JVM y en el simulador (`ConfigRepositoryImplTest`, `ConfigRepositoryIosTest`) | `2b95a60` |

## Aceptado a conciencia

# Revisión externa del 2026-11-12: decisiones literales (ficha del plan `2026-11-12-revision-externa-v3`)

- **Fecha:** 2026-11-12 (America/Caracas) · **De:** la revisión externa de Rumba (el proyecto del que nacieron los
  manuales), que quien decide pegó en la conversación con Claude Code en dos mensajes.
- **Commit de partida:** `ead1b3c`.
- Se copian **literales** las partes que deciden. Se omiten las preguntas B1–B3 (contestadas en la conversación) y la
  lista C, que pide candidatos para Rumba y no cambia la plantilla. De los dos mensajes, solo se quitaron los marcos de
  `====`.

## Mensaje 1 · A, aprobado para la v3 de los manuales

> A1. LA AUTORIZACIÓN DE ALCANCE PASA A SER LA PRIMERA, no la tercera.
> No es comodidad: es el papel separándose por una grieta. Si quien decide autoriza en la conversación y el manual pide
> autorizar después del auditor, o la autorización es provisional o la pasada del auditor es teatro. Medido ayer en Rumba
> (#RS1): autoricé el alcance en la ficha, el auditor pasó después, y su hallazgo 1 CAMBIÓ MI DECISIÓN —una pieza creció
> porque lo que conservaba no se veía en ninguna pantalla—. Era provisional. La forma escrita, literal, en la plantilla
> del plan:
> «Alcance autorizado, provisional hasta la pasada del auditor.»
> «Lo que el auditor cambió del alcance: …»
> Dos líneas y la grieta se cierra.
>
> A2. EL CARRIL LIGERO, SÍ, con una frontera: se salta el plan y el auditor, y NO SE SALTA LA REVISIÓN.
> Ayer en Rumba hicimos tres PR de solo documentación con el ciclo entero y fue caro, así que tu propuesta es correcta.
> Pero el único crítico de esos tres lo cazó la revisión EN UN PR DE SOLO DOCUMENTACIÓN: un absoluto —«el resto del
> documento sigue valiendo»— sobre filas que esa rama no había comprobado. Un documento afirma cosas falsas igual que el
> código, y la revisión es lo único que lo mira.
>
> A3. `plan_lint.py` EN LA PUERTA, aprobado. Y añade en el manual el segundo motivo, que no está en tu reporte: cuando
> todos los hallazgos del auditor son de fondo, sus hallazgos se creen más. Hoy un hallazgo de forma y uno de fondo se
> leen igual en la lista, y los de forma diluyen la atención.
>
> A4. LA ESCALA DE PESO SE ACOTA, NO SE QUITA. Tu propuesta de quedarte con «requisito de salida / 🟡 / higiene» tiene
> razón a medias, y la mitad que falta la medí ayer: usé las cuatro preguntas siete veces. En cuatro salieron «no, no,
> no, no» y la entrada se fue fuera de escala — ahí es ceremonia y tenías razón. En las otras tres hicieron trabajo: en una
> me obligaron a mirar si la pregunta (a) aplicaba y así cacé que una premisa MÍA era falsa; en otra sospeché la (c) y la
> medición la desmintió; y un 🔴 sale de la (a) directamente.
> Así que en el manual: **las cuatro preguntas solo para lo que toca el camino del dinero; para todo lo demás, la escala
> de tres.** Fuera de ese camino siempre salen cuatro «no».
>
> A5. LA VALIDACIÓN EN TRES PASOS, aprobada, solo para piezas nuevas o cambiadas, con su método escrito. Y añade este
> hecho mecánico, que ayer nos costó dos intentos en Rumba: **la definición de un agente se carga al arrancar la
> sesión**, así que editar su fichero a mitad no la cambia. Cualquier validación de un agente necesita una sesión nueva,
> y el paso de control también.
>
> A6. SIN NÚMEROS DE VERSIÓN EN LOS PROMPTS, aprobado, y escríbelo como la forma general y no como una lista de casos:
> **un prompt que nombra una versión es un prompt con fecha de caducidad.** Se nombra la restricción, no el número.
>
> A7. UNA PÁGINA CON EL CICLO, aprobada. Eugenio lo ha dicho por su cuenta dos veces: el volumen de planes y auditorías le
> resulta abrumador. Una página que remita al detalle.
>
> A8. EL zsh PASA DE RECOMENDACIÓN A REGLA en `CLAUDE.md`: los comandos que manejan listas de ficheros o de tareas, en
> bash. Son dos errores tuyos ahí y dos en Rumba, el mismo día y el mismo mecanismo —una variable con varias rutas sin
> partir—. Cuatro instancias en dos repositorios es medición, no anécdota.
>
> A9. EL `revisor-externo`: que la ranura exista es buen diseño, porque una app derivada puede no tener un chat guardando
> las decisiones. Pero **nómbralo por lo que es.** Lo que hace la revisión externa que un agente dentro del repo no puede
> hacer es NO PODER LEER EL CÓDIGO; el valor del papel depende de eso, igual que el del auditor depende de su filtro — y
> ayer descubrimos que la descripción del auditor prometía más de lo que su filtro daba. Si `revisor-externo` tiene acceso
> al repositorio, es un segundo auditor con otro modelo. Vale, pero que `CLAUDE.md` lo diga así, o la ranura queda llena
> con algo que se le parece. Tu recomendación de estrenarlo en la primera feature de una app derivada se queda: no puedo
> juzgar lo que no ha corrido.
>
> Lo que NO apruebo, para que quede escrito: quitar las cuatro preguntas de la escala (se acotan, A4) y que el carril
> ligero se salte la revisión (A2).

## Mensaje 2 · Decisiones 1 a 7

> 1. `manuales.py` NO se escribe sobre una reconstrucción. Espera a la v3, y sus criterios salen de una PASADA NUEVA DEL
> AUDITOR SOBRE LA V3. Auditar los criterios de la v2 sería verificar un documento a punto de cambiar. Y mi decisión del
> 2026-11-09 —«C1, C2, C4, C5 y C6 abiertos hasta que manuales.py salga verde»— queda SUPERADA, con esa razón escrita:
> eran criterios de la v2 y su definición no está en ningún sitio legible. La escribí sin comprobar que sus criterios
> pudieran leerse; eso también va escrito.
>
> 2. `#PP8`: aprobado extender `verificarSinStubEnRelease` a `ui-tooling` y al manifiesto fusionado de release. Entra en la
> cola como verificador, no como arreglo. Y a la v3, como preferencia general: cuando ya existe un verificador de la
> clase, SE EXTIENDE; no se escribe uno nuevo al lado. Dos verificadores de la misma clase divergen.
>
> 3. `plan_lint.py` sigue adelante con el 40 % MARCADO COMO DERIVACIÓN. Su argumento no necesita la cifra: un hallazgo de
> forma y uno de fondo se leen igual en la lista, y los de forma diluyen la atención. Y lo que no se queda así: LOS
> INFORMES DEL AUDITOR SE GUARDAN, desde ya, en un fichero por pasada. Hoy la evidencia de lo que el auditor encontró la
> redacta quien aplicó los hallazgos. Rumba ya los guarda en `.claude/reviews/`: cópiale la forma.
>
> 4. A9, con lo que mediste: SE QUITA «Solo lee» de los dos agentes y se escribe lo que son — herramientas de lectura más
> `Bash`, con GitHub denegado por nombre. Y `revisor-externo` se nombra en `CLAUDE.md` §2 como un segundo auditor con otro
> modelo, no como la ranura de la revisión externa. NO amplíes el filtro para denegar escrituras por nombre: hereda el
> agujero que medimos ayer en Rumba con `git ls-remote`. Y UNA MEDICIÓN que puede dar un mecanismo de verdad: ¿puede el
> auditor hacer su trabajo SIN `Bash`? Si le bastan Read, Grep y Glob, quitárselo es una frontera real en vez de una
> instrucción. Si necesita `git show`, dilo y nos quedamos con la descripción honesta.
>
> 5. ESCRIBE LA v3 con A1–A9 del encargo anterior, más lo de arriba. Ninguna de las 39 notas la contradice, así que no hay
> nada que reconciliar.
>
> 6. Dos cambios en `CLAUDE.md`: la regla de bash para listas de ficheros o tareas (A8) y lo de A9.
>
> 7. Y arregla tu propia laguna del punto 2 de la lista: `hooks_test.sh` no prueba `stop-verificadores.sh` sin `python3`.
> Es la misma clase que el hallazgo que Rumba cerró ayer.
>
> NO TOQUES RUMBA. Lo que se trae allá lo encargo yo en su sesión, y el orden ya está decidido: `validaciones.py` primero,
> luego la convención del 3 sobre su guion de disparadores, el patrón del hook sin su herramienta, y las pruebas del
> filtro. `entregables.py` no se trae: resuelve un problema de arranque y Rumba ya arrancó.

## Lo que se midió para contestar (en la conversación, 2026-11-12)

- Los manuales viven en `docs/manuales/` y, fuera del repositorio, en el escritorio de quien decide, idénticos byte a
  byte (`cmp`) [medido].
- `manuales.py` no existe ni lo sigue nadie: no está en la cola, en los entregables ni en las notas [medido: `grep`].
- `#PP8` no tiene verificador: `git grep PreviewActivity` en `config/`, `androidApp/`, `shared/` y `.github/` no devuelve
  nada [medido].
- El «cerca del 40 %» es una derivación, no un recuento [dicho por el ejecutor].

# Encargo de la revisión externa: los commands, la regla nueva y su verificador (ficha)

- **Fecha:** 2026-11-12 (America/Caracas) · **De:** la revisión externa (Rumba), pegado por quien decide en la conversación.
- **Commit de partida:** `329be68`. Se copia literal y solo se quitan los marcos.

> 1. LOS DOS COMMANDS: corrígelos, con el mismo texto honesto que los agentes. Son la instancia peor de la clase: un
> command corre en la sesión principal con todas las herramientas, así que «solo lee» ahí no lo sostiene ni un filtro.
>
> 2. Y prepara UNA SOLA sesión nueva que cierre las dos cosas: la revalidación de los dos commands y la medición de #PP31
> (qué herramientas recibe de verdad el revisor, intentando usar Grep y Glob). Un bloque, seis o siete pasos, con su paso
> 0 de que la sesión cargó el texto nuevo. No lo dejes «para la próxima vez que algo caduque una validación»: eso es un
> disparador de ocasión, y están medidos 23 de 23 fallando.
>
> 3. REGLA NUEVA, decidida por la revisión externa (2026-11-12), a `CLAUDE.md` y a la v3 del manual de operación: «Toda
> pieza que declare una propiedad dice qué mecanismo la sostiene, o no la declara.» Con sus cinco instancias medidas
> citadas, dos de ellas de Rumba: no hay ambiente separado de producción, no hay protección de ramas, la descripción del
> auditor de Rumba prometía más que su filtro, los dos agentes y los dos commands.
>
> 4. Y SU VERIFICADOR, que es lo que la hace pegar: una comprobación que barra skills, commands y agentes buscando las
> frases de capacidad que ya hemos visto —«solo lee», «sin acceso a», «no modifica» y sus variantes— y exija al lado la
> línea del mecanismo. ESCRIBE SU LÍMITE: es un grep de frases, así que caza las formulaciones vistas y no toda
> afirmación posible. Falla hacia el silencio, que es lo que ya pasa hoy sin ella. Si al escribirlo resulta que la mitad
> de las piezas declaran algo sin mecanismo, PARA y tráelo antes de arreglarlas: eso sería una tanda, no un verificador.
>
> 5. Fusiona el #48 cuando su CI esté en verde sobre su cabeza, comprobándola. El #47 sigue abierto.
>
> 6. Y dime si al correr el verificador del punto 4 aparece algo que YO haya escrito en esos manuales con una propiedad
> sin mecanismo. Es lo más probable de todo este encargo.

## Lo medido antes del plan (2026-11-12)

- **Punto 5:** el #48 se fusionó (`329be68`) con el CI en verde sobre su cabeza `cad6aa7`, comprobada con
  `--match-head-commit` [medido].
- **Punto 4, el umbral de «la mitad»:** de 5 piezas, 2 declaran una propiedad sin mecanismo, los dos commands
  (`plantilla-review.md:2,9`; `plantilla-contract-sync.md:11`). Los agentes ya lo dicen al lado (`cad6aa7`), y la skill
  no declara nada [medido: `grep`]. Es menos de la mitad.
- **Punto 6:** en los manuales, sin mecanismo [leído]:
  - (a) arranque, FASE 11, el PROMPT, punto 2: «SOLO lee y escribe su informe». Es **el origen** del texto del command;
  - (b) arranque, FASE 12, PROMPT B: «FASE A (solo lectura, PROHIBIDO modificar)»;
  - (c) arranque, la tabla de sustitutos: «`<proyecto>-review` … y solo informa»;
  - (d) arranque, FASE 12, y operación §1: «un subagente que solo ve el documento y el código, sin la bandeja». Se
    sostiene en parte: «sin la conversación», por construcción del subagente; «sin la bandeja», por el filtro de GitHub
    (por nombre); «solo ve» no se sostiene, porque tiene `Bash`. Este caso no lo cazaría el verificador: «solo ve» no
    está en la lista de frases.

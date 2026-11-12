# Una lista de ficheros o tareas en una variable llega como UNA sola palabra.

- **Síntoma:** un comando recibe una sola ruta inexistente o una sola «tarea» con espacios: Gradle dice «task not
  found», `git` no encuentra el fichero, o una mutación sale «vista» porque el comando falló por mal uso, no por la guarda.
- **Causa:** zsh (el shell de este entorno) **no parte en palabras** una variable sin comillas (`$V`), a diferencia de
  bash. Medido 2026-11-11: dos veces aquí (las tareas de `VERDES` en una mutación de `#PP13`; las rutas de las capturas en
  un `puerta.sh --commit`) y dos en Rumba, el mismo día y el mismo mecanismo (revisión externa, A8).
- **Regla:** CLAUDE.md §11 «Corre en bash los comandos que manejan listas de ficheros o de tareas».

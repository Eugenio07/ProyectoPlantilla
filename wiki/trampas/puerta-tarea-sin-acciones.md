# La puerta se pone roja por una tarea que «no se ejecutó» y que no hace nada.

- **Síntoma:** La puerta se pone roja por una tarea que «no se ejecutó» y que no hace nada.
- **Causa:** `preAndroidHostTestBuild` es una tarea de ciclo de vida sin acciones, siempre `UP-TO-DATE`; un patrón laxo la confundía con un test.
- **Regla:** CLAUDE.md §4 «`corrio.sh` mira el inicio y el fin del nombre de la tarea».

# Un hook de git que nunca rechaza nada.

- **Síntoma:** Un hook de git que nunca rechaza nada.
- **Causa:** `$(…)` en bash elimina los bytes NUL: una lista `-z` guardada así llega sin separadores y el bucle no itera.
- **Regla:** CLAUDE.md §11 «Lee una lista `-z` por sustitución de proceso, nunca de una variable».

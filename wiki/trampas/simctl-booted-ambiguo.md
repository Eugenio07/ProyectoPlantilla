# Una captura «del iPhone 17 Pro» es de otro simulador.

- **Síntoma:** Una captura «del iPhone 17 Pro» es de otro simulador.
- **Causa:** Con dos simuladores arrancados, `simctl … booted` resuelve a uno cualquiera (la tarea de tests de iOS arranca el suyo).
- **Regla:** CLAUDE.md §7 «Usa el UDID explícito con `simctl`».

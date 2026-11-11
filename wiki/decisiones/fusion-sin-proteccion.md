# Sin protección de ramas: nunca `gh pr merge --auto`; esperar el verde; comprobar la cabeza (`--match-head-commit`); fusionar a mano

- **Firma:** Ejecutor (la ranura de protección la fija GitHub)
- **Fecha:** 2026-11-11
- **Estado:** Vigente

**Decisión.** Sin protección de ramas: nunca `gh pr merge --auto`; esperar el verde; comprobar la cabeza (`--match-head-commit`); fusionar a mano.

**Por qué.** Repo privado en plan gratuito: la API de protección responde 403 (medido 2026-11-11).

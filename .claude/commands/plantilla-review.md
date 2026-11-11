---
description: Revisión de pre-PR de la rama actual contra CLAUDE.md. Solo lee; escribe su informe en .claude/informes/plantilla-review.md. Úsala ANTES de abrir el PR.
---

Revisa los cambios de la rama actual respecto a `develop` (`git diff develop...HEAD` y `git log develop..HEAD`) contra
las reglas de `CLAUDE.md` (léelo: es la referencia, no la copies).

Reglas de esta revisión:
- **Solo lees.** No editas ningún fichero salvo el informe.
- Cada hallazgo con `fichero:línea` (y el commit), la regla de `CLAUDE.md` que incumple (por su sección y su frase) y la
  evidencia. Marca cada afirmación medido / leído / sin medir.
- Mira en especial: la regla de dependencias de `domain` (§5), que los repositorios no lancen (§5), la config por
  plataforma con un test de grafo por plataforma (§6), el orden por capa y las mutaciones del plan (§7), la copy en
  recursos (§10), los no-hacer (§11), y que el plan de `docs/plans/` tenga su autorización y su verificación por criterio.
- Lo que no pudiste comprobar, en una sección «Lo que no pude comprobar».

Escribe el informe en `.claude/informes/plantilla-review.md` (lo sobrescribes en cada revisión) y resume en el chat
cuántos hallazgos hay y cuál es el más grave.

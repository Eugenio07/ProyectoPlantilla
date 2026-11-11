# La variante `releaseLocal` (firma de depuración) prueba en local el comportamiento de release; `release` sigue sin firmar hasta el keystore real

- **Firma:** Ejecutor, a raíz del auditor de la Fase 6
- **Fecha:** 2026-11-11
- **Estado:** Vigente
- **descartada_porque:** firmar `release` con la clave de depuración (descartada por lo de arriba).

**Decisión.** La variante `releaseLocal` (firma de depuración) prueba en local el comportamiento de release; `release` sigue sin firmar hasta el keystore real.

**Por qué.** Firmar `release` con la clave de depuración haría que un CI publicara un artefacto firmado en depuración.

---
description: Sin backend (Fase 3, rama A) - compara los DTO de shared/src/commonMain/.../data/remote/dto con los borradores de docs/contracts/ y REPORTA divergencias. Corre en la sesión principal con todas sus herramientas: no edita por instrucción, no por mecanismo.
---

Compara cada DTO de `shared/src/commonMain/kotlin/org/example/proyecto_plantilla/data/remote/dto/` con los borradores
de `docs/contracts/` (`config-endpoint.md`, `api-errors.md`):
- claves JSON (`@SerialName` incluido), tipos, nulabilidad y valores por defecto frente a lo que dice el borrador;
- claves que el borrador declara y el DTO no tiene, y al revés;
- el envelope de errores frente a `NetworkErrorMapper`.

**Reporta**: una tabla «DTO · clave · borrador · código · divergencia», con `fichero:línea`. No editas nada, por
instrucción (ningún mecanismo te lo impide). Si no
hay divergencias, dilo y di qué comparaste. Con un backend real esta herramienta se retira: la sustituye el verificador
de contrato en la puerta (v2, Fase 3, rama B o C).

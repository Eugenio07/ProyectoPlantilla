# Autorización del arranque

- **Fecha:** 2026-11-11 (America/Caracas)
- **De:** quien decide, en la conversación con Claude Code que construye la plantilla
- **Commit de partida:** `c448186`

Mensaje literal:

> Bueno, por ahora el xcode vale. Vamos a construir la plantilla. Te dejo 3 documentos, el primero por el que debes
> empezar es kmp_bootstrap_playbook_generico_v2. Ahi están las instrucciones de arranque. La idea es que puedas
> construir toda la app de forma autónoma. Solo detente si te trabas con algo. Guarda los 3 documentos dentro del repo.
> Con respecto a la parte de firebase, no voy a crear un proyecto firebase para la plantilla, pero si deja en la app
> toda la construcción necesaria para usarla como si hubiese proyecto de firebase sin que eso imposibilite la
> compilación de la app. Al final dime que preguntas tienes o cuales modificaciones sugieres a los documentos

**Lectura del ejecutor:** vale como `Implementación autorizada: Sí` para cada plan de incremento del arranque, con
granularidad «el arranque entero según v2». No cubre escrituras fuera del árbol que el manual manda confirmar (crashes
reales a un panel de Firebase: no hay proyecto, así que no ocurren).

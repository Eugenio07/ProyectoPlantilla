# <Feature o incremento>

- **Estado:** Plan en borrador
- **Tamaño:** S | M | L
- **Entrada:** <ficha, o en el arranque: «v2, FASE n, incremento <nombre>»>
- **Alcance autorizado, provisional hasta la pasada del auditor:** <sí/no, quién, fecha>
- **Lo que el auditor cambió del alcance:** <tras la pasada>
- **Implementación autorizada:** No

> **En el arranque** un plan es solo la Parte 2: la Parte 1 es el prompt de la fase de v2, por referencia
> (`docs/manuales/kmp_bootstrap_playbook_generico_v2.md`, la fase y el nombre del incremento).

## Parte 1 · Spec (QUÉ y POR QUÉ)

Qué construimos y para quién · situación actual · invariantes (por su frase) · alcance · flujo · datos y reglas ·
casos alternativos · criterios de aceptación · decisiones pendientes.

## Parte 2 · Plan (CÓMO)

### Verificación previa

Contra el commit: `<sha>`

| Afirmación | Cómo se comprobó | Ramas medidas | Resultado | HECHO / SUPUESTO (tarea que lo mide) |
|---|---|---|---|---|

¿Qué de lo que asumo que existe lo borra este mismo plan?

### Solución y decisiones

Si se elige distinto de una decisión escrita, se cita y se dice por qué.

### Mapa de ficheros

### Tareas

Cada tarea dice su orden por capa:

| Capa | Orden |
|---|---|
| Lógica pura | Test primero, verlo fallar por una aserción, luego el código |
| Repositorios y casos de uso | Test primero, con dobles de red |
| ViewModels | Test primero |
| Composables y UI | Sin test primero: captura real (comprobación humana declarada) |
| Corregir un bug | El test que reproduce el fallo, primero |

### Etapa final · Cierre

- [ ] Capturas (si se tocó UI), en `docs/mediciones/` con su índice
- [ ] La puerta entera (sin `--rapido`; antes de la Fase 6.5: los cuatro verdes a mano)
- [ ] Auditoría visual de UI (ranura) si se tocó UI: `compose-visual-loop` (captura en Android e iOS) y
  `/material-3 audit` (10 categorías)
- [ ] Revisión de pre-PR: `/plantilla-review`, antes de abrir el PR
- [ ] Overviews del área cambiada
- [ ] `barrido_texto.py` si hay una salida nueva
- [ ] Entradas de la cola cuyo disparador nombra esta feature: cerradas o con su porqué
- [ ] `docs/entregables-del-arranque.md` con los artefactos nuevos

### Verificación por criterio

Cada criterio **declara su clase** (manual de operación §4):

| Criterio | Clase | Método y evidencia | La mutación que lo mata (fichero, cambio, aserción que cae) / quién, sobre qué build, dónde vive la evidencia | Resultado: pasó / falló / no pude medir |
|---|---|---|---|---|

### Lo que no podrá comprobarse aquí

## Revisiones

(No cuenta para el tope de ~150 líneas.) Cada pasada del auditor con su fecha; cada corrección con su sitio y su commit;
la autorización, copiada literal si la escribe el ejecutor al dictado.

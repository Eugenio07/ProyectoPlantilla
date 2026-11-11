# Para CLAUDE.md (provisional)

Reglas y ranuras que el arranque escribe antes de que exista `CLAUDE.md`. La Fase 9 del manual de arranque
(`docs/manuales/kmp_bootstrap_playbook_generico_v2.md`) lo absorbe entero y lo borra en el mismo commit.

Fechas en la zona del proyecto (America/Caracas, UTC−4). Atribuciones según el manual de operación §7.

## Ranuras del manual de arranque

| Ranura | Valor | Qué hay que escribir siempre |
|---|---|---|
| **Máquina de verificación** | Una sola: la MacBook Pro de quien decide (macOS, Android + iOS) [medido 2026-11-11: Xcode 27.2, emulador `Pixel_10_Pro`, simulador iPhone 17 Pro] | **Sin ella** no corren la compilación de iOS, el arranque en simulador ni la puerta local. Lo nota quien decide al abrir un PR sin el informe de la puerta. Válvula: el CI alojado (Fase 7) cubre los tres primeros verdes y el cuarto en `macos-latest`; el arranque en simulador queda como «no pude medir» hasta que vuelva la máquina |
| **Dónde corre el CI** | Runners alojados de GitHub (no hay runner propio) [medido 2026-11-11: sin runners registrados en el repo]. Se revisa con el método de minutos de la Fase 7 | Fuera de la máquina de trabajo verifica el CI alojado en cada PR (Fase 7) |
| **Protección de ramas** | **No la permite**: repo privado en plan gratuito [medido 2026-11-11: `gh api …/branches/main/protection` → 403] | Rigen las tres reglas de fusión de la Fase 7 (ver abajo, «Fusión») |
| **Backend** | Ninguno todavía → **Fase 3, rama A** (borradores en `docs/contracts/`) | La verdad del contrato son los borradores, hasta que exista backend |
| **Personas** | Una | Quien decide tiene la autoridad de producto |

## Ranuras del manual de operación

| Ranura | Valor |
|---|---|
| **La ruta crítica** | La plantilla no tiene dominio. Su camino de arranque (Splash → force-update → Home) y el salvavidas de crashes son lo único que cualquier app derivada hereda tal cual. **Cada app derivada rellena esta ranura antes de su primera ficha** |
| **El activo crítico** | Ninguno en la plantilla (no guarda ni pide datos de nadie). Lo rellena cada app derivada |
| **Dónde vive el activo crítico** | No aplica en la plantilla |
| **Si la app está publicada** | No |
| **Contra qué entorno prueba el ejecutor** | Local: el stub `MockEngine` de la Fase 4. No hay servidor |
| **Quien decide** | Eugenio Martínez (dueño del repositorio) |
| **El revisor** | Por confirmar por quien decide: un agente en otra conversación, sin acceso al código |
| **Los otros equipos** | Ninguno |
| **La bandeja** | Las incidencias de GitHub del repositorio (propuesta del ejecutor, por confirmar) |
| **La auditoría visual de UI** | La skill `compose-visual-loop` (captura real en emulador y simulador) más `/material-3 audit` sobre lo modificado, con sus 10 categorías |
| **La herramienta de grafo** | Ninguna. Una afirmación de alcanzabilidad se escribe «no comprobada: sin herramienta de grafo» |
| **La zona horaria del proyecto** | America/Caracas (UTC−4) [medido 2026-11-11: `date` → `-04`] |

## Reglas y decisiones (anteriores a CLAUDE.md)

- **`main` hace el papel de `master`** del manual (rama de versiones). `develop` es la rama por defecto
  [medido 2026-11-11]. Lo que el manual llama `master` se lee `main` (gana lo que encuentras).
- **Fusión sin protección de ramas** (Fase 7): no se usa `gh pr merge --auto`; se espera el verde y se fusiona a mano;
  antes de fusionar se comprueba que la cabeza de la rama es la revisada.
- **Arquitectura** (detalle en `docs/ARCHITECTURE.md`): `domain` depende solo de `core/error`, `core/result`,
  `core/log` (interfaz), `core/platform` (contratos) y `core/version`, enumerados; nunca de `core/network` ni `core/di`. `data`→`domain`,
  `ui`→`domain`, nada depende de `ui`. Un use case existe solo si hace algo que el repositorio no puede. La lista de
  `domain` se amplía con una decisión escrita en `docs/ARCHITECTURE.md`.
- **Contratos: rama A** (sin backend). Los borradores viven en `docs/contracts/`; son la fuente para construir el
  backend. La clave JSON de un DTO es la del contrato (`@SerialName` si la propiedad Kotlin difiere).
- **Hook pre-commit, una vez por clon:** `git config core.hooksPath .githooks`. Rechaza en el índice
  `google-services.json`, `GoogleService-Info.plist`, `local.properties`, `*.jks` y `*.keystore`.
- **`.gitattributes` antes del primer guion:** `*.sh text eol=lf`.
- **Autorización del arranque:** quien decide autorizó construir la plantilla entera de forma autónoma, parando solo si
  hay un bloqueo (mensaje literal en `docs/mediciones/2026-11-11-autorizacion-del-arranque.md`).

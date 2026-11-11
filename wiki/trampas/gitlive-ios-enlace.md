# `linkDebugTestIosSimulatorArm64`: `ld: framework 'FirebaseCore' not found`.

- **Síntoma:** `linkDebugTestIosSimulatorArm64`: `ld: framework 'FirebaseCore' not found`.
- **Causa:** El klib de iOS de GitLive exige el framework nativo al enlazar cualquier ejecutable de Kotlin/Native.
- **Regla:** CLAUDE.md §8 «GitLive solo en `androidMain`».

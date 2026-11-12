#!/usr/bin/env bash
# Stop. INFORMA: falla ABIERTO. Si el árbol sucio o el último commit (salvo un merge) tocan UI, recuerda la AUDITORÍA
# VISUAL DE UI (ranura de CLAUDE.md §2: compose-visual-loop con captura en las dos plataformas + /material-3 audit, 10 categorías).
# No corre nada ni sabe si ya se corrió: solo avisa. Mira el árbol sucio y el último commit, no la rama entera, para avisar
# justo después del trabajo de UI y no en cada parada. En Swift solo son UI las vistas (`View` en el nombre) y la escena
# (iOSApp.swift, el App.kt de Swift): SwiftCrashReporter.swift avisaba sin motivo (#PP22).
set -u
entrada="$(cat 2>/dev/null || true)"
case "$entrada" in *'"stop_hook_active": true'* | *'"stop_hook_active":true'*) exit 0 ;; esac
raiz="${CLAUDE_PROJECT_DIR:-$(git rev-parse --show-toplevel 2>/dev/null)}"
[ -n "$raiz" ] && cd "$raiz" 2>/dev/null || exit 0
# El árbol sucio (rastreado Y sin rastrear: una pantalla nueva sin git add) y el último commit, salvo que sea un MERGE:
# entonces es el PR entero, ya auditado, y avisaría en cada parada hasta el siguiente commit (#PP27).
cambios="$( {
  git diff --name-only HEAD 2>/dev/null
  git ls-files --others --exclude-standard 2>/dev/null
  git rev-parse -q --verify HEAD^2 >/dev/null 2>&1 || git diff --name-only HEAD~1 HEAD 2>/dev/null
} | sort -u)"
if printf '%s\n' "$cambios" | grep -qE '(/ui/|composeResources/|^iosApp/iosApp/(.*View[^/]*|iOSApp)\.swift$|App\.kt$)'; then
  echo "Recordatorio: se tocó UI. Auditoría visual (CLAUDE.md §2): captura real en Android e iOS con compose-visual-loop y /material-3 audit con sus 10 categorías, antes de dar el cambio por terminado." >&2
  exit 2
fi
exit 0

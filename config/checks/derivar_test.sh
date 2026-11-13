#!/usr/bin/env bash
# derivar_test.sh [--completo] · el test de config/derivar.sh (plan docs/historico/planes/2026-11-12-derivar.md).
#
# Copia el árbol de trabajo (rastreados y nuevos no ignorados) a carpetas desechables, hace en cada una un repositorio
# con un commit, y ejecuta el guion DE LA COPIA: así prueba el guion tal como está, mutaciones incluidas. Nunca toca el
# árbol de la plantilla. Cada aserción tiene nombre: un rojo dice cuál cayó.
#   C1 derivar sale 0 y deja cada cambio en su sitio · C2 cada entrada mala sale 2 sin tocar nada
#   C3 una forma que el guion no trata sale 1 y la nombra · C4 la puerta y `calidad` lo corren
# --completo (C5, fuera de la puerta): además compila la copia derivada y corre sus tests JVM. Necesita ANDROID_HOME.
# Portable: bash 3.2 y GNU (corre en `calidad`, ubuntu). Salida: 0 · 1 (cayó alguna aserción) · 3 (no pude medir).
set -u
ORIG="$(cd "$(dirname "$0")/../.." && pwd)"
COMPLETO=0
[ "${1:-}" = "--completo" ] && COMPLETO=1
TMP="$(mktemp -d)" || { echo "derivar_test: sin carpeta temporal → NO PUDE MEDIR"; exit 3; }
[ "$COMPLETO" -eq 1 ] || trap 'rm -rf "$TMP"' EXIT
FALLOS=0
FORMAS='proyecto[ _-]?plantilla'

aserta() { # aserta "<nombre>" <comando…>
  local nombre="$1"; shift
  if "$@" >/dev/null 2>&1; then echo "  ok    $nombre"; else echo "  FALLO $nombre"; FALLOS=$((FALLOS + 1)); fi
}

copia() { # copia <destino>: el árbol de trabajo, como repositorio limpio de un commit
  local d="$1"
  mkdir -p "$d"
  (cd "$ORIG" && git ls-files -z --cached --others --exclude-standard |
    while IFS= read -r -d '' f; do [ -e "$f" ] && printf '%s\0' "$f"; done |
    tar --null -T - -cf -) | tar -xf - -C "$d" || return 1
  git -C "$d" init -q &&
    git -C "$d" add -A && # en la copia desechable, no en la plantilla
    git -C "$d" -c user.name=derivar_test -c user.email=derivar_test@localhost -c commit.gpgsign=false \
      -c core.hooksPath=/dev/null commit -qm base
}

commitea() { # commitea <repo> <mensaje> · `add -A` solo en las copias desechables: §11 protege el índice de la plantilla
  git -C "$1" add -A && git -C "$1" -c user.name=derivar_test -c user.email=derivar_test@localhost \
    -c commit.gpgsign=false -c core.hooksPath=/dev/null commit -qm "$2" --allow-empty >/dev/null
}

deriva() { # deriva <repo> <args…>: ejecuta el guion de ESE repo desde su raíz; deja rc, salida en $SALIDA
  local d="$1"; shift
  SALIDA="$(cd "$d" && bash config/derivar.sh "$@" 2>&1)"; RC=$?
}

limpio() { [ -z "$(git -C "$1" status --porcelain)" ]; }
contiene() { printf '%s' "$SALIDA" | grep -qF -- "$1"; }
quedan() { # quedan <repo>: las formas viejas fuera de los registros y de las líneas permitidas
  git -C "$1" grep -n -I -i -E "$FORMAS" -- . ':!docs/historico' ':!docs/mediciones' ':!docs/plans' ':!docs/manuales' \
    ':!config/derivar.sh' ':!config/checks/derivar_test.sh' |
    grep -v -i 'guion bajo' | grep -v -E '^[^:]*\.example:[0-9]+:.*proyecto-plantilla-ejemplo'
}
sin_quedan() { [ -z "$(quedan "$1")" ]; }
tiene() { grep -qF -- "$2" "$1"; }   # tiene <fichero> <texto literal>
no_tiene() { ! grep -qF -- "$2" "$1"; }

echo "--- C4: la puerta y calidad lo corren"
aserta "la puerta lo lista en VERIFICADORES" grep -qE '^VERIFICADORES=.*derivar_test\.sh' "$ORIG/config/checks/puerta.sh"
aserta "calidad lo corre" tiene "$ORIG/.github/actions/calidad/action.yml" "bash config/checks/derivar_test.sh"
aserta "entregables lo lista" tiene "$ORIG/docs/entregables-del-arranque.md" '`config/derivar.sh`'

C1="$TMP/c1"; C2="$TMP/c2"; C3="$TMP/c3"
for c in "$C1" "$C2" "$C3"; do copia "$c" || { echo "derivar_test: no pude copiar el árbol → NO PUDE MEDIR"; exit 3; }; done
N_VIEJO="$(git -C "$C1" ls-files | grep -c '/kotlin/org/example/proyecto_plantilla/')"

echo "--- C2: cada entrada mala sale 2 y no toca nada"
mala() { # mala "<nombre>" "<frase esperada>" <args…>
  local nombre="$1" frase="$2"; shift 2
  deriva "$C2" "$@"
  aserta "$nombre: sale 2" [ "$RC" -eq 2 ]
  aserta "$nombre: dice «$frase»" contiene "$frase"
  aserta "$nombre: el árbol queda igual" limpio "$C2"
}
mala "sin argumentos" "uso:"
mala "paquete con mayúsculas" "paquete" Com.Foo.app "Mi App"
mala "paquete de un segmento" "paquete" miapp "Mi App"
mala "el paquete de la plantilla" "paquete" org.example.proyecto_plantilla "Mi App"
mala "paquete dentro del de la plantilla" "paquete" org.example.proyecto_plantilla.sub "Mi App"
mala "nombre con comillas" "nombre" com.miempresa.miapp 'Mi "App"'
mala "raíz que empieza por cifra" "raíz" com.miempresa.miapp "Mi App" 1App
mala "nombre no ASCII sin raíz" "raíz" com.miempresa.micafe "Mi Café"
echo x >> "$C2/README.md"
deriva "$C2" com.miempresa.miapp "Mi App"
aserta "árbol sucio: sale 2" [ "$RC" -eq 2 ]
aserta "árbol sucio: dice «limpio»" contiene "limpio"
aserta "árbol sucio: solo cambia lo que ya cambiaba" [ "$(git -C "$C2" status --porcelain)" = " M README.md" ]
git -C "$C2" checkout -q -- README.md

echo "--- C1: derivar sale 0 y deja cada cambio en su sitio"
deriva "$C1" com.miempresa.miapp "Mi App"
aserta "sale 0" [ "$RC" -eq 0 ]
aserta "no commitea" [ "$(git -C "$C1" rev-list --count HEAD)" -eq 1 ]
aserta "deja todo en el índice" [ -z "$(git -C "$C1" status --porcelain | grep -v -E '^[MADR] ')" ]
aserta "las carpetas viejas no existen" [ -z "$(find "$C1" -path '*/kotlin/org/example/proyecto_plantilla' -type d)" ]
aserta "no quedan carpetas vacías" [ -z "$(find "$C1" -path '*/kotlin/*' -type d -empty)" ]
aserta "las nuevas tienen los mismos ficheros ($N_VIEJO)" \
  [ "$(git -C "$C1" ls-files | grep -c '/kotlin/com/miempresa/miapp/')" -eq "$N_VIEJO" ]
aserta "paquete en androidApp/build.gradle.kts" tiene "$C1/androidApp/build.gradle.kts" 'applicationId = "com.miempresa.miapp"'
aserta "ruta en plantilla-contract-sync" tiene "$C1/.claude/commands/plantilla-contract-sync.md" 'kotlin/com/miempresa/miapp/data'
aserta "paquete de recursos en HomeScreen.kt" tiene "$C1/shared/src/commonMain/kotlin/com/miempresa/miapp/ui/home/HomeScreen.kt" \
  'miapp.shared.generated.resources'
aserta "bundle id en Config.xcconfig" tiene "$C1/iosApp/Configuration/Config.xcconfig" \
  'PRODUCT_BUNDLE_IDENTIFIER=com.miempresa.miapp.MiApp$(TEAM_ID)'
aserta "bundle id en ci.yml" tiene "$C1/.github/workflows/ci.yml" 'com.miempresa.miapp.MiApp'
aserta "rootProject.name" tiene "$C1/settings.gradle.kts" 'rootProject.name = "MiApp"'
aserta "clase MiAppApp (fichero)" [ -f "$C1/androidApp/src/main/kotlin/com/miempresa/miapp/MiAppApp.kt" ]
aserta "clase MiAppApp (manifiesto)" tiene "$C1/androidApp/src/main/AndroidManifest.xml" 'android:name=".MiAppApp"'
aserta "app_name" tiene "$C1/androidApp/src/main/res/values/strings.xml" '<string name="app_name">Mi App</string>'
aserta "PRODUCT_NAME" tiene "$C1/iosApp/Configuration/Config.xcconfig" 'PRODUCT_NAME=Mi App'
aserta "Mi App.app en el pbxproj" tiene "$C1/iosApp/iosApp.xcodeproj/project.pbxproj" 'path = "Mi App.app"'
aserta "título de CLAUDE.md" tiene "$C1/CLAUDE.md" '# CLAUDE.md · Mi App'
aserta "la excepción del guion bajo, intacta" tiene "$C1/.editorconfig" 'org.example.proyecto_plantilla'
aserta "los registros, intactos" [ -z "$(git -C "$C1" diff --cached --name-only -- docs/historico docs/mediciones docs/plans docs/manuales)" ]
aserta "no queda ninguna forma vieja fuera de las permitidas" sin_quedan "$C1"
aserta "el guion no está" [ ! -e "$C1/config/derivar.sh" ]
aserta "su test no está" [ ! -e "$C1/config/checks/derivar_test.sh" ]
aserta "la puerta no lo lista" no_tiene "$C1/config/checks/puerta.sh" 'derivar_test.sh'
aserta "calidad no lo corre" no_tiene "$C1/.github/actions/calidad/action.yml" 'derivar_test.sh'
aserta "entregables no lo lista" no_tiene "$C1/docs/entregables-del-arranque.md" 'derivar'
aserta "entregables.py de la copia sale 0" python3 -I "$C1/config/checks/entregables.py"
aserta "dice que revalides plantilla-contract-sync" contiene "plantilla-contract-sync"

echo "--- C1 bis: nombre con acentos y raíz aparte"
deriva "$C2" com.miempresa.micafe "Mi Café" MiCafe
aserta "sale 0" [ "$RC" -eq 0 ]
aserta "app_name con acento" tiene "$C2/androidApp/src/main/res/values/strings.xml" '<string name="app_name">Mi Café</string>'
aserta "rootProject.name = MiCafe" tiene "$C2/settings.gradle.kts" 'rootProject.name = "MiCafe"'
aserta "bundle id con la raíz" tiene "$C2/iosApp/Configuration/Config.xcconfig" 'com.miempresa.micafe.MiCafe$(TEAM_ID)'
commitea "$C2" derivado
cp "$C3/config/derivar.sh" "$C2/config/derivar.sh" && commitea "$C2" "el guion otra vez"
deriva "$C2" com.otra.app "Otra"
aserta "ya derivado: sale 2" [ "$RC" -eq 2 ]
aserta "ya derivado: lo dice" contiene "ya derivado"

echo "--- C3: una forma que no trata sale 1 y la nombra"
printf 'Esto viene de Proyecto-Plantilla.\n' > "$C3/docs/nota.md" && commitea "$C3" nota
deriva "$C3" com.miempresa.miapp "Mi App"
aserta "sale 1" [ "$RC" -eq 1 ]
aserta "nombra docs/nota.md:1" contiene "docs/nota.md:1"
aserta "dice cómo volver" contiene "git reset --hard"
aserta "el guion sigue ahí para repetir" [ -e "$C3/config/derivar.sh" ]

if [ "$COMPLETO" -eq 1 ]; then
  echo "--- C5 (--completo): la copia derivada compila y pasan sus tests JVM ($C1)"
  if [ -z "${ANDROID_HOME:-}" ] || [ ! -d "$ANDROID_HOME" ]; then
    echo "derivar_test --completo: sin ANDROID_HOME → NO PUDE MEDIR"; exit 3
  fi
  (cd "$C1" && ./gradlew --console=plain -q :androidApp:assembleDebug :shared:testAndroidHostTest \
    :shared:compileKotlinIosSimulatorArm64)
  aserta "Gradle sale 0 en la copia derivada" [ "$?" -eq 0 ]
fi

if [ "$FALLOS" -gt 0 ]; then echo "derivar_test: $FALLOS aserciones cayeron"; exit 1; fi
echo "derivar_test: todas las aserciones pasan"

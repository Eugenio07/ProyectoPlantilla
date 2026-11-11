#!/usr/bin/env bash
# candado.sh — el candado de la puerta. UNA SOLA función de liveness, que usan la puerta y todos los hooks.
#
#   candado.sh estado   imprime «vivo <pid> <hora>», «huerfano <motivo>» o «libre»; sale 0 (3 si no encuentra .git)
#   candado.sh tomar    (solo la puerta) toma el candado para el proceso que la llama ($PPID); sale 0 si lo tomó,
#                       1 si hay uno VIVO (dice de quién). Con uno HUÉRFANO lo retira, lo anuncia y lo anota en
#                       .git/puerta-huerfanos.log: es la ÚNICA que borra un candado huérfano.
#   candado.sh soltar   (solo la puerta) lo borra si es suyo
#
# VIVO = el directorio existe, su PID existe Y la hora de arranque de ese PID (ps -o lstart=) coincide con la escrita.
# Cualquier otra cosa (sin PID, PID muerto, PID reutilizado por otro proceso) es HUÉRFANO: dice que una puerta murió a
# medias (kill -9, la máquina dormida) y su verde no existe.
set -u
GITDIR="$(git rev-parse --git-dir 2>/dev/null)" || { echo "candado: no encuentro .git"; exit 3; }
CANDADO="$GITDIR/puerta.lock"

hora_de() { ps -o lstart= -p "$1" 2>/dev/null | sed 's/^ *//;s/ *$//'; }

estado() {
  [ -d "$CANDADO" ] || { echo "libre"; return; }
  local pid hora actual
  pid="$(cat "$CANDADO/pid" 2>/dev/null)"
  hora="$(cat "$CANDADO/hora" 2>/dev/null)"
  if [ -z "$pid" ] || [ -z "$hora" ]; then echo "huerfano sin PID o sin hora"; return; fi
  actual="$(hora_de "$pid")"
  if [ -z "$actual" ]; then echo "huerfano PID $pid muerto (creado a las $hora)"; return; fi
  if [ "$actual" != "$hora" ]; then echo "huerfano PID $pid reutilizado (el candado es de las $hora; ese PID arrancó a las $actual)"; return; fi
  echo "vivo $pid $hora"
}

case "${1:-}" in
  estado) estado ;;
  tomar)
    dueno="$PPID"
    for _ in 1 2; do
      if mkdir "$CANDADO" 2>/dev/null; then
        echo "$dueno" > "$CANDADO/pid"
        hora_de "$dueno" > "$CANDADO/hora"
        exit 0
      fi
      e="$(estado)"
      case "$e" in
        vivo*) echo "candado: la puerta ya corre en este árbol ($e). Espera a que termine."; exit 1 ;;
        huerfano*)
          msg="candado huérfano: ${e#huerfano }; lo retiro"
          echo "$msg"
          echo "$(date '+%Y-%m-%d %H:%M:%S %z') $msg" >> "$GITDIR/puerta-huerfanos.log"
          rm -rf "$CANDADO" ;;
        libre) : ;;
      esac
    done
    echo "candado: no pude tomarlo"; exit 3 ;;
  soltar)
    [ -d "$CANDADO" ] && [ "$(cat "$CANDADO/pid" 2>/dev/null)" = "$PPID" ] && rm -rf "$CANDADO"
    exit 0 ;;
  *) echo "uso: candado.sh estado|tomar|soltar" >&2; exit 2 ;;
esac

#!/usr/bin/env bash
# Prueba del candado de la puerta, llamando a candado.sh como lo hará un hook (los hooks nacen en la Fase 10; su prueba
# real va a hooks_test.sh). Sale 0 si todo pasa, 1 si algo falla. NO va en la puerta: toma el candado.
set -u
cd "$(dirname "$0")/../.." || exit 3
C=config/checks/candado.sh
GITDIR="$(git rev-parse --git-dir)"
LOCK="$GITDIR/puerta.lock"
fallos=0
ok() { echo "ok    $1"; }
ko() { echo "FALLO $1"; fallos=$((fallos + 1)); }
hook() { case "$($C estado)" in vivo*) echo "calla" ;; *) echo "corre" ;; esac; }   # lo que hará un hook informativo

[ -d "$LOCK" ] && { echo "hay un candado puesto: no pruebo encima de una puerta real"; exit 3; }

# 1) Mientras una «puerta» corre: vivo; un hook calla; una segunda puerta aborta.
bash -c "$C tomar >/dev/null && sleep 4 && $C soltar" &
for _ in 1 2 3 4 5 6 7 8 9 10; do [ -d "$LOCK" ] && break; sleep 0.2; done
case "$($C estado)" in vivo*) ok "1a con la puerta corriendo: vivo" ;; *) ko "1a esperaba vivo: $($C estado)" ;; esac
[ "$(hook)" = "calla" ] && ok "1b el hook calla con el candado vivo" || ko "1b el hook no calló"
bash -c "$C tomar" >/dev/null 2>&1; [ $? -eq 1 ] && ok "1c una segunda puerta aborta (1)" || ko "1c la segunda puerta no abortó"
wait
[ "$($C estado)" = "libre" ] && ok "1d al terminar, la puerta suelta el candado" || ko "1d el candado sigue: $($C estado)"

# 2) PID muerto: huérfano; el hook corre y NO lo borra; quien lo toma lo anuncia, lo anota y lo retira.
sleep 0 & muerto=$!; wait "$muerto"
mkdir "$LOCK"; echo "$muerto" > "$LOCK/pid"; echo "Mon Jan  1 00:00:00 2001" > "$LOCK/hora"
case "$($C estado)" in huerfano*muerto*) ok "2a PID muerto: huérfano" ;; *) ko "2a esperaba huérfano (muerto): $($C estado)" ;; esac
[ "$(hook)" = "corre" ] && ok "2b el hook corre con el candado huérfano" || ko "2b el hook calló"
[ -d "$LOCK" ] && ok "2c el hook no lo borró" || ko "2c el hook borró el candado"
antes=$( (wc -l < "$GITDIR/puerta-huerfanos.log") 2>/dev/null || echo 0)
salida="$(bash -c "$C tomar && $C soltar")"
echo "$salida" | grep -q "candado huérfano: PID $muerto muerto" && ok "2d la puerta lo anuncia al tomarlo" || ko "2d sin anuncio: $salida"
despues=$(wc -l < "$GITDIR/puerta-huerfanos.log")
[ "$despues" -gt "$antes" ] && ok "2e queda anotado en puerta-huerfanos.log" || ko "2e no se anotó"

# 3) PID vivo pero de OTRO proceso (hora distinta): huérfano.
sleep 30 & otro=$!
mkdir "$LOCK"; echo "$otro" > "$LOCK/pid"; echo "Mon Jan  1 00:00:00 2001" > "$LOCK/hora"
case "$($C estado)" in huerfano*reutilizado*) ok "3a PID vivo de otro proceso: huérfano" ;; *) ko "3a esperaba huérfano (reutilizado): $($C estado)" ;; esac
[ "$(hook)" = "corre" ] && ok "3b el hook corre" || ko "3b el hook calló"
kill "$otro" 2>/dev/null; wait "$otro" 2>/dev/null; rm -rf "$LOCK"

# 4) Sin PID: huérfano.
mkdir "$LOCK"
case "$($C estado)" in huerfano*sin*) ok "4 candado sin PID: huérfano" ;; *) ko "4 esperaba huérfano (sin PID): $($C estado)" ;; esac
rm -rf "$LOCK"

echo; [ "$fallos" -eq 0 ] && { echo "candado_test: todo pasa"; exit 0; } || { echo "candado_test: $fallos fallos"; exit 1; }

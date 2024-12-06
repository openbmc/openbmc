#!/bin/bash
# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit
# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source /usr/share/gbmc-net-lib.sh || exit

: "${RA_IF:?No RA interface set}"
: "${IP_OFFSET=?1}"
: "${ROUTE_METRIC:?No Metric set}"

# We would prefer empty string but it's easier for associative array handling
# to use invalid
old_rtr=invalid
old_mac=invalid
old_pfx=invalid
old_fqdn=invalid

default_update_rtr() {
  local rtr="$1"
  local mac="$2"

  if ip addr show | grep -q "^[ ]*inet6 $rtr/"; then
    echo "Router is ourself, ignoring" >&2
    return 0
  fi

  # Override any existing gateway information within files
  # Make sure we cover `00-*` and `-*` files
  for file in /run/systemd/network/{00,}-bmc-$RA_IF.network; do
    mkdir -p "$file.d"
    printf '[Route]\nGateway=%s\nGatewayOnLink=true\nMetric=%d\n[Neighbor]\nMACAddress=%s\nAddress=%s' \
      "$rtr" "$ROUTE_METRIC" "$mac" "$rtr" >"$file.d"/10-gateway.conf
  done

  # Don't force networkd to reload as this can break phosphor-networkd
  # Fall back to reload only if ip link commands fail
  (ip -6 route replace default via "$rtr" onlink dev "$RA_IF" metric "$ROUTE_METRIC" && \
    ip -6 neigh replace "$rtr" dev "$RA_IF" lladdr "$mac") || \
    gbmc_net_networkd_reload "$RA_IF" || true

  echo "Set router $rtr on $RA_IF" >&2
}

default_update_fqdn() {
  local fqdn="$1"
  [ -z "$fqdn" ] && return
  hostnamectl set-hostname "$fqdn" || true
  echo "Set hostname $fqdn on $RA_IF" >&2
}

retries=1
min_w=10
declare -A rtrs
rtrs=()
while true; do
  # shellcheck disable=SC2206
  data=(${rtrs["${old_rtr}"]-})
  curr_dl="${data[1]-$(( min_w + SECONDS ))}"
  args=(-m "$RA_IF" -w $(( (curr_dl - SECONDS) * 1000 )))
  if (( retries > 0 )); then
    args+=(-r "$retries")
  else
    args+=(-d)
  fi
  while read -r line; do
    # `script` terminates all lines with a CRLF, remove it
    line="${line:0:-1}"
    # shellcheck disable=SC2026
    if [ -z "$line" ]; then
      lifetime=-1
      mac=
      hextet=
      pfx=
      host=
      domain=
    elif [[ "$line" =~ ^Router' 'lifetime' '*:' '*([0-9]*) ]]; then
      lifetime="${BASH_REMATCH[1]}"
    elif [[ "$line" =~ ^Source' 'link-layer' 'address' '*:' '*([a-fA-F0-9:]*)$ ]]; then
      mac="${BASH_REMATCH[1]}"
    elif [[ "$line" =~ ^Prefix' '*:' '*(.*)/([0-9]+)$ ]]; then
      t_pfx="${BASH_REMATCH[1]}"
      t_pfx_len="${BASH_REMATCH[2]}"
      ip_to_bytes t_pfx_b "$t_pfx" || continue
      (( (t_pfx_len == 76 || t_pfx_len == 80) && (t_pfx_b[8] & 0xfd) == 0xfd )) || continue
      (( t_pfx_b[9] &= 0xf0 ))
      (( t_pfx_b[9] |= IP_OFFSET ))
      hextet="fd$(printf '%02x' "${t_pfx_b[9]}")"
      pfx="$(ip_bytes_to_str t_pfx_b)"
    elif [[ "$line" =~ ^'DNS search list'' '*:' '*([^.]+)(.*[.]google[.]com)' '*$ ]]; then
      # Ideally, we use PCRE and with lookahead and can do this in a single regex
      #   ^([a-zA-Z0-9-]+(?=-n[a-fA-F0-9]{1,4})|[a-zA-Z0-9-]+(?!-n[a-fA-F0-9]{1,4}))[^.]*[.]((?:[a-zA-Z0-9]*[.])*google[.]com)$
      # Instead we do multiple steps to extract the needed info
      host="${BASH_REMATCH[1]}"
      domain="${BASH_REMATCH[2]#.}"
      if [[ "$host" =~ (-n[a-fA-F0-9]{1,4})$ ]]; then
        host="${host%"${BASH_REMATCH[1]}"}"
      fi
    elif [[ "$line" =~ ^from' '(.*)$ ]]; then
      rtr="${BASH_REMATCH[1]}"
      # Only valid default routers can be considered, 0 lifetime implies
      # a non-default router
      (( lifetime > 0 )) || continue

      dl=$((lifetime + SECONDS))
      fqdn=
      if [[ -n $host && -n $hextet && -n $domain ]]; then
        fqdn="$host-n$hextet.$domain"
      fi
      rtrs["$rtr"]="$mac $dl $pfx $fqdn"
      # We have some notoriously noisy lab environments with many routers being broadcast
      # We always prefer "fe80::1" in prod and labs for routing, so prefer that gateway.
      # We also want to take the first router we find to speed up acquisition on boot.
      if [[ "$rtr" = "fe80::1" || "$old_rtr" = "invalid" ]]; then
        if [[ "$rtr" != "$old_rtr" && "$mac" != "$old_mac" ]]; then
          echo "Got defgw $rtr at $mac on $RA_IF" >&2
          update_rtr "$rtr" "$mac" || true
          retries=-1
          old_mac="$mac"
          old_rtr="$rtr"
        fi
      fi
      # Only update router properties if we use this router
      [[ "$rtr" == "$old_rtr" ]] || continue
      if [[ $pfx != "$old_pfx" ]]; then
        echo "Got PFX $pfx from $rtr on $RA_IF" >&2
        old_pfx="$pfx"
        update_pfx "$pfx" || true
      fi
      if [[ $fqdn != "$old_fqdn" ]]; then
        echo "Got FQDN $fqdn from $rtr on $RA_IF" >&2
        old_fqdn="$fqdn"
        update_fqdn "$fqdn" || true
      fi
    fi
  done < <(exec script -q -c "rdisc6 ${args[*]}" /dev/null 2>/dev/null)
  # Purge any expired routers
  for rtr in "${!rtrs[@]}"; do
    # shellcheck disable=SC2206
    data=(${rtrs["$rtr"]})
    dl=${data[1]}
    if (( dl <= SECONDS )); then
      unset "rtrs[$rtr]"
    fi
  done
  # Consider changing the gateway if the old one doesn't send RAs for the entire period
  # This ensures we don't flip flop between multiple defaults if they exist.
  if [[ "$old_rtr" != "invalid" && -z "${rtrs["$old_rtr"]-}" ]]; then
    echo "Old router $old_rtr disappeared" >&2
    old_rtr=invalid
    for rtr in "${!rtrs[@]}"; do
      # shellcheck disable=SC2206
      data=(${rtrs["$rtr"]})
      mac=${data[0]}
      dl=${data[1]}
      pfx=${data[2]}
      fqdn=${data[3]}
      update_rtr "$rtr" "$mac" || true
      update_pfx "$pfx" || true
      update_fqdn "$fqdn" || true
      break
    done
  fi

  # If rdisc6 exits early we still want to wait for the deadline before retrying
  (( timeout = curr_dl - SECONDS ))
  sleep $(( timeout < 0 ? 0 : timeout ))
done

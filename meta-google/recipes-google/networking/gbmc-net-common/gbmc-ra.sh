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
: "${ROUTE_METRIC?No Metric set}"
# An empty ROUTE_METRIC means the RA router is only used for the interface
# routing table, without any default routes in the main table.
ROUTE_METRIC_LO=
if [ -n "$ROUTE_METRIC" ]; then
  ROUTE_METRIC_LO="$((ROUTE_METRIC + GBMC_ROUTE_METRIC_FALLBACK_OFFSET))"
fi

# We would prefer empty string but it's easier for associative array handling
# to use invalid
old_rtr=invalid
old_mac=invalid
old_rt=invalid
old_pfx=invalid
old_fqdn=invalid

# Installs one of the router's main table default routes, with the source
# gbmc-br-gw-src picked for it if any. The route matches its networkd
# [Route] (proto static and the same source) so networkd keeps it when it
# reconfigures the interface, see GBMC_NET_ROUTE_SRC_DIR.
# Arguments:
#   $1: Router IPv6 address
#   $2: Route metric
rtr_route_replace() {
  local rtr="$1"
  local metric="$2"
  local args=(default via "$rtr" onlink dev "$RA_IF" metric "$metric" proto static)
  local src
  gbmc_net_route_src_get src "$RA_IF" "$rtr" "$metric"
  # The saved source might not be usable (yet), install it without one
  if [[ -n $src ]] && ip -6 route replace "${args[@]}" src "$src" 2>/dev/null; then
    return 0
  fi
  ip -6 route replace "${args[@]}"
}

# Install router and gateway configuration for an RA router.
# Writes networkd drop-ins ([Neighbor] and [Route] definitions) under
# /run/systemd/network/ for the RA interface, updates the runtime neighbor
# table and kernel routes (both interface-specific and default metric routes),
# and records the last seen RA router info.
# Arguments:
#   $1: Router IPv6 address
#   $2: Router MAC address
#   $3: Flag indicating if this is a newly discovered router (1) or cached (0)
add_rtr() {
  local rtr="$1"
  local mac="$2"
  local new="$3"

  local route_table
  route_table="$(gbmc_net_route_table_for_intf "$RA_IF")" || return

  # Override any existing gateway information within files
  # Make sure we cover `00-*` and `-*` files
  local conf route
  printf -v conf '[Neighbor]\nMACAddress=%s\nAddress=%s\n' "$mac" "$rtr"
  printf -v route '[Route]\nGateway=%s\nGatewayOnLink=true\nMetric=%d\nTable=%d\n' \
    "$rtr" "$GBMC_ROUTE_METRIC_ONLINK" "$route_table"
  conf+="$route"
  if [ -n "$ROUTE_METRIC" ]; then
    printf -v route '[Route]\nGateway=%s\nGatewayOnLink=true\nMetric=%d\n' \
      "$rtr" "$ROUTE_METRIC_LO"
    conf+="$route"
  fi
  gbmc_net_reload_queue_start
  gbmc_net_gateway_conf_write "$RA_IF" 10-gateway.conf "$conf"
  if (( new == 1 )); then
    conf=
    if [ -n "$ROUTE_METRIC" ]; then
      printf -v conf '[Route]\nGateway=%s\nGatewayOnLink=true\nMetric=%d\n' \
        "$rtr" "$ROUTE_METRIC"
    fi
    gbmc_net_gateway_conf_write "$RA_IF" 10-gateway-dyn.conf "$conf"
    gbmc_net_write_if_changed "$rtr"$'\n'"$mac" "/run/last-ra/$RA_IF" || true
    if [ -z "${gbmc_net_avoid_rwfs-${GBMC_AVOID_RWFS-}}" ]; then
      gbmc_net_write_if_changed "$rtr"$'\n'"$mac" "/var/google/last-ra/$RA_IF" || true
    fi
  fi

  # Also apply to the kernel right away
  ip -6 neigh replace "$rtr" dev "$RA_IF" lladdr "$mac" || true
  ip -6 route replace default via "$rtr" onlink dev "$RA_IF" metric "$GBMC_ROUTE_METRIC_ONLINK" table "$route_table" proto static || true
  if [ -n "$ROUTE_METRIC" ]; then
    rtr_route_replace "$rtr" "$ROUTE_METRIC_LO" || true
  fi
  if (( new == 1 )) && [ -n "$ROUTE_METRIC" ]; then
    rtr_route_replace "$rtr" "$ROUTE_METRIC" || true
  fi
  gbmc_net_reload_queue_end || true
}

# Read the old info from persistent storage in case ToR is updating
rafile=
if [ -e "/run/last-ra/$RA_IF" ]; then
  rafile="/run/last-ra/$RA_IF"
elif [ -e "/var/google/last-ra/$RA_IF" ]; then
  rafile="/var/google/last-ra/$RA_IF"
fi
if [ -n "$rafile" ]; then
  rtr='' mac=''
  exec {rafd}<"$rafile"
  read -r -u "$rafd" rtr
  read -r -u "$rafd" mac
  exec {rafd}<&-
  # Masked (bind mounted empty) files have no router
  if [ -n "$rtr" ]; then
    echo "Loading old router $rtr($mac)" >&2
    add_rtr "$rtr" "$mac" 0
  fi
fi

# Process router reachability and prefix information received via RA.
# Ignores routers assigned to local BMC addresses, verifies prefix matching
# against the expected subnet, and manages interface routes and networkd drop-ins.
# Arguments:
#   $1: Router IPv6 address
#   $2: Router MAC address
#   $3: Operation ('add' or 'remove', defaults to 'add')
default_update_rtr() {
  local rtr="$1"
  local mac="$2"
  local op="${3-add}"

  if [[ ${op} = "add" ]]; then
    echo "Set router $rtr on $RA_IF" >&2
    add_rtr "$rtr" "$mac" 1
  elif [[ ${op} = "remove" ]]; then
    echo "Del router $rtr on $RA_IF" >&2
    # Override any existing gateway information within files
    # Make sure we cover `00-*` and `-*` files
    gbmc_net_gateway_conf_write "$RA_IF" 10-gateway-dyn.conf ''

    [ -n "$ROUTE_METRIC" ] || return 0
    ip -6 route del default via "$rtr" onlink dev "$RA_IF" metric "$ROUTE_METRIC" 2>/dev/null || true
  fi
}

# Update the BMC's hostname based on the DNS search list domain from RA.
# Arguments:
#   $1: Fully qualified domain name (FQDN)
default_update_fqdn() {
  local fqdn="$1"
  [ -z "$fqdn" ] && return
  hostnamectl set-hostname "$fqdn" || true
  echo "Set hostname $fqdn on $RA_IF" >&2
}

# mininum retry time
min_w="$GBMC_RA_MIN_RETRY"
# rdisc time
r_timeout="$GBMC_RA_TIMEOUT"
# routing expiration time
expire_time="$GBMC_RA_EXPIRE"
# rs intervals
rs_intervals="$GBMC_RA_RS_INTERVAL"
declare -A rtrs
rtrs=()
while true; do
  # shellcheck disable=SC2206
  data=(${rtrs["${old_rtr}"]-})
  next_rs=
  if [ -z "${data[1]}" ]; then
    next_rs=$(( min_w + SECONDS ))
  else
    next_rs=$(( rs_intervals + SECONDS ))
  fi
  args=(-m "$RA_IF" -w $(( r_timeout * 1000 )) -r 1)
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
      if ip addr show | grep -q "^[ ]*inet6 $rtr/"; then
        echo "Router is ourself, ignoring" >&2
        continue
      fi

      dl=$((expire_time + SECONDS))
      fqdn=
      if [[ -n $host && -n $hextet && -n $domain ]]; then
        fqdn="$host-n$hextet.$domain"
      fi
      rtrs["$rtr"]="$mac $dl $pfx $fqdn"
      # We have some notoriously noisy lab environments with many routers being broadcast
      # We always prefer "fe80::1" in prod and labs for routing, so prefer that gateway.
      # We also want to take the first router we find to speed up acquisition on boot.
      if [[ "$rtr" = "fe80::1" || "$old_rtr" = "invalid" || "$rtr" = "$old_rtr" ]]; then
        rt="$(gbmc_net_route_table_for_intf "$RA_IF")" || rt=invalid
        if [[ "$rtr" != "$old_rtr" || "$mac" != "$old_mac" || "$rt" != "$old_rt" ]]; then
          echo "Got defgw $rtr at $mac on $RA_IF" >&2
          update_rtr "$rtr" "$mac" || true
          old_mac="$mac"
          old_rtr="$rtr"
          old_rt="$rt"
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
    # replace routing config
    found="false"
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
      old_rtr="$rtr"
      old_pfx="$pfx"
      old_mac="$mac"
      old_rt="$(gbmc_net_route_table_for_intf "$RA_IF")" || old_rt=invalid
      old_fqdn="$fqdn"
      found="true"
      break
    done
    # no other route exsits, removing the route
    if [[ "$found" = "false" ]]; then
      update_rtr "$old_rtr" "$old_mac" "remove" || true
      old_rtr=invalid
      old_mac=invalid
      old_rt=invalid
    fi
  fi

  # If rdisc6 exits early we still want to wait for the deadline before retrying
  (( timeout = next_rs - SECONDS ))
  sleep $(( timeout < 0 ? 0 : timeout ))
done

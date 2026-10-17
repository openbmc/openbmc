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

[[ -n ${gbmc_br_from_ra_lib-} ]] && return

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit
# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source /usr/share/gbmc-net-lib.sh || exit
# shellcheck source=meta-google/recipes-google/networking/gbmc-bridge/gbmc-br-lib.sh
source /usr/share/gbmc-br-lib.sh || exit

gbmc_br_from_ra_init=
gbmc_br_from_ra_mac=
# RA prefixes we configure addresses in
declare -A gbmc_br_from_ra_pfxs=()
# RA prefix -> uptime deadline for its route to come back, see below
# shellcheck disable=SC2034  # grace map, used by name
declare -A gbmc_br_from_ra_gone=()
declare -A gbmc_br_from_ra_prev_addrs=()
# RA /80 gateway routes on gbmcbr (prefix -> gateway link-local)
declare -A gbmc_br_from_ra_routes=()
# shellcheck disable=SC2034  # grace map, used by name
declare -A gbmc_br_from_ra_routes_gone=()
declare -A gbmc_br_from_ra_prev_routes=()
# Last carrier state seen on each gbmcbr port
declare -A gbmc_br_from_ra_port_carrier=()

# The RA addresses are configured through networkd rather than only with
# `ip addr`. networkd removes addresses it doesn't manage every time it
# reconfigures gbmcbr (e.g. on any reload), which briefly changed the
# preferred source and triggered more reloads in a loop.
#
# Every reconfigure of gbmcbr also restarts networkd's router discovery,
# which flushes all of the RA routes until the next RA is received, which it
# solicits right away. Route deletions therefore don't immediately mean the
# prefix is gone, and acting on them would rewrite our config and reload
# networkd, flushing the routes again. Instead, a prefix is only dropped once
# its route stayed gone for GBMC_BR_RA_GRACE seconds. This also covers
# prefixes networkd removes because they were withdrawn or expired.
GBMC_BR_FROM_RA_CONFS=(/run/systemd/network/{00,}-bmc-gbmcbr.network.d/65-ra-addrs.conf)

# Finds the gateway on gbmcbr that advertised an on-link /80 prefix. A
# ...:fd<xy>::/80 RA route matches the on-link ...:fd<x>0::/80 prefix of the
# same /64, preferring the (sub)root BMC (y <= 1) over sibling trays. Without
# one, it falls back to the gateway of a default route advertisement (/124)
# in the same /64, see gbmc_br_gw_src_br_routes.
# Arguments:
#   $1: Output variable for the gateway link-local address
#   $2: On-link /80 prefix (with length)
# shellcheck disable=SC2034  # nameref
gbmc_br_from_ra_pfx_gw() {
  local -n pfx_gw_out="$1"
  pfx_gw_out=
  local pfx_bytes=()
  ip_to_bytes pfx_bytes "${2%/*}" || return
  local fallback_gw=
  local rtr_pfx
  while read -r rtr_pfx; do
    [[ -n $rtr_pfx ]] || continue
    local rtr_bytes=()
    ip_to_bytes rtr_bytes "${rtr_pfx%/*}" || continue
    (( pfx_bytes[8] == 0xfd && (rtr_bytes[8] == 0 || rtr_bytes[8] == 0xfd) )) || continue
    (( (rtr_bytes[9] & 0xf0) == pfx_bytes[9] )) || continue
    ip_same_pfx "${rtr_pfx%/*}" "${2%/*}" 64 || continue
    if (( (rtr_bytes[9] & 0x0f) <= 1 )); then
      pfx_gw_out="${gbmc_br_from_ra_routes["$rtr_pfx"]}"
      return 0
    fi
    [[ -n $fallback_gw ]] || fallback_gw="${gbmc_br_from_ra_routes["$rtr_pfx"]}"
  done < <(printf '%s\n' "${!gbmc_br_from_ra_routes[@]}" | sort)
  if [[ -n $fallback_gw ]]; then
    pfx_gw_out="$fallback_gw"
    return 0
  fi
  # shellcheck disable=SC2154
  while read -r rtr_pfx; do
    [[ -n $rtr_pfx ]] || continue
    ip_same_pfx "${rtr_pfx%/*}" "${2%/*}" 64 || continue
    pfx_gw_out="${gbmc_br_gw_src_br_routes["$rtr_pfx"]}"
    return 0
  done < <(printf '%s\n' "${!gbmc_br_gw_src_br_routes[@]}" | sort)
  return 1
}

# Installs the route and routing policy rules for a /80 on gbmcbr, see
# gbmc_br_from_ra_write_conf().
# Arguments:
#   $1: Prefix with length
#   $2: Gateway, or empty for an on-link prefix
#   $3: Policy routing table of the prefix's gateway, if any
gbmc_br_from_ra_pfx_add() {
  local pfx="$1"
  local gw="$2"
  local rt="$3"
  # Separate owners, so a /80 advertised both ways keeps its From= rule
  # while one of them goes away
  local owner=from-ra-addr
  [[ -z $gw ]] || owner=from-ra-route
  ip -6 route replace "$pfx" ${gw:+via "$gw" onlink} dev gbmcbr \
    metric "$GBMC_ROUTE_METRIC_ONLINK" proto static 2>/dev/null || true
  gbmc_net_rule_replace to "$pfx" lookup main pref "$GBMC_INTF_ROUTE_TABLE_BASE" 2>/dev/null || true
  gbmc_br_from_rule_set "$owner" "$pfx" "$rt"
}

# Removes what gbmc_br_from_ra_pfx_add() installed.
# Arguments:
#   $1: Prefix with length
#   $2: Gateway, or empty for an on-link prefix
gbmc_br_from_ra_pfx_del() {
  local pfx="$1"
  local gw="$2"
  local owner=from-ra-addr
  [[ -z $gw ]] || owner=from-ra-route
  gbmc_br_from_rule_set "$owner" "$pfx"
  # The same /80 can still be wanted as the other kind
  [[ -z ${gbmc_br_from_ra_pfxs["$pfx"]-}${gbmc_br_from_ra_routes["$pfx"]-} ]] || return 0
  ip -6 route del "$pfx" ${gw:+via "$gw"} dev gbmcbr metric "$GBMC_ROUTE_METRIC_ONLINK" 2>/dev/null || true
  ip -6 rule del to "$pfx" lookup main pref "$GBMC_INTF_ROUTE_TABLE_BASE" 2>/dev/null || true
}

# Writes the networkd config for the given "<addr> <pfx> [<rt>]" and
# "<pfx> <gw> [<rt>]" entries, only reloading networkd when the config
# actually changes.
#
# It also makes networkd ignore RAs from our own gbmcbr link-local address. A
# BMC that also routes for gbmcbr (e.g. a tray with its own front link)
# advertises prefixes there. networkd ignores route options from its own RAs,
# but not on-link prefixes, so whenever we solicit RAs our own prefixes showed
# up as RA routes. Configuring an address in one reloaded networkd, which
# flushed the route again, which removed the address and reloaded again,
# every RS interval, flushing the tray default routes every time.
#
# Each prefix and gateway /80 route also gets its route and routing policy
# rules over gbmcbr. The kernel installs the route from the RA, but it is
# flushed on every reconfigure until the next RA, which leaves the rest of the
# /80 (e.g. other BMCs) unreachable in the meantime. The To= rule keeps traffic
# to peer BMCs in the main table ahead of any local uplink From= rule, and the
# From= rule routes traffic sourced from a peer's RA prefix back through that
# gateway's gbmcbr routing table.
#
# Each address also declares its /80's address label, so networkd puts the
# label back whenever it reconfigures gbmcbr. We add it to the kernel right
# away too, and networkd tolerates finding it already there.
gbmc_br_from_ra_write_conf() {
  local contents=
  local ll
  if ll="$(ip_pfx_concat fe80::/64 "$(mac_to_eui64 "$gbmc_br_from_ra_mac")")"; then
    contents+="[IPv6AcceptRA]"$'\n'"RouterDenyList=${ll%/*}"$'\n'
  fi
  local -A tables=()
  local first second rt
  while read -r first second rt; do
    [[ -n $first ]] || continue
    local pfx
    if [[ $second == */* ]]; then
      pfx="$second"
      contents+="[Address]"$'\n'"Address=$first"$'\n'"AddPrefixRoute=no"$'\n'
      contents+="[IPv6AddressLabel]"$'\n'"Prefix=$pfx"$'\n'"Label=$GBMC_SLAAC_ADDRLABEL"$'\n'
      contents+="[Route]"$'\n'"Destination=$pfx"$'\n'"Metric=$GBMC_ROUTE_METRIC_ONLINK"$'\n'
    else
      pfx="$first"
      contents+="[Route]"$'\n'"Destination=$pfx"$'\n'"Gateway=$second"$'\n'
      contents+="GatewayOnLink=true"$'\n'"Metric=$GBMC_ROUTE_METRIC_ONLINK"$'\n'
    fi
    contents+="[RoutingPolicyRule]"$'\n'"To=$pfx"$'\n'"Table=main"$'\n'
    contents+="Priority=$GBMC_INTF_ROUTE_TABLE_BASE"$'\n'
    if [[ -n $rt ]]; then
      tables["$rt"]=1
      contents+="[RoutingPolicyRule]"$'\n'"From=$pfx"$'\n'"Table=$rt"$'\n'
      contents+="Priority=$GBMC_BR_RA_RULE_PREF"$'\n'
    fi
  done < <(printf '%s\n' "$@" | sort)
  while read -r rt; do
    [[ -n $rt ]] || continue
    gbmc_br_gw_table_route contents "$rt" || true
  done < <(printf '%s\n' "${!tables[@]}" | sort -n)

  if gbmc_net_write_if_changed "$contents" "${GBMC_BR_FROM_RA_CONFS[@]}"; then
    echo "gBMC Bridge RA addresses changed: ${*:-none}" >&2
  fi
}

# Drops a gateway /80 route that stayed gone for GBMC_BR_RA_GRACE seconds.
# Arguments:
#   $1: Prefix with length
gbmc_br_from_ra_route_expire() {
  local pfx="$1"
  local gw="${gbmc_br_from_ra_routes["$pfx"]-}"
  [[ -n $gw ]] || return 0
  # Make sure we didn't miss it coming back
  ip -6 route show "$pfx" dev gbmcbr proto ra 2>/dev/null | grep -q ' via ' && return 0
  echo "gBMC Bridge RA Route Del: $pfx via $gw" >&2
  unset 'gbmc_br_from_ra_prev_routes[$pfx]'
  unset 'gbmc_br_from_ra_routes[$pfx]'
  gbmc_br_from_ra_pfx_del "$pfx" "$gw"
}

# Drops the address in an on-link /80 prefix that stayed gone for
# GBMC_BR_RA_GRACE seconds.
# Arguments:
#   $1: Prefix with length
gbmc_br_from_ra_addr_expire() {
  local pfx="$1"
  [[ -n ${gbmc_br_from_ra_pfxs["$pfx"]-} ]] || return 0
  # Make sure we didn't miss it coming back
  ip -6 route show "$pfx" dev gbmcbr proto ra 2>/dev/null | grep -q -v ' via ' && return 0
  unset 'gbmc_br_from_ra_pfxs[$pfx]'
  local sfx addr
  sfx="$(mac_to_eui48 "$gbmc_br_from_ra_mac")" || return
  addr="$(ip_pfx_concat "$pfx" "$sfx")" || return
  echo "gBMC Bridge RA Addr Del: $addr (pfx $pfx label $GBMC_SLAAC_ADDRLABEL)" >&2
  ip addr del "$addr" dev gbmcbr 2>/dev/null || true
  ip addrlabel del prefix "$pfx" dev gbmcbr label "$GBMC_SLAAC_ADDRLABEL" 2>/dev/null || true
  unset 'gbmc_br_from_ra_prev_addrs[$addr]'
  gbmc_br_from_ra_pfx_del "$pfx" ''
}

gbmc_br_from_ra_update() {
  [[ -n $gbmc_br_from_ra_init && -n $gbmc_br_from_ra_mac ]] || return

  gbmc_ip_monitor_grace_expire gbmc_br_from_ra_routes_gone from-ra-route \
    gbmc_br_from_ra_route_expire
  gbmc_ip_monitor_grace_expire gbmc_br_from_ra_gone from-ra-addr \
    gbmc_br_from_ra_addr_expire

  local -a entries=()
  local pfx
  for pfx in "${!gbmc_br_from_ra_routes[@]}"; do
    local gw="${gbmc_br_from_ra_routes["$pfx"]}"
    local rt=
    gbmc_br_gw_table rt "$gw" || true
    entries+=("$pfx $gw${rt:+ $rt}")
    if [[ ${gbmc_br_from_ra_prev_routes["$pfx"]-} != "$gw $rt" ]]; then
      echo "gBMC Bridge RA Route Add: $pfx via $gw${rt:+ (table $rt)}" >&2
      gbmc_br_from_ra_prev_routes["$pfx"]="$gw $rt"
      gbmc_br_from_ra_pfx_add "$pfx" "$gw" "$rt"
    fi
  done

  for pfx in "${!gbmc_br_from_ra_pfxs[@]}"; do
    local cidr sfx addr
    if ! cidr="$(ip_pfx_to_cidr "$pfx")" || (( cidr != 80 )) ||
       ! sfx="$(mac_to_eui48 "$gbmc_br_from_ra_mac")" ||
       ! addr="$(ip_pfx_concat "$pfx" "$sfx")"; then
      unset 'gbmc_br_from_ra_pfxs[$pfx]'
      continue
    fi
    local gw='' rt=''
    if gbmc_br_from_ra_pfx_gw gw "$pfx"; then
      gbmc_br_gw_table rt "$gw" || true
    fi
    entries+=("$addr $pfx${rt:+ $rt}")
    # Unset (never configured) differs from empty (no gateway table)
    if [[ ${gbmc_br_from_ra_prev_addrs["$addr"]-none} != "$rt" ]]; then
      echo "gBMC Bridge RA Addr Add: $addr (pfx $pfx label $GBMC_SLAAC_ADDRLABEL${rt:+ table $rt})" >&2
      gbmc_br_from_ra_prev_addrs["$addr"]="$rt"
      # Usable right away, networkd takes it over once it reloads
      ip addr replace "$addr" dev gbmcbr noprefixroute
      ip addrlabel add prefix "$pfx" dev gbmcbr label "$GBMC_SLAAC_ADDRLABEL" 2>/dev/null || true
      gbmc_br_from_ra_pfx_add "$pfx" '' "$rt"
    fi
  done

  gbmc_br_from_ra_write_conf "${entries[@]}"
}

gbmc_br_from_ra_hook() {
  # shellcheck disable=SC2154
  if [[ $change == init ]]; then
    gbmc_br_from_ra_init=1
    gbmc_ip_monitor_defer
  elif [[ $change == defer ]]; then
    gbmc_br_from_ra_update
  elif [[ $change == timer && $timer == from-ra-* ]]; then
    gbmc_br_from_ra_update
  elif [[ $change == link && $action == del &&
          -n ${gbmc_br_from_ra_port_carrier["$intf"]-} ]]; then
    # A port that comes back under the same name is new again
    unset 'gbmc_br_from_ra_port_carrier[$intf]'
  elif [[ $change == link && $intf != gbmcbr && $intf != gbmcbr?* &&
          -e /sys/class/net/gbmcbr/brif/$intf ]]; then
    # USB NCM/ECM ports report operstate UNKNOWN, so go by the carrier itself.
    # The gbmcbr* ports (gbmcbrdhcp, gbmcbrdummy) are virtual and always up.
    local up=0 c=
    read -r c 2>/dev/null <"/sys/class/net/$intf/carrier"
    [[ $c == 1 ]] && up=1
    local prev="${gbmc_br_from_ra_port_carrier["$intf"]-0}"
    gbmc_br_from_ra_port_carrier["$intf"]="$up"
    if (( up == 1 && prev == 0 )); then
      echo "gBMC Bridge port $intf carrier UP, soliciting RAs" >&2
      # Waits for the answers, so never in the event loop
      systemctl start --no-block gbmc-br-port-rs@"$intf".service || true
    fi
  elif [[ $change == route && $route != *' table '* &&
          $route =~ ^([^ ]+/80)' dev gbmcbr proto ra ' ]]; then
    local pfx="${BASH_REMATCH[1]}"
    # Deletions get a grace period, see the comment on GBMC_BR_FROM_RA_CONFS
    # shellcheck disable=SC2154
    if [[ $action == add ]]; then
      gbmc_ip_monitor_grace_clear gbmc_br_from_ra_gone "$pfx"
      if [[ -z ${gbmc_br_from_ra_pfxs["$pfx"]-} ]]; then
        gbmc_br_from_ra_pfxs["$pfx"]=1
        gbmc_ip_monitor_defer
      fi
    elif [[ -n ${gbmc_br_from_ra_pfxs["$pfx"]-} ]]; then
      gbmc_ip_monitor_grace_start gbmc_br_from_ra_gone "$pfx" \
        "$GBMC_BR_RA_GRACE" from-ra-addr
    fi
  elif [[ $change == route && $route =~ ^([^ ]+/80)' via '([^ ]+)' dev gbmcbr proto ra ' ]]; then
    local pfx="${BASH_REMATCH[1]}"
    local gw="${BASH_REMATCH[2]}"
    if [[ $action == add ]]; then
      gbmc_ip_monitor_grace_clear gbmc_br_from_ra_routes_gone "$pfx"
      if [[ ${gbmc_br_from_ra_routes["$pfx"]-} != "$gw" ]]; then
        gbmc_br_from_ra_routes["$pfx"]="$gw"
        gbmc_ip_monitor_defer
      fi
    elif [[ -n ${gbmc_br_from_ra_routes["$pfx"]-} ]]; then
      gbmc_ip_monitor_grace_start gbmc_br_from_ra_routes_gone "$pfx" \
        "$GBMC_BR_RA_GRACE" from-ra-route
    fi
  elif [[ $change == link && $intf == gbmcbr ]]; then
    # Asynchronous, and coalesced while one is still running
    systemctl start --no-block gbmc-br-port-rs@gbmcbr.service || true
    if [[ $action == add && $mac != "$gbmc_br_from_ra_mac" ]]; then
      gbmc_br_from_ra_mac="$mac"
      gbmc_ip_monitor_defer
    fi
    if [[ $action == del && $mac == "$gbmc_br_from_ra_mac" ]]; then
      gbmc_br_from_ra_mac=
      gbmc_ip_monitor_defer
    fi
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_from_ra_hook)

gbmc_br_from_ra_lib=1

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

gbmc_br_from_ra_init=
gbmc_br_from_ra_mac=
# RA prefixes we configure addresses in
declare -A gbmc_br_from_ra_pfxs=()
# RA prefix -> uptime deadline for its route to come back, see below
declare -A gbmc_br_from_ra_gone=()
declare -A gbmc_br_from_ra_prev_addrs=()

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
# its route stayed gone for GBMC_BR_FROM_RA_GRACE seconds. This also covers
# prefixes networkd removes because they were withdrawn or expired.
GBMC_BR_FROM_RA_GRACE=5
GBMC_BR_FROM_RA_CONFS=(/run/systemd/network/{00,}-bmc-gbmcbr.network.d/65-ra-addrs.conf)

# Writes the networkd config for the given addresses, only reloading networkd
# when the set of addresses actually changes.
gbmc_br_from_ra_write_conf() {
  local contents=
  local addr
  while read -r addr; do
    [[ -n $addr ]] || continue
    contents+="[Address]"$'\n'"Address=$addr"$'\n'"AddPrefixRoute=no"$'\n'
  done < <(printf '%s\n' "$@" | sort)

  local changed=
  local file
  for file in "${GBMC_BR_FROM_RA_CONFS[@]}"; do
    if [[ -z $contents ]]; then
      [[ -e $file ]] || continue
      rm -f "$file"
    else
      [[ "$(cat "$file" 2>/dev/null)"$'\n' == "$contents" ]] && continue
      mkdir -p "$(dirname "$file")"
      printf '%s' "$contents" >"$file.tmp"
      mv -f "$file.tmp" "$file"
    fi
    changed=1
  done
  if [[ -n $changed ]]; then
    echo "gBMC Bridge RA addresses changed: ${*:-none}" >&2
    # shellcheck disable=SC2119
    gbmc_net_networkd_reload
  fi
}

gbmc_br_from_ra_update() {
  [[ -n $gbmc_br_from_ra_init && -n $gbmc_br_from_ra_mac ]] || return

  local now
  gbmc_ip_monitor_uptime now || return
  local next=
  local -a addrs=()
  local pfx
  for pfx in "${!gbmc_br_from_ra_pfxs[@]}"; do
    local cidr
    if ! cidr="$(ip_pfx_to_cidr "$pfx")"; then
      unset 'gbmc_br_from_ra_pfxs[$pfx]'
      continue
    fi
    if (( cidr == 80 )); then
      local sfx
      if ! sfx="$(mac_to_eui48 "$gbmc_br_from_ra_mac")"; then
        unset 'gbmc_br_from_ra_pfxs[$pfx]'
        continue
      fi
      local addr
      if ! addr="$(ip_pfx_concat "$pfx" "$sfx")"; then
        unset 'gbmc_br_from_ra_pfxs[$pfx]'
        continue
      fi
    else
      unset 'gbmc_br_from_ra_pfxs[$pfx]'
      continue
    fi
    local deadline="${gbmc_br_from_ra_gone["$pfx"]-}"
    if [[ -n $deadline ]] && (( deadline <= now )); then
      unset 'gbmc_br_from_ra_gone[$pfx]'
      # Make sure we didn't miss it coming back
      if [[ -z "$(ip -6 route show "$pfx" dev gbmcbr proto ra 2>/dev/null)" ]]; then
        echo "gBMC Bridge RA Addr Del: $addr (pfx $pfx label 99)" >&2
        unset 'gbmc_br_from_ra_prev_addrs[$addr]'
        ip addr del "$addr" dev gbmcbr 2>/dev/null || true
        ip addrlabel del prefix "$pfx" label 99 2>/dev/null || true
        unset 'gbmc_br_from_ra_pfxs[$pfx]'
        continue
      fi
    elif [[ -n $deadline ]]; then
      if [[ -z $next ]] || (( deadline < next )); then
        next=$deadline
      fi
    fi
    addrs+=("$addr")
    if [[ -z ${gbmc_br_from_ra_prev_addrs["$addr"]-} ]]; then
      echo "gBMC Bridge RA Addr Add: $addr (pfx $pfx label 99)" >&2
      gbmc_br_from_ra_prev_addrs["$addr"]=1
      # Usable right away, networkd takes it over once it reloads
      ip addr replace "$addr" dev gbmcbr noprefixroute
      ip addrlabel add prefix "$pfx" label 99 2>/dev/null || true
    fi
  done
  if [[ -n $next ]]; then
    gbmc_ip_monitor_timer from-ra $(( next - now ))
  else
    gbmc_ip_monitor_timer_cancel from-ra
  fi
  gbmc_br_from_ra_write_conf "${addrs[@]}"
}

gbmc_br_from_ra_hook() {
  # shellcheck disable=SC2154
  if [[ $change == init ]]; then
    gbmc_br_from_ra_init=1
    gbmc_ip_monitor_defer
  elif [[ $change == defer ]]; then
    gbmc_br_from_ra_update
  elif [[ $change == timer && $timer == from-ra ]]; then
    gbmc_br_from_ra_update
  elif [[ $change == route && $route != *' via '* ]] &&
       [[ $route == *' dev gbmcbr proto ra '* ]]; then
    local pfx="${route%% *}"
    # Deletions get a grace period, see the comment on GBMC_BR_FROM_RA_CONFS
    # shellcheck disable=SC2154
    if [[ $action == add ]]; then
      unset 'gbmc_br_from_ra_gone[$pfx]'
      if [[ -z ${gbmc_br_from_ra_pfxs["$pfx"]-} ]]; then
        gbmc_br_from_ra_pfxs["$pfx"]=1
        gbmc_ip_monitor_defer
      fi
    elif [[ -n ${gbmc_br_from_ra_pfxs["$pfx"]-} ]]; then
      [[ -z ${gbmc_br_from_ra_gone["$pfx"]-} ]] || return 0
      local now
      gbmc_ip_monitor_uptime now || return
      gbmc_br_from_ra_gone["$pfx"]=$(( now + GBMC_BR_FROM_RA_GRACE ))
      gbmc_ip_monitor_timer from-ra "$GBMC_BR_FROM_RA_GRACE"
    fi
  elif [[ $change == link && $intf == gbmcbr ]]; then
    rdisc6 -m gbmcbr -r 1 -w 100 >/dev/null 2>&1
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

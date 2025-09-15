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

gbmc_br_from_ra_init=
gbmc_br_from_ra_mac=
declare -A gbmc_br_from_ra_pfxs=()
declare -A gbmc_br_from_ra_prev_addrs=()

gbmc_br_from_ra_update() {
  [[ -n $gbmc_br_from_ra_init && -n $gbmc_br_from_ra_mac ]] || return

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
    local valid="${gbmc_br_from_ra_pfxs["$pfx"]}"
    if (( valid > 0 )); then
      if [[ -z ${gbmc_br_from_ra_prev_addrs["$addr"]-} ]]; then
        echo "gBMC Bridge RA Addr Add: $addr" >&2
        gbmc_br_from_ra_prev_addrs["$addr"]=1
        ip addr replace "$addr" dev gbmcbr noprefixroute
      elif ! ip addr show dev gbmcbr | grep -q "$addr"; then
        echo "gBMC Bridge RA missing addr add: $addr" >&2
        ip addr replace "$addr" dev gbmcbr noprefixroute
      fi
    else
      if [[ -n ${gbmc_br_from_ra_prev_addrs["$addr"]-} ]]; then
        echo "gBMC Bridge RA Addr Del: $addr" >&2
        unset 'gbmc_br_from_ra_prev_addrs[$addr]'
      fi
      ip addr del "$addr" dev gbmcbr
      unset 'gbmc_br_from_ra_pfxs[$pfx]'
    fi
  done
}

gbmc_br_from_ra_hook() {
  # shellcheck disable=SC2154
  if [[ $change == init ]]; then
    gbmc_br_from_ra_init=1
    gbmc_ip_monitor_defer
  elif [[ $change == defer ]]; then
    gbmc_br_from_ra_update
  elif [[ $change == route && $route != *' via '* ]] &&
       [[ $route =~ ^(.* dev gbmcbr proto ra .*)( +expires +([^ ]+)sec).*$ ]]; then
    pfx="${route%% *}"
    # shellcheck disable=SC2154
    if [[ $action == add ]]; then
      gbmc_br_from_ra_pfxs["$pfx"]="${BASH_REMATCH[3]}"
      gbmc_ip_monitor_defer
    elif [[ $action == del ]]; then
      gbmc_br_from_ra_pfxs["$pfx"]=0
      gbmc_ip_monitor_defer
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

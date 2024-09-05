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

[[ -n ${gbmc_br_ula_lib-} ]] && return

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit

declare -A gbmc_br_ulas=()

# BITs set for address suffixes
GBMC_BR_ULA_SFX_HAS_LL=1
GBMC_BR_ULA_SFX_HAS_ULA=2

gbmc_br_ula_cleanup() {
  local addr
  for addr in "${!gbmc_br_ulas[@]}"; do
    local val="${gbmc_br_ulas["$addr"]}"
    if (( val & GBMC_BR_ULA_SFX_HAS_LL == 0 )); then
      echo "Removing Stale ULA: $addr" >&2
      ip addr del "$addr"/64 dev gbmcbr || true
    fi
  done
}

gbmc_br_ula_is_ll() {
  # shellcheck disable=SC2178
  local -n bytes="$1"
  (( bytes[0] == 0xfe && bytes[1] == 0x80 && bytes[2] == 0x00 &&
     bytes[3] == 0x00 && bytes[4] == 0x00 && bytes[5] == 0x00 &&
     bytes[6] == 0x00 && bytes[7] == 0x00 ))
}

gbmc_br_ula_is_ula() {
  # shellcheck disable=SC2178
  local -n bytes="$1"
  (( bytes[0] == 0xfd && bytes[1] == 0xb5 && bytes[2] == 0x04 &&
     bytes[3] == 0x81 && bytes[4] == 0x10 && bytes[5] == 0xce &&
     bytes[6] == 0x00 && bytes[7] == 0x00 ))
}

gbmc_br_ula_hook() {
  # shellcheck disable=SC2154
  if [[ $change == init ]]; then
    gbmc_br_ula_cleanup
  elif [[ $change == addr && $intf == gbmcbr && $fam == inet6 ]]; then
    local pfx_bytes=()
    ip_to_bytes pfx_bytes "$ip" || return
    local val=0
    if gbmc_br_ula_is_ll pfx_bytes; then
      val="$GBMC_BR_ULA_SFX_HAS_LL"
    elif gbmc_br_ula_is_ula pfx_bytes; then
      val="$GBMC_BR_ULA_SFX_HAS_ULA"
    else
      return 0
    fi
    # Force all addresses into what they would be as a ULA so that we can
    # store bits about the assigned addresses on the interface
    pfx_bytes[0]=0xfd
    pfx_bytes[1]=0xb5
    pfx_bytes[2]=0x04
    pfx_bytes[3]=0x81
    pfx_bytes[4]=0x10
    pfx_bytes[5]=0xce
    addr="$(ip_bytes_to_str pfx_bytes)"
    local old=${gbmc_br_ulas["$addr"]-0}
    if [[ $action == add ]]; then
      val=$((old | val))
    elif [[ $action == del ]]; then
      val=$((old & ~val))
    fi
    gbmc_br_ulas["$addr"]=$val
    if (( val == GBMC_BR_ULA_SFX_HAS_LL )); then
      # We have a link local address but no ULA, so we need to add the ULA
      echo "Adding ULA: $addr" >&2
      ip addr replace "$addr"/64 dev gbmcbr
    elif (( val == GBMC_BR_ULA_SFX_HAS_ULA )); then
      # We have a ULA without a link local, so we should not longer have this ULA
      echo "Removing ULA: $addr" >&2
      ip addr del "$addr"/64 dev gbmcbr || true
    elif (( val == 0 )); then
      # Cleanup the map if we no longer have any addresses for the suffix
      unset 'gbmc_br_ulas[$addr]'
    fi
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_ula_hook)

gbmc_br_ula_lib=1

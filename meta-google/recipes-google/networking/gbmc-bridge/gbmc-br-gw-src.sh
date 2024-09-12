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

[[ -n ${gbmc_br_gw_src_lib-} ]] && return

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit
# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source /usr/share/gbmc-net-lib.sh || exit

declare -A gbmc_br_gw_src_ips=()
declare -A gbmc_br_gw_src_routes=()
gbmc_br_gw_defgw=

gbmc_br_set_router() {
  local defgw=
  local route
  for route in "${!gbmc_br_gw_src_routes[@]}"; do
    if [[ $route != *' dev gbmcbr '* ]]; then
      defgw=1
      break
    fi
  done
  # Make becoming a router sticky, if we ever have a default route we are
  # always treated as a router. Otherwise, we end up reloading unnecessarily
  # a number of times. The reload causes the network configuration to be
  # reappplied with packet drops for a short amount of time.
  [[ -z $defgw ]] && return
  [[ $defgw == "$gbmc_br_gw_defgw" ]] && return
  gbmc_br_gw_defgw="$defgw"

  local files=(/run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-defgw.conf)
  if [[ -n $defgw ]]; then
    local file
    for file in "${files[@]}"; do
      mkdir -p "$(dirname "$file")"
      printf '[IPv6SendRA]\nRouterLifetimeSec=120\n' >"$file"
    done
  else
    rm -f "${files[@]}"
  fi

  # shellcheck disable=SC2119
  gbmc_net_networkd_reload
}

gbmc_br_gw_src_update() {
  # Pick the shortest address, we always want to use the most root level
  # The order of preference looks roughly like
  #   1. Root /64 address (2620:15c:2c3:aaae::/64)
  #      This is generally used by the OOB RJ45 port and is our primary preference
  #   2. BMC subordonate root (2620:15c:2c3:aaae:fd01::/80)
  #      From the NIC over NCSI with the /64 shared with the CN
  #   3. BMC stateless (2620:15c:2c3:aaae:fd00:3c8d:20dc:263e/80)
  #      From the NIC, but derived from the MAC and typically never used
  #
  local new_src=
  local new_len=16
  local ip
  for ip in "${!gbmc_br_gw_src_ips[@]}"; do
    local ip_len="${gbmc_br_gw_src_ips["$ip"]}"
    if (( ip_len < new_len )); then
      new_src="$ip"
      new_len="$ip_len"
    fi
  done
  (( new_len >= 16 )) && return

  local route
  for route in "${!gbmc_br_gw_src_routes[@]}"; do
    [[ $route != *" src $new_src "* ]] || continue
    echo "gBMC Bridge Updating GW source [$new_src]: $route" >&2
    # shellcheck disable=SC2086
    ip route change $route src "$new_src" && \
      unset 'gbmc_br_gw_src_routes[$route]'
  done
}

gbmc_br_gw_src_hook() {
  # We only want to match default gateway routes that are dynamic
  # (have an expiration time). These will be updated with our preferred
  # source.
  # shellcheck disable=SC2154
  if [[ $change == route && $route == 'default '*':'* ]]; then
    if [[ $route =~ ^(.*)( +expires +[^ ]+)(.*)$ ]]; then
      route="${BASH_REMATCH[1]}${BASH_REMATCH[3]}"
    fi
    if [[ $action == add && -z ${gbmc_br_gw_src_routes["$route"]} ]]; then
      gbmc_br_gw_src_routes["$route"]=1
      gbmc_br_gw_src_update
      gbmc_br_set_router
    elif [[ $action == del && -n "${gbmc_br_gw_src_routes["$route"]}" ]]; then
      unset 'gbmc_br_gw_src_routes[$route]'
      gbmc_br_gw_src_update
      gbmc_br_set_router
    fi
  # Match only global IP addresses on the bridge that are non-ULA addresses.
  # So 2002:af4:3480:2248:fd00:6345:3069:9186 would be
  # matched as the preferred source IP for outoging traffic.
  elif [[ $change == addr && $intf == gbmcbr && $scope == global ]] &&
       [[ $fam == inet6 && $flags != *tentative* ]]; then
    local ip_bytes=()
    if ! ip_to_bytes ip_bytes "$ip"; then
      echo "gBMC Bridge Ensure RA Invalid IP: $ip" >&2
      return 1
    fi
    # Ignore ULAs
    if (( (ip_bytes[0] & 0xfe) == 0xfc )); then
      return 0
    fi
    if [[ $action == add ]]; then
      local i=0
      local non_zero=0
      for (( i=0; i<16; ++i )); do
        if (( ip_bytes[i] != 0 )); then
          non_zero="$i"
        fi
      done
      gbmc_br_gw_src_ips["$ip"]="$non_zero"
    elif [[ $action == del ]]; then
      unset 'gbmc_br_gw_src_ips[$ip]'
    fi
    gbmc_br_gw_src_update
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_gw_src_hook)

gbmc_br_gw_src_lib=1

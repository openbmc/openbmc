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

[ -z "${gbmc_br_gw_src_lib-}" ] || return

source /usr/share/network/lib.sh || exit

gbmc_br_gw_src_ip=
declare -A gbmc_br_gw_src_routes=()

gbmc_br_gw_src_update() {
  [ -n "$gbmc_br_gw_src_ip" ] || return

  local route
  for route in "${!gbmc_br_gw_src_routes[@]}"; do
    [[ "$route" != *" src $gbmc_br_gw_src_ip "* ]] || continue
    echo "gBMC Bridge Updating GW source [$gbmc_br_gw_src_ip]: $route" >&2
    ip route change $route src "$gbmc_br_gw_src_ip"
    unset 'gbmc_br_gw_src_routes[$route]'
  done
}

gbmc_br_gw_src_hook() {
  # We only want to match default gateway routes that are dynamic
  # (have an expiration time). These will be updated with our preferred
  # source.
  if [[ "$change" == 'route' && "$route" == 'default '*':'* ]]; then
    if [[ "$route" =~ ^(.*)( +expires +[^ ]+)(.*)$ ]]; then
      route="${BASH_REMATCH[1]}${BASH_REMATCH[3]}"
    fi
    if [ "$action" = 'add' -a -z "${gbmc_br_gw_src_routes["$route"]}" ]; then
      gbmc_br_gw_src_routes["$route"]=1
      gbmc_br_gw_src_update
    elif [ "$action" = 'del' -a -n "${gbmc_br_gw_src_routes["$route"]}" ]; then
      unset 'gbmc_br_gw_src_routes[$route]'
      gbmc_br_gw_src_update
    fi
  # Match only global IP addresses on the bridge that match the BMC stateless
  # prefix (<mpfx>:fd00:). So 2002:af4:3480:2248:fd00:6345:3069:9186 would be
  # matched as the preferred source IP for outoging traffic.
  elif [ "$change" = 'addr' -a "$intf" = 'gbmcbr' -a "$scope" = 'global' ] &&
       [[ "$fam" == 'inet6' && "$flags" != *tentative* ]]; then
    local ip_bytes=()
    if ! ip_to_bytes ip_bytes "$ip"; then
      echo "gBMC Bridge Ensure RA Invalid IP: $ip" >&2
      return 1
    fi
    if (( ip_bytes[8] != 0xfd || ip_bytes[9] != 0 )); then
      return 0
    fi
    if [ "$action" = 'add' -a "$ip" != "$gbmc_br_gw_src_ip" ]; then
      gbmc_br_gw_src_ip="$ip"
      gbmc_br_gw_src_update
    fi
    if [ "$action" = 'del' -a "$ip" = "$gbmc_br_gw_src_ip" ]; then
      gbmc_br_gw_src_ip=
    fi
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_gw_src_hook)

gbmc_br_gw_src_lib=1

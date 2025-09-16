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

# primary IP priority should be higher than NCSI and NIC
primary_rt_metric=700

# For tray, priorize the route sent by host BMC
primary_ip_from_br=""

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

gbmc_br_config_primary_ip() {
   local route=$1
   local op=$2

   local route_new
   route_new=$(echo "$route" | sed -E 's/ metric [0-9]+//')
   if [[ "$op" == "del" ]]; then
     # shellcheck disable=SC2086
     ip route "$op" $route_new metric "$primary_rt_metric" 2>/dev/null
   else
     # shellcheck disable=SC2086
     ip route "$op" $route_new metric "$primary_rt_metric"
   fi
}

# Notify the tray BMC with the preferred src ip
primary_route_to_br_update() {
  local rt
  local need_reload=0
  local new_src=""
  local dev=""
  # no need to notify if it does not have default gw
  rt="$(ip -6 route get 2000:: 2>/dev/null)"

  if [[ "$rt" =~ dev' '([^ ]+).*' src '([^ ]+) ]]; then
    dev="${BASH_REMATCH[1]}"
    [[ "$dev" != "gbmcbr" ]] && new_src="${BASH_REMATCH[2]}"
  fi

  if [[ -z "$new_src" ]]; then
    for file in /run/systemd/network/{00,}-bmc-gbmcbr.network.d/70-ip-hybrid-route.conf; do
      [ -f "$file" ] || continue
      rm -rf "$file"
      need_reload=1
    done
  else
    read -r -d '' notify_route <<EOF
[IPv6RoutePrefix]
Route=$new_src/124
LifetimeSec=90
EOF
    for file in /run/systemd/network/{00,}-bmc-gbmcbr.network.d/70-ip-hybrid-route.conf; do
      grep -q "Route=$new_src/124" "$file" 2>/dev/null && continue
      mkdir -p "$(dirname "$file")"
      printf '%s\n' "$notify_route" >"$file"
      need_reload=1
    done
  fi

  # shellcheck disable=SC2119
  (( need_reload == 1 )) && gbmc_net_networkd_reload
}

gbmc_br_gw_src_update() {
  local route
  local ip
  # {dev}-{ip} -> {metric}
  local -A dev_ip_to_metric=()
  for ip in "${!gbmc_br_gw_src_ips[@]}"; do
    local rt
    rt="$(ip -6 route get 2000:: from "$ip" 2>/dev/null)"
    [[ "$rt" =~ ' dev '([^ ]+).*' metric '([^ ]+) ]] || continue
    local rt_dev="${BASH_REMATCH[1]}"
    local rt_metric="${BASH_REMATCH[2]}"
    dev_ip_to_metric["$rt_dev-$ip"]+="$rt_metric"
  done
  local primary_ip
  primary_ip=$(cat /var/google/gbmc-br-ip 2>/dev/null)
  for route in "${!gbmc_br_gw_src_routes[@]}"; do
    local new_src
    local new_len=16
    local new_metric=4096
    [[ "$route" =~ dev' '([^ ]+) ]] || continue
    local dev="${BASH_REMATCH[1]}"
    for ip in "${!gbmc_br_gw_src_ips[@]}"; do
      # Prioritize the IP with the lowest route metric, as we want specific
      # source routes to take precedence in selection. If they are the same,
      # pick the shortest address. We prefer primary IP address from DHCP.
      # Types of IP are:
      #   1. Root /64 address (2620:15c:2c3:aaae::/64)
      #      This is generally used by the OOB RJ45 port and is our primary preference
      #   2. BMC subordonate root (2620:15c:2c3:aaae:fd01::/80)
      #      From the NIC over NCSI with the /64 shared with the CN
      #   3. BMC stateless (2620:15c:2c3:aaae:fd00:3c8d:20dc:263e/80)
      #      From the NIC, but derived from the MAC and typically never used
      local metric="${dev_ip_to_metric["$dev-$ip"]-4097}"
      local ip_len="${gbmc_br_gw_src_ips["$ip"]}"
      if (( metric < new_metric || (metric == new_metric && ip_len < new_len) )); then
        new_src="$ip"
        new_len="$ip_len"
        new_metric="$metric"
      elif (( metric == new_metric && ip_len == 9 && new_len == 9 )); then
        # 4. for hybrid mode we might get 2 ip with the same length and same
        # metrics, we prefer the primary IP here. For tray BMC, prefer the
        # primary ip from the host BMC.
        # Real OOB for tray should have higher metrics and that will be always
        # priorized and is not affected by this.
        if [[ -z "$primary_ip_from_br" ]]; then
          [[ "$ip" == "$primary_ip" ]] && new_src="$ip"
        else
          # compare the first 8 bytes
          local ip_bytes=()
          local primary_ip_from_br_bytes=()
          local i
          local equal=1
          ip_to_bytes ip_bytes "$ip"
          ip_to_bytes primary_ip_from_br_bytes "$primary_ip_from_br"

          for (( i=0; i<8; ++i )); do
            if (( ip_bytes[i] != primary_ip_from_br_bytes[i] )); then
              equal=0
              break
            fi
          done

          (( equal == 1 )) && new_src="$ip"
        fi
      fi
    done
    (( new_len >= 16 )) && continue
    if [[ "$new_src" == "$primary_ip" ]]; then
      # Add primary src ip for the GW
      gbmc_br_config_primary_ip "$route src $new_src" "replace"
    fi
    [[ $route != *" src $new_src "* ]] || continue
    echo "gBMC Bridge Updating GW source [$new_src]: $route" >&2
    gbmc_br_config_primary_ip "$route" "del"
    # shellcheck disable=SC2086
    ip route change $route src "$new_src" && unset 'gbmc_br_gw_src_routes[$route]'
  done
  primary_route_to_br_update
}

gbmc_br_gw_src_hook() {
  # We only want to match default gateway routes that are dynamic
  # (have an expiration time). These will be updated with our preferred
  # source.
  # shellcheck disable=SC2154
  if [[ $change == route && $route == 'default '*':'* ]]; then
    # ignore everything except main table
    [[ $route == *" table "* ]] && return
    # ignore the primary route as this script fully controls it
    [[ $route == *" metric $primary_rt_metric "* ]] && return
    if [[ $route =~ ^(.*)( +expires +[^ ]+)(.*)$ ]]; then
      route="${BASH_REMATCH[1]}${BASH_REMATCH[3]}"
    fi
    if [[ $route =~ ^(.*)( +proto +[^ ]+)(.*)$ ]]; then
      route="${BASH_REMATCH[1]}${BASH_REMATCH[3]}"
    fi
    if [[ $action == add && -z ${gbmc_br_gw_src_routes["$route"]} ]]; then
      gbmc_br_gw_src_routes["$route"]=1
      gbmc_br_gw_src_update
      gbmc_br_set_router
    elif [[ $action == del && -n "${gbmc_br_gw_src_routes["$route"]}" ]]; then
      if [[ $route =~ ' src '([^ ]+) ]]; then
        gbmc_br_config_primary_ip "$route" "del"
      fi
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
  # check route on gbmcbr with /124 address
  elif [[ $change == route && $route =~ ^[0-9a-f:]+'/124 '.*' dev gbmcbr ' ]]; then
    local expires='0sec'
    if [[ $route =~ ^(.*)( +expires +[^ ]+)(.*)$ ]]; then
      route="${BASH_REMATCH[1]}${BASH_REMATCH[3]}"
      expires="${BASH_REMATCH[2]}"
    fi
    local brip=${route%%/124 *}
    if [[ $action == add ]]; then
      [[ "$brip" == "$primary_ip_from_br" ]] && return
      echo "Change preferred src from bridge RA: $route" >&2
      primary_ip_from_br="$brip"
      gbmc_br_gw_src_update
    elif [[ $action == del ]]; then
      # Every RA will trigger a delete and re-add. Only delete when the route is
      # truly expired to prevent redundant config.
      [[ "$expires" != "0sec" ]] && return
      [[ "$brip" == "$primary_ip_from_br" ]] || return
      echo "Remove preferred src from bridge RA: $route $expires" >&2
      primary_ip_from_br=''
      gbmc_br_gw_src_update
    fi
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_gw_src_hook)

gbmc_br_gw_src_lib=1

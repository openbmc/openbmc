#!/bin/bash
# Copyright 2025 Google LLC
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

[[ -n ${gbmc_br_dhcrelay_lib-} ]] && return

# shellcheck disable=SC2034
declare -A gbmc_br_dhcrelay_linkaddrs
gbmc_br_dhcrelay_dirty="${gbmc_br_dhcrelay_dirty-}"

gbmc_br_dhcrelay_hook() {
  # shellcheck disable=SC2154
  if [[ $change == defer ]]; then
    if [[ -n ${gbmc_br_dhcrelay_dirty-} ]]; then
      gbmc_br_dhcrelay_dirty=
      systemctl restart --no-block gbmc-br-dhcrelay.service
    fi
  elif [[ $change == addr && $scope == link && $fam == inet6 ]]; then
    local -n addrs=gbmc_br_dhcrelay_linkaddrs["$intf"]
    if [[ -z ${addrs+1} ]]; then
      return 0
    fi
    [[ $addrs != 0 ]] || addrs=
    local had_addrs=0
    [[ -n $addrs ]] && had_addrs=1
    if [[ $action == add && $flags != *tentative* ]]; then
      [[ " $addrs " != *" $ip "* ]] || return 0
      addrs="${addrs:+$addrs }$ip"
    else
      [[ " $addrs " == *" $ip "* ]] || return 0
      local padded=" $addrs "
      padded="${padded//" $ip "/ }"
      padded="${padded# }"
      addrs="${padded% }"
    fi
    local has_addrs=0
    [[ -n $addrs ]] && has_addrs=1
    (( had_addrs != has_addrs )) || return 0
    local dir=/run/gbmc-br-dhcrelay/uppers
    local file="$dir/$intf"
    if (( has_addrs == 1 )); then
      local val="ff02::1:2%$intf"
      if [[ -f $file && $(<"$file") == "$val" ]]; then
        return 0
      fi
      local tmp="$dir/.$intf.tmp.$BASHPID"
      [[ -d $dir ]] || mkdir -p "$dir"
      printf '%s' "$val" >"$tmp" && mv -f "$tmp" "$file"
      echo "Adding dhcrelay upper $intf" >&2
    else
      [[ -e $file ]] || return 0
      rm -f "$file"
      echo "Removing dhcrelay upper $intf" >&2
    fi
    gbmc_br_dhcrelay_dirty=1
    gbmc_ip_monitor_defer
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_dhcrelay_hook)

gbmc_br_dhcrelay_lib=1

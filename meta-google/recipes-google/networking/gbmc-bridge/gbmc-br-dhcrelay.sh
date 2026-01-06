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

gbmc_br_dhcrelay_hook() {
  # shellcheck disable=SC2154
  if [[ $change == addr && $scope == link && $fam == inet6 && $flags != *tentative* ]]; then
    local -n numaddrs=gbmc_br_dhcrelay_linkaddrs["$intf"]
    if [[ -z ${numaddrs+1} ]]; then
      return 0
    fi
    if [[ $action == add ]]; then
      (( numaddrs += 1 ))
    else
      (( numaddrs -= 1 ))
    fi
    local svc=
    svc=gbmc-br-dhcrelay.service
    file=/run/gbmc-br-dhcrelay/uppers/"$intf"
    if (( numaddrs == 1 )); then
      mkdir -p "$(dirname "$file")"
      printf 'ff02::1:2%%%s' "$intf" >"$file"
      echo "Adding dhcrelay upper $intf" >&2
    elif (( numaddrs == 0 )); then
      rm -f "$file"
      echo "Removing dhcrelay upper $intf" >&2
    fi
    systemctl reset-failed "$svc"
    systemctl restart --no-block "$svc"
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_dhcrelay_hook)

gbmc_br_dhcrelay_lib=1

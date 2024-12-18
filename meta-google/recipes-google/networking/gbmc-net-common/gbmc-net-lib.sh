#!/bin/bash
# Copyright 2024 Google LLC
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

[ -n "${gbmc_net_lib_init-}" ] && return

gbmc_net_networkd_reload() {
  local retry=0
  while true; do
    (( retry = retry + 1 ))
    (( retry <= 3 )) || return 1
    # sleep 3s if not first time
    (( retry == 1 )) || sleep 3
    if [ "$(systemctl is-active systemd-networkd)" != 'inactive' ]; then
      echo "Reloading networkd + reconfiguring ($*) from $(caller 0), time $retry" >&2
      networkctl reload || continue
      local intf
      for intf in "$@"; do
        networkctl reconfigure "$intf" || continue 2
      done
    fi
    return 0
  done
}

gbmc_net_lib_init=1

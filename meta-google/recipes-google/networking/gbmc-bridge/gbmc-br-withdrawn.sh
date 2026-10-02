#!/bin/bash
# Copyright 2026 Google LLC
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

[[ -n ${gbmc_br_withdrawn_lib-} ]] && return

# shellcheck source=meta-google/recipes-google/networking/gbmc-bridge/gbmc-br-lib.sh
source /usr/share/gbmc-br-lib.sh || exit

# Cleans up the prefix withdrawals from gbmc_br_ra_withdraw() once they
# expire. They are written by whatever changes our gbmcbr addresses, which
# always reloads networkd and so generates gbmcbr route events.
gbmc_br_withdrawn_hook() {
  # shellcheck disable=SC2154
  if [[ $change == init || $change == timer && $timer == gbmc-br-withdrawn ]] ||
     [[ $change == route && $route == *' dev gbmcbr '* ]]; then
    local next
    gbmc_br_ra_withdrawn_expire next || return
    [[ -n $next ]] || return 0
    local now
    gbmc_ip_monitor_uptime now || return
    gbmc_ip_monitor_timer gbmc-br-withdrawn $(( next - now ))
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_withdrawn_hook)

gbmc_br_withdrawn_lib=1

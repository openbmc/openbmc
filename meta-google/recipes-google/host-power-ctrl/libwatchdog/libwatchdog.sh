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

##################################################
# Stop the host watchdog
# Return:
#   0 if success, non-zero if error
##################################################
stop_host_watchdog() {
  if (( $# != 0 )); then
    echo 'Usage: stop_host_watchdog' >&2
    return 1
  fi

  local srv='xyz.openbmc_project.Watchdog'
  local obj='/xyz/openbmc_project/watchdog/host0'
  local intf='xyz.openbmc_project.State.Watchdog'
  local method='Enabled'
  local args=('b' 'false')
  busctl set-property "${srv}" "${obj}" "${intf}" "${method}" "${args[@]}"
}

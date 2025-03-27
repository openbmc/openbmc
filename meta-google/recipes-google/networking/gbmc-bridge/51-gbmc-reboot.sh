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

[ -n "${gbmc_reboot-}" ] && return

gbmc_reboot_needed=

gbmc_reboot_hook() {
  update_netboot_status "reboot_check" "reboot hook starting" "START"
  # We don't always need a warm reset, allow skipping
  if [ -z "${gbmc_reboot_needed-}" ]; then
    update_netboot_status "reboot_check" "Skipping bmc reboot" "SUCCESS"
    return 0
  fi
  update_netboot_status "reboot_check" "Reboot is needed" "SUCCESS"

  update_netboot_status "reboot" "triggerring bmc reboot" "START"
  update_netboot_status "reboot" "About to reboot" "SUCCESS"
  reboot -f
  exit 0
}

GBMC_BR_DHCP_HOOKS+=(gbmc_reboot_hook)

gbmc_reboot=1

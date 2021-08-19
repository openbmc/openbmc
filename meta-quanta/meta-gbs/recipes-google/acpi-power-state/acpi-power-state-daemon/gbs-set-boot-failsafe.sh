#!/bin/bash
# Copyright 2021 Google LLC
# Copyright 2021 Quanta Computer Inc.
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

main() {
  local pgd_val
  pgd_val="$(busctl get-property -j xyz.openbmc_project.State.Chassis \
           /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis \
           CurrentPowerState | jq -r '.["data"]')"

  if [[ $pgd_val != 'xyz.openbmc_project.State.Chassis.PowerState.On' ]]; then
    echo "Setting failsafe assuming host is off" >&2
    systemctl start --no-block gbs-host-s5-set-failsafe
  else
    echo "Setting failsafe assuming host is running" >&2
    systemctl start --no-block gbs-host-s0-set-failsafe
  fi
}

# Exit without running main() if sourced
return 0 2>/dev/null

main "$@"

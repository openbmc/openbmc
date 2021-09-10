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


target_pwm="$1"

if [ -z "$target_pwm" ]; then
  echo "Target_pwm is not set" >&2
  exit 1
fi

zone_num="$(busctl tree xyz.openbmc_project.State.FanCtrl | grep zone | wc -l)"
result=0

for (( i = 0; i < ${zone_num}; i++ )); do
  retries=4
  busctl_error=-1

  while (( retries > 0 )) && (( busctl_error != 0 )); do
    busctl set-property xyz.openbmc_project.State.FanCtrl /xyz/openbmc_project/settings/fanctrl/zone${i} xyz.openbmc_project.Control.FanSpeed Target t "${target_pwm}"
    busctl_error=$?

    if (( busctl_error != 0 )); then
      #Retry setting failsafe. Swampd may be running but zone aren't yet built
      #so sleep a second to let them be built
      sleep 1
    fi

    let retries-=1
  done

  if (( busctl_error != 0 )); then
    echo "Failure setting zone${i} fan failsafe to ${target_pwm}" >&2
    result=$busctl_error
  else
    echo "Setting zone${i} fan failsafe to ${target_pwm}"
  fi
done

exit $result

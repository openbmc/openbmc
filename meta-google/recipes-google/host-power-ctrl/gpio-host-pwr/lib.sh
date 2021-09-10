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

# This is intended to be used as a library for managing gpio line values.
# Executing this directly will do nothing.
[ -n "${host_pwr_init-}" ] && return

source /usr/share/gpio-ctrl/lib.sh || exit

# Read by the tooling to determine if the machine is powered on or off
HOST_GPIO_PGOOD='unset'
# Set according to whether the host is powered on or off
HOST_LED_PWR=''
# Written by the tooling to assert the power button
HOST_GPIO_PWR_BTN='unset'
# Written by the tooling to assert a cold reset
HOST_GPIO_COLD_RESET='unset'
# Written by the tooling to assert a warm reset
HOST_GPIO_WARM_RESET='unset'

# Load configurations from a known location in the filesystem to populate
# named GPIOs
shopt -s nullglob
for conf in /usr/share/gpio-host-pwr/conf.d/*.sh; do
  source "$conf"
done

##################################################
# Stop the host watchdog
# Return:
#   0 if success, non-zero if error
##################################################
host_pwr_stop_watchdog() {
  # Check to see if we can stop the watchdog safely
  # We don't want to stop if we are called from the watchdog itself
  if [ -n "${DONT_STOP_WATCHDOG-}" ]; then
    return 0
  fi

  echo 'Stopping the host watchdog' >&2
  systemctl stop phosphor-watchdog@host0
}

##################################################
# Start the host watchdog
# Return:
#   0 if success, non-zero if error
##################################################
host_pwr_start_watchdog() {
  echo 'Starting the host watchdog' >&2
  systemctl start phosphor-watchdog@host0
}

host_pwr_init=1
return 0 2>/dev/null
echo "gpio-host-pwr is a library, not executed directly" >&2
exit 1

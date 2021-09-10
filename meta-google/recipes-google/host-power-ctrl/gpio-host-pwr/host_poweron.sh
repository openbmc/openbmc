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

source /usr/share/gpio-host-pwr/lib.sh || exit

gpio_build_cache 10 "$HOST_GPIO_PGOOD" "$HOST_GPIO_PWR_BTN" || exit
gpio_init "$HOST_GPIO_PGOOD" || exit

# Set the power status LED
if [ -n "$HOST_LED_PWR" ]; then
  echo 'none' > /sys/class/leds/"$HOST_LED_PWR"/trigger || true
  echo '255' > /sys/class/leds/"$HOST_LED_PWR"/brightness || true
fi

# Ensure the watchdog is available before the host starts
host_pwr_start_watchdog || true

# We don't want to do anything if the machine is already on
pgood="$(gpio_get_value "$HOST_GPIO_PGOOD")" || exit
if (( pgood == 1 )); then
  echo 'Host is already running, doing nothing' >&2
  exit 0
fi

# Do a quick push of the button if PGOOD
echo "Starting host power" >&2
rc=0
gpio_set_value "$HOST_GPIO_PWR_BTN" 1 || rc=$?
sleep 0.1
gpio_set_value "$HOST_GPIO_PWR_BTN" 0 || rc=$?

# Loop until we realize that host power is on
# Limit the loop count to 10 seconds as we should never
# have to wait this long for poweroff
s=$SECONDS
while true; do
  if ! pgood="$(gpio_get_value "$HOST_GPIO_PGOOD")"; then
    rc=1
    break
  fi
  if (( pgood == 1 )); then
    echo 'Host is now on' >&2
    break
  fi
  if (( SECONDS - s > 10 )); then
    echo 'Poweron timed out, terminating' >&2
    rc=2
    break
  fi
  sleep 0.1
done

exit $rc

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
  echo '0' > /sys/class/leds/"$HOST_LED_PWR"/brightness || true
fi

# Ensure the watchdog is no longer going to run
host_pwr_stop_watchdog || true

# We don't want to do anything if the machine is already off
pgood="$(gpio_get_value "$HOST_GPIO_PGOOD")" || exit
if (( pgood == 0 )); then
  echo 'Host is already off, doing nothing' >&2
  exit 0
fi

# Do a long push of the button if PGOOD
echo 'Stopping host power' >&2
rc=0
gpio_set_value "$HOST_GPIO_PWR_BTN" 1 || rc=$?

# Loop until we realize that host power is off
# Limit the loop count to 10 seconds as we should never
# have to wait this long for poweroff
s=$SECONDS
while true; do
  if ! pgood="$(gpio_get_value "$HOST_GPIO_PGOOD")"; then
    rc=1
    break
  fi
  if (( pgood == 0 )); then
    echo 'Host is now off' >&2
    break
  fi
  if (( SECONDS - s > 10 )); then
    echo 'Poweroff timed out, terminating' >&2
    rc=2
    break
  fi
  sleep 0.1
done

gpio_set_value "$HOST_GPIO_PWR_BTN" 0 || rc=$?

exit $rc

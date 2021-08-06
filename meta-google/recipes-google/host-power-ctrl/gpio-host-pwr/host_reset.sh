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

if [[ "${1-}" == "warm" ]]; then
  rst_txt='WARM' >&2
  rst_gpio="$HOST_GPIO_WARM_RESET"
else
  rst_txt='COLD' >&2
  rst_gpio="$HOST_GPIO_COLD_RESET"
fi

gpio_build_cache 10 "$rst_gpio" || exit

# Do a quick push of the button if PGOOD
echo "Issuing $rst_txt reset" >&2
rc=0
gpio_set_value "$rst_gpio" 1 || rc=$?
sleep 0.1
gpio_set_value "$rst_gpio" 0 || rc=$?

# Make sure the watchdog is stopped while the host is in reset
# and can't possibly restart it.
host_pwr_stop_watchdog || true

exit $rc

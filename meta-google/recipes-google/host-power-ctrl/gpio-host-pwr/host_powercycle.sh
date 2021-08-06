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

rc=0
host_poweroff.sh || rc=$?
# We want to ensure we aren't too quickly bouncing the power button
sleep 0.5
host_poweron.sh || rc=$?
exit $rc

#!/bin/bash
#
# Copyright (c) 2022 Nuvoton Technology Corporation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script to active/inactive LEDs
# Verson : v3

LED_SERVICE_NAME="xyz.openbmc_project.LED.GroupManager"
LED_INACTIVE_OBJPATH="/xyz/openbmc_project/led/groups/boot_status_inactive"
LED_STANDBY_OBJPATH="/xyz/openbmc_project/led/groups/boot_status_standby"
LED_INTERFACE_NAME="xyz.openbmc_project.Led.Group"
LED_Property="Asserted"

PWR_STATE_SERVICE="xyz.openbmc_project.State.Chassis"
PWR_STATE_OBJPATH="/xyz/openbmc_project/state/chassis0"
PWR_STATE_INTERFACE_NAME="xyz.openbmc_project.State.Chassis"
PWR_STATE_Property="CurrentPowerState"

power_state=""

mapper wait $LED_INACTIVE_OBJPATH
mapper wait $LED_STANDBY_OBJPATH

power_state="$(busctl get-property $PWR_STATE_SERVICE $PWR_STATE_OBJPATH $PWR_STATE_INTERFACE_NAME $PWR_STATE_Property | awk '{print $2}')"

if [[ $power_state == "\"xyz.openbmc_project.State.Chassis.PowerState.On\"" ]];then
    echo $power_state
    busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
    sleep 0.5s
    busctl set-property $LED_SERVICE_NAME $LED_STANDBY_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
    sleep 0.5s
else
    echo $power_state
    busctl set-property $LED_SERVICE_NAME $LED_STANDBY_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
    sleep 0.5s
    busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
    sleep 0.5s
fi

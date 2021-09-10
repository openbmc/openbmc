#!/bin/sh
#
# Copyright (c) 2021 Ampere Computing LLC
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

# Toggle the state of identify LED Group

SERVICE="xyz.openbmc_project.LED.GroupManager"
INTERFACE="xyz.openbmc_project.Led.Group"
PROPERTY="Asserted"

# Get enclosure_identify state
identify_object=$(busctl tree $SERVICE --list | grep -m 1 identify)
identify_state=$(busctl get-property $SERVICE $identify_object $INTERFACE $PROPERTY \
	  |  awk '{print $NF;}')

# Get enclosure_identify_blink state
identify_blink_object=$(busctl tree $SERVICE --list | grep identify_blink)
identify_blink_state=$(busctl get-property $SERVICE $identify_blink_object $INTERFACE $PROPERTY \
	  |  awk '{print $NF;}')

# Set state
if [[ "$identify_state" == "false" && "$identify_blink_state" == "false" ]]; then
    # Turn on the UID LED
    busctl set-property $SERVICE $identify_object $INTERFACE $PROPERTY b true
elif [[ "$identify_state" == "false" && "$identify_blink_state" == "true" ]]; then
    # Turn off the UID LED when LED is blinking state
    busctl set-property $SERVICE $identify_blink_object $INTERFACE $PROPERTY b false
elif [[ "$identify_state" == "true" && "$identify_blink_state" == "false" ]]; then
    # Turn off the UID LED
    busctl set-property $SERVICE $identify_object $INTERFACE $PROPERTY b false
else
    echo "Invalid case! When identify_blink_state is true, the identify_state will set to false"
fi

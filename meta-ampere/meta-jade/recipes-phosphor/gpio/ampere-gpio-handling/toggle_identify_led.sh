#!/bin/sh
#
# Toggle the state of identify LED Group

SERVICE="xyz.openbmc_project.LED.GroupManager"
INTERFACE="xyz.openbmc_project.Led.Group"
PROPERTY="Asserted"

# Get enclosure_identify state
identify_object=$(busctl tree $SERVICE --list | grep -m 1 identify)
identify_state=$(busctl get-property $SERVICE "$identify_object" $INTERFACE $PROPERTY \
			|  awk '{print $NF;}')

# Get enclosure_identify_blink state
identify_blink_object=$(busctl tree $SERVICE --list | grep identify_blink)
identify_blink_state=$(busctl get-property $SERVICE "$identify_blink_object" $INTERFACE $PROPERTY \
			|  awk '{print $NF;}')

# Set state
if [ "$identify_state" = "false" ] && [ "$identify_blink_state" = "false" ]; then
    # Turn on the UID LED
    busctl set-property $SERVICE "$identify_object" $INTERFACE $PROPERTY b true
elif [ "$identify_state" = "false" ] && [ "$identify_blink_state" = "true" ]; then
    # Turn off the UID LED when LED is blinking state
    busctl set-property $SERVICE "$identify_blink_object" $INTERFACE $PROPERTY b false
elif [ "$identify_state" = "true" ] && [ "$identify_blink_state" = "false" ]; then
    # Turn off the UID LED
    busctl set-property $SERVICE "$identify_object" $INTERFACE $PROPERTY b false
else
    echo "Invalid case! When identify_blink_state is true, the identify_state will set to false"
fi

#!/bin/bash
#
# Toggle the state of identify LED Group

socket=$1
isOn=$2

# Create /tmp/fault* file
if [ "$isOn" == "on" ]; then
	touch /tmp/fault"$socket"
else
	rm /tmp/fault"$socket"
fi

# Set Fault Led state
if [[ -f /tmp/fault0 ]] || [[ -f /tmp/fault1 ]]; then
	target='true'
else
	target='false'
fi

SERVICE="xyz.openbmc_project.LED.GroupManager"
INTERFACE="xyz.openbmc_project.Led.Group"
PROPERTY="Asserted"

# Get current state
object=$(busctl tree $SERVICE --list | grep system_fault)

# Set target state
busctl set-property $SERVICE "$object" $INTERFACE $PROPERTY b $target

#!/bin/sh

# This script is a backup solution for setting up/removing MCTP endpoint for NICs
action=$1
slot=$2
# NIC bus are 24~27.
bus=$((23 + slot))
# Static EID for NIC are 90~93.
eid=$((89 + slot))

sleep 2

if [ "$action" = "add" ]; then
    echo "Adding MCTP endpoint for slot $slot"
    busctl call xyz.openbmc_project.MCTP /xyz/openbmc_project/mctp au.com.CodeConstruct.MCTP AssignEndpointStatic sayy "mctpi2c${bus}" 1 0x32 "$eid"
elif [ "$action" = "remove" ]; then
    echo "Removing MCTP endpoint for slot $slot"
    busctl call xyz.openbmc_project.MCTP /xyz/openbmc_project/mctp/1/${eid} au.com.CodeConstruct.MCTP.Endpoint Remove
fi


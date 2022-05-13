#!/bin/sh

# Get BMC Latest Reboot Cause
bootstatus=$(cat /sys/class/watchdog/watchdog0/bootstatus)
bootstatus=$(printf "0x%x" $bootstatus)

# Interface: xyz.openbmc_project.State.BMC
OBJECT_ROOT=/xyz/openbmc_project
STATE_OBJECT=$OBJECT_ROOT/state
INTERFACE_ROOT=xyz.openbmc_project
STATE_INTERFACE=$INTERFACE_ROOT.State

# Object path: /xyz/openbmc_project/state/bmc0
OBJECT=$STATE_OBJECT/bmc0
SERVICE=$(mapper get-service $OBJECT)
INTERFACE=$STATE_INTERFACE.BMC
PROPERTY=LastRebootCause

# External relay 1
WDIOF_EXTERN1=0x4

# Card previously reset the CPU
WDIOF_CARDRESET=0x20

set_property ()
{
    busctl set-property "$@"
}

if [ $bootstatus == $WDIOF_EXTERN1 ]; then
   VALUE=$INTERFACE.RebootCause.Watchdog
elif [ $bootstatus == $WDIOF_CARDRESET ]; then
   VALUE=$INTERFACE.RebootCause.POR
else
   VALUE=$INTERFACE.RebootCause.Unknown
fi

set_property $SERVICE $OBJECT $INTERFACE $PROPERTY "s" $VALUE

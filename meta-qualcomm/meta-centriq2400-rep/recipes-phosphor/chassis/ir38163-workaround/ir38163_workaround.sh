#!/bin/sh -e
# For one of the IR38163 device on i2c bus 5, address 0x42
# Because the device only work when DC-ON, so when BMC during the DC-OFF state
# the device will probe fail, need the workaround for this HW design..

if [ "$1" == "add" ]; then
    echo Add the 0x42 device on i2c bus 5....
    sleep 2
    echo 5-0042 > /sys/bus/i2c/drivers/ir38163/bind
    sleep 1
elif [ "$1" == "rm" ]; then
    echo Remove the 0x42 device
    sleep 2
    echo 5-0042 > /sys/bus/i2c/drivers/ir38163/unbind
    sleep 1
else
    echo "$0 <add|rm>" to set state
fi


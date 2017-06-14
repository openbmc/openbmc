#!/bin/bash
# #########################################################
# Script to run on witherspoon BMC to unbind/bind the ir35221
# driver's devices

if [ "$1" = "unbind" ]
then
    if [ -e /sys/bus/i2c/drivers/ir35221/4-0070 ]
    then
        echo 4-0070 > /sys/bus/i2c/drivers/ir35221/$1
    fi

    if [ -e /sys/bus/i2c/drivers/ir35221/4-0071 ]
    then
        echo 4-0071 > /sys/bus/i2c/drivers/ir35221/$1
    fi

    if [ -e /sys/bus/i2c/drivers/ir35221/5-0070 ]
    then
        echo 5-0070 > /sys/bus/i2c/drivers/ir35221/$1
    fi

    if [ -e /sys/bus/i2c/drivers/ir35221/5-0071 ]
    then
        echo 5-0071 > /sys/bus/i2c/drivers/ir35221/$1
    fi
elif [ "$1" = "bind" ]
then
    if [ -e /sys/devices/platform/ahb/ahb:apb/ahb:apb:i2c@1e78a000/1e78a140.i2c-bus/i2c-4/4-0070 ]
    then
        echo 4-0070 > /sys/bus/i2c/drivers/ir35221/$1
    fi

    if [ -e /sys/devices/platform/ahb/ahb:apb/ahb:apb:i2c@1e78a000/1e78a140.i2c-bus/i2c-4/4-0071 ]
    then
        echo 4-0071 > /sys/bus/i2c/drivers/ir35221/$1
    fi

    if [ -e /sys/devices/platform/ahb/ahb:apb/ahb:apb:i2c@1e78a000/1e78a180.i2c-bus/i2c-5/5-0070 ]
    then
        echo 5-0070 > /sys/bus/i2c/drivers/ir35221/$1
    fi

    if [ -e /sys/devices/platform/ahb/ahb:apb/ahb:apb:i2c@1e78a000/1e78a180.i2c-bus/i2c-5/5-0071 ]
    then
        echo 5-0071 > /sys/bus/i2c/drivers/ir35221/$1
    fi
fi

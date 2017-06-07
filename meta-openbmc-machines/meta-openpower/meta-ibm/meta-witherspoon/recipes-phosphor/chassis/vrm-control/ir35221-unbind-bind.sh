#!/bin/bash
# #########################################################
# Script to run on witherspoon BMC to unbind/bind the ir35221
# driver's devices

echo 4-0070 > /sys/bus/i2c/drivers/ir35221/$1
echo 4-0071 > /sys/bus/i2c/drivers/ir35221/$1
echo 5-0070 > /sys/bus/i2c/drivers/ir35221/$1
echo 5-0071 > /sys/bus/i2c/drivers/ir35221/$1

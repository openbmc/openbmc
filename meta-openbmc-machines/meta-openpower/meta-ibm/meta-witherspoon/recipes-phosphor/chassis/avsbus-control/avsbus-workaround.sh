#!/bin/sh

echo 4-0070 > /sys/bus/i2c/drivers/ir35221/unbind
echo 4-0071 > /sys/bus/i2c/drivers/ir35221/unbind
echo 5-0070 > /sys/bus/i2c/drivers/ir35221/unbind
echo 5-0071 > /sys/bus/i2c/drivers/ir35221/unbind

i2cset -y 4 0x12 0x2E 0x23 b # VDD/VCS 0
i2cset -y 4 0x13 0x2E 0x23 b # VDN 0
i2cset -y 5 0x12 0x2E 0x23 b # VDD/VCS 1
i2cset -y 5 0x13 0x2E 0x23 b # VDN 1

echo 4-0070 > /sys/bus/i2c/drivers/ir35221/bind
echo 4-0071 > /sys/bus/i2c/drivers/ir35221/bind
echo 5-0070 > /sys/bus/i2c/drivers/ir35221/bind
echo 5-0071 > /sys/bus/i2c/drivers/ir35221/bind

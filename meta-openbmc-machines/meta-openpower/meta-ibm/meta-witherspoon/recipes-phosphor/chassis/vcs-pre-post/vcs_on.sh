#!/bin/sh

echo 4-0070 > /sys/bus/i2c/drivers/ir35221/unbind
echo 4-0071 > /sys/bus/i2c/drivers/ir35221/unbind
echo 5-0070 > /sys/bus/i2c/drivers/ir35221/unbind
echo 5-0071 > /sys/bus/i2c/drivers/ir35221/unbind

# enable VCS rail with OPERATION
# A side
i2cset -y 4 0x70 0x00 0x01 b
i2cset -y 4 0x70 0x02 0x1A b #respond to OPERATION
i2cset -y 4 0x70 0x00 0x00 b
# B side
i2cset -y 5 0x70 0x00 0x01 b
i2cset -y 5 0x70 0x02 0x1A b #respond to OPERATION
i2cset -y 5 0x70 0x00 0x00 b

echo 4-0070 > /sys/bus/i2c/drivers/ir35221/bind
echo 4-0071 > /sys/bus/i2c/drivers/ir35221/bind
echo 5-0070 > /sys/bus/i2c/drivers/ir35221/bind
echo 5-0071 > /sys/bus/i2c/drivers/ir35221/bind

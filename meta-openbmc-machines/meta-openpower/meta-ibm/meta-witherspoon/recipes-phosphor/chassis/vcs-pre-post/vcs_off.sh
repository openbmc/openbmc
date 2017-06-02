#!/bin/sh

echo 4-0070 > /sys/bus/i2c/drivers/ir35221/unbind
echo 4-0071 > /sys/bus/i2c/drivers/ir35221/unbind
echo 5-0070 > /sys/bus/i2c/drivers/ir35221/unbind
echo 5-0071 > /sys/bus/i2c/drivers/ir35221/unbind

# use these commands to properly disable VCS before powering on
# A side
i2cset -y 4 0x70 0x00 0x01 b
i2cset -y 4 0x70 0x02 0x16 b #respond to OPERATION
i2cset -y 4 0x70 0x00 0x00 b
# B side
i2cset -y 5 0x70 0x00 0x01 b
i2cset -y 5 0x70 0x02 0x16 b #respond to OPERATION
i2cset -y 5 0x70 0x00 0x00 b

echo 4-0070 > /sys/bus/i2c/drivers/ir35221/bind
echo 4-0071 > /sys/bus/i2c/drivers/ir35221/bind
echo 5-0070 > /sys/bus/i2c/drivers/ir35221/bind
echo 5-0071 > /sys/bus/i2c/drivers/ir35221/bind

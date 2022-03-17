#!/bin/bash
#
# Set all fans to pwm mode.

# Set all pwm_enable to 1
find /sys/class/hwmon/hwmon*/ -name 'pwm*_enable' -exec bash -c 'echo "$1" && echo 1 > "$1" && cat "$1"' -- {} \;

for i in {0..5}
do
    fan_pwm_rate_of_change=0x$(printf '%02x' $((8 + i)) | \
            awk '{print $1}')
    # Set Fan PWM Rate-of-Change Bits(bits 4:2) to 000b
    # Register 08h to 0Dh
    oriRegVal=$(i2cget -y -f 18 0x2c \
        "${fan_pwm_rate_of_change}")
    updateVal=$((oriRegVal & 0xe3))
    updateVal=0x$(printf "%x" ${updateVal})
    i2cset -y -f 18 0x2c \
        "${fan_pwm_rate_of_change}" "${updateVal}"
done

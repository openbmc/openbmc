#!/bin/bash
#

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-mori/recipes-mori/mori-fw-utility/mori-fw/mori-lib.sh
source /usr/libexec/mori-fw/mori-lib.sh


# Set all pwm to 50%
find /sys/class/hwmon/hwmon*/ -name 'pwm[1-6]' -exec bash -c 'echo "$1" && echo 127 > "$1" && cat "$1"' -- {} \;


# Set all fans to pwm mode.
find /sys/class/hwmon/hwmon*/ -name 'pwm*_enable' -exec bash -c 'echo "$1" && echo 1 > "$1" && cat "$1"' -- {} \;


for i in {0..5}
do
    fan_pwm_rate_of_change=0x$(printf '%02x' $((8 + i)) | \
            awk '{print $1}')
    # Set Fan PWM Rate-of-Change Bits(bits 4:2) to 000b
    # Register 08h to 0Dh
    oriRegVal=$(i2cget -y -f "${I2C_FANCTRL[0]}" 0x"${I2C_FANCTRL[1]}" \
        "$fan_pwm_rate_of_change")
    updateVal=$((oriRegVal & 0xe3))
    updateVal=0x$(printf "%x" $updateVal)
    i2cset -y -f "${I2C_FANCTRL[0]}" 0x"${I2C_FANCTRL[1]}" \
        "$fan_pwm_rate_of_change" "$updateVal"
done


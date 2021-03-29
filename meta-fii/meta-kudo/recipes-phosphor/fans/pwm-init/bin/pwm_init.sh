#!/bin/bash
#
# Set all fans to pwm mode.

# Set all pwm_enable to 1
find /sys/class/hwmon/hwmon*/ -name 'pwm*_enable' -exec bash -c 'echo "${1}" && echo 1 > "${1}" && cat "${1}"' -- {} \;
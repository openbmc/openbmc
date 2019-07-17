#!bin/bash

echo 230 > /sys/class/hwmon/*/pwm1
echo 230 > /sys/class/hwmon/*/pwm2
echo 230 > /sys/class/hwmon/*/pwm3

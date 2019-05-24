#!bin/bash

echo 255 > /sys/class/hwmon/*/pwm1
echo 255 > /sys/class/hwmon/*/pwm2
echo 255 > /sys/class/hwmon/*/pwm3

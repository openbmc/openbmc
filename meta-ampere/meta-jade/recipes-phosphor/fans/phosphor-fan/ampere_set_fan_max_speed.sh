#!/bin/bash
# shellcheck disable=SC2086

for filename in /sys/class/hwmon/*/pwm*
do
  echo 255 > $filename
done;

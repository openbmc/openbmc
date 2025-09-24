#!/bin/bash

GPIO_BASE=$(cat /sys/devices/platform/ahb/ahb:apb/1e780000.gpio/gpio/*/base)
GPIO_NUM=$((GPIO_BASE + 24 + 3))

echo 1 > /sys/class/gpio/gpio${GPIO_NUM}/value
sleep 5
echo 0 > /sys/class/gpio/gpio${GPIO_NUM}/value

exit 0;

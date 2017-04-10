#!/bin/bash

GPIO_BASE=$(cat /sys/class/gpio/gpiochip*/base)
GPIO_NUM=$(($GPIO_BASE + 24 + 3))

echo 1 > /sys/class/gpio/gpio${GPIO_NUM}/value
sleep 1
echo 0 > /sys/class/gpio/gpio${GPIO_NUM}/value

exit 0;

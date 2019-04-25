#!/bin/bash

GPIO_BASE=$(cat /sys/devices/platform/ahb/ahb:apb/1e780000.gpio/gpio/*/base)
#MGMT_ASSERT_PWR_BTN
PWR_OUT_NUM=$(($GPIO_BASE + 96 + 0))

#MGMT_ASSERT_BMC_READY GPIO M7
BMC_READY_NUM=$(($GPIO_BASE + 96 + 7 ))

echo 1 > /sys/class/gpio/gpio${BMC_READY_NUM}/value
echo 1 > /sys/class/gpio/gpio${PWR_OUT_NUM}/value
sleep 1
echo 0 > /sys/class/gpio/gpio${PWR_OUT_NUM}/value

exit 0;

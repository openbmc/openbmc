#!/bin/bash

NVME_GPIO_NUM=( 148 149 150 151 152 153 154 155 )
NVME=( 1 1 1 1 1 1 1 1 )
FAN_TABLE_PATH="/usr/share/swampd/config.json"
FAN_TABLE=( "/usr/share/swampd/config-8ssd.json" "/usr/share/swampd/config-2ssd.json" )
TYPE=-1

# get nvme presence
for i in {0..7}
do
  gpioNum=${NVME_GPIO_NUM[$i]}
  NVME[$i]=$(cat /sys/class/gpio/gpio$gpioNum/value)
done

# distinguish between 8-ssd and 2-ssd sku
for i in {2..7}
do
  if [ ${NVME[$i]} -eq 0 ]; then
    TYPE=0
  fi
done
if [ $TYPE -eq -1 ]; then
  for i in {0..1}
  do
    if [ ${NVME[$i]} -eq 0 ]; then
      TYPE=1
    fi
  done
fi

if [ $TYPE -eq 1 ]; then
  cp ${FAN_TABLE[1]} $FAN_TABLE_PATH
else
  cp ${FAN_TABLE[0]} $FAN_TABLE_PATH
fi

# start pid control
/usr/bin/swampd

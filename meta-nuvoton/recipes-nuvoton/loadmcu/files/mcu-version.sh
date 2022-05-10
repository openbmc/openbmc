#!/bin/sh

sleep 1

i2c_bus=$(echo $1)

# get mcu firmware version
version=$(i2ctransfer -f -y $i2c_bus w2@0x70 0x01 0x30 r2)

# parse mcu firmware major revision
major=`echo $version | awk '{print$2}'`

# parse mcu firmware minor revision
minor=`echo $version | awk '{print$1}'`

version="V`echo $((major))`.`echo $((minor))`"

if [ $version != "V0.0" ]; then
   echo "VERSION_ID=$version" > /var/lib/phosphor-bmc-code-mgmt/mcu-release
else
   echo "VERSION_ID=N/A" > /var/lib/phosphor-bmc-code-mgmt/mcu-release
fi

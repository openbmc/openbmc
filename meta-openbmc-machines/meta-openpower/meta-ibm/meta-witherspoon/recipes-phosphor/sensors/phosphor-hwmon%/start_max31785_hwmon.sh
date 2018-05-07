#!/bin/sh

# Copy in the correct MAX31785 phosphor-hwmon config file to use based on the
# WaterCooled property, and then start the hwmon service.

# $1: The OF_FULLNAME udev attribute for the MAX31785

base="/etc/default/obmc/hwmon/"$1
target=$base".conf"

service=$(mapper get-service /xyz/openbmc_project/inventory/system/chassis)

if [ $(busctl get-property $service \
     /xyz/openbmc_project/inventory/system/chassis \
     xyz.openbmc_project.Inventory.Decorator.CoolingType \
     WaterCooled | grep true | wc -l) != 0 ];
then
    source=$base'_water.conf'
else
    source=$base'_air.conf'
fi

cp $source $target

instance=$(systemd-escape $1)
systemctl start xyz.openbmc_project.Hwmon@$instance.service

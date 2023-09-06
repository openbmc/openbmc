#!/bin/sh
# File which is managing GPIOS when detected. First parameter is which GPIOs that switched
if [ "$1" = "pressed" ] 
then
	busctl set-property xyz.openbmc_project.Chassis.Gpios /xyz/openbmc_project/chassis/gpios xyz.openbmc_project.Chassis.Gpios PButton b true
else
	if [ "$1" = "released" ] 
	then
		busctl set-property xyz.openbmc_project.Chassis.Gpios /xyz/openbmc_project/chassis/gpios xyz.openbmc_project.Chassis.Gpios PButton b false
	fi
fi
echo "$1" >> /tmp/buttons.txt

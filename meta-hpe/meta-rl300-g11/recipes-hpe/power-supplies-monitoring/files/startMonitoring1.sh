#!/bin/sh
myaddress=$(gpiofind PSU1_INST)
state=$(gpioget "$myaddress")
if [ "r$state" = "r1" ]
then
	psus-manager 1 up
else
	psus-manager 1 down
fi
/usr/bin/phosphor-multi-gpio-monitor --config /usr/share/gpios/psu1.json

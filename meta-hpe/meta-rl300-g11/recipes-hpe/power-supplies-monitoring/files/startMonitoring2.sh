#!/bin/sh
myaddress=$(gpiofind PSU2_INST)
state=$(gpioget "$myaddress")
if [ "r$state" = "r1" ]
then
        psus-manager 2 up
else
        psus-manager 2 down
fi
/usr/bin/phosphor-multi-gpio-monitor --config /usr/share/gpios/psu2.json

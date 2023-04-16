#!/bin/bash

# shellcheck source=meta-ampere/meta-mitchell/recipes-ampere/platform/ampere-platform-init/gpio-lib.sh
source /usr/sbin/gpio-lib.sh

value=0
while true;
do
	if [[ $value -eq 0 ]]; then
		value=1
		gpio_name_set led-sw-hb 1
		gpio_name_set led-bmc-hb 0
	else
		value=0
		gpio_name_set led-sw-hb 0
		gpio_name_set led-bmc-hb 1
	fi
	sleep 1s
done

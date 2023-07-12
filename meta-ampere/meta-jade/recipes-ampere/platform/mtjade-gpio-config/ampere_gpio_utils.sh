#!/bin/bash

# shellcheck disable=SC2046

function usage() {
	echo "usage: ampere_gpio_utils.sh [power] [on|off]";
}

set_gpio_power_off() {
	echo "Setting GPIO before Power off"
}

set_gpio_power_on() {
	echo "Setting GPIO before Power on"
	val=$(gpioget $(gpiofind host0-ready))
	if [ "$val" == 1 ]; then
		exit
	fi
	gpioset $(gpiofind spi0-program-sel)=1
	gpioset $(gpiofind spi0-backup-sel)=0
}

if [ $# -lt 2 ]; then
	echo "Total number of parameter=$#"
	echo "Insufficient parameter"
	usage;
	exit 0;
fi

if [ "$1" == "power" ]; then
	if [ "$2" == "on" ]; then
		set_gpio_power_on
	elif [ "$2" == "off" ]; then
		set_gpio_power_off
	fi
	exit 0;
else
	echo "Invalid parameter1=$1"
	usage;
	exit 0;
fi

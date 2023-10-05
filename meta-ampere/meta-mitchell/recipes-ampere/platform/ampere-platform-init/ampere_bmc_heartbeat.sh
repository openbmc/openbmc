#!/bin/bash

# shellcheck disable=SC2046

value=0
while true;
do
	if [[ $value -eq 0 ]]; then
		value=1
		gpioset $(gpiofind led-sw-hb)=1
		gpioset $(gpiofind led-bmc-hb)=0
	else
		value=0
		gpioset $(gpiofind led-sw-hb)=0
		gpioset $(gpiofind led-bmc-hb)=1
	fi
	sleep 1s
done

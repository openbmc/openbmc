#!/bin/bash

# shellcheck source=/dev/null
source /usr/sbin/gpio-defs.sh

function gpio_number() {
	GPIO_BASE=$(cat /sys/class/gpio/gpiochip"$GPIO_CHIP0_BASE"/base)
	echo $((GPIO_BASE + $1))
}

# Configure GPIO as output and set its value
function gpio_configure_output() {
	gpioId=$(gpio_number "$1")
	echo "$gpioId" > /sys/class/gpio/export
	echo out > /sys/class/gpio/gpio"${gpioId}"/direction
	echo "$2" > /sys/class/gpio/gpio"${gpioId}"/value
	echo "$gpioId" > /sys/class/gpio/unexport
}

function gpio_get_val() {
	gpioId=$(gpio_number "$1")
	echo "$gpioId" > /sys/class/gpio/export
	cat /sys/class/gpio/gpio"$gpioId"/value
	echo "$gpioId" > /sys/class/gpio/unexport
}

# Configure GPIO as input
function gpio_configure_input() {
	gpioId=$(gpio_number "$1")
	echo "$gpioId" > /sys/class/gpio/export
	echo "in" > /sys/class/gpio/gpio"${gpioId}"/direction
	echo "$gpioId" > /sys/class/gpio/unexport
}

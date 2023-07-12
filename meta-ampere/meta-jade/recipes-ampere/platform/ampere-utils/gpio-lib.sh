#!/bin/bash

# Base of GPIO chip in /sys/class/gpio
GPIO_CHIP0_BASE=512
GPIO_CHIP1_BASE=780

AST2X00_GPIO_BASE=(
    "$GPIO_CHIP0_BASE"
    "$GPIO_CHIP1_BASE"
)

function gpio_configure_output() {
	echo "$1" > /sys/class/gpio/export
	echo out > /sys/class/gpio/gpio"$1"/direction
	echo "$2" > /sys/class/gpio/gpio"$1"/value
	echo "$1" > /sys/class/gpio/unexport
}

function gpio_get_val() {
	echo "$1" > /sys/class/gpio/export
	cat /sys/class/gpio/gpio"$1"/value
	echo "$1" > /sys/class/gpio/unexport
}

# Configure GPIO as input
function gpio_configure_input() {
	echo "$1" > /sys/class/gpio/export
	echo "in" > /sys/class/gpio/gpio"$1"/direction
	echo "$1" > /sys/class/gpio/unexport
}

function gpio_name_set()
{
    str=$(gpiofind "$1")
    #Verify error code when run gpiofind
    if [ "$?" == '1' ]; then
        echo "Invalid gpio name $1"
    else
        gpioid=$(echo "$str"|cut -c 9)
        offset=$(echo "$str"|cut -d " " -f 2)
        gpioPin=$(("$offset" + ${AST2X00_GPIO_BASE[$gpioid]}))
        gpio_configure_output "$gpioPin" "$2"
    fi
}

function gpio_name_get()
{
    str=$(gpiofind "$1")
    #Verify error code when run gpiofind
    if [ "$?" == '1' ]; then
        echo "Invalid gpio name $1"
    else
        offset=$(echo "$str"|cut -d " " -f 2)
        gpioid=$(echo "$str"|cut -c 9)
        gpioPin=$(("$offset" + ${AST2X00_GPIO_BASE[$gpioid]}))
        gpio_get_val "$gpioPin"
    fi
}

function gpio_name_input()
{
    str=$(gpiofind "$1")
    #Verify error code when run gpiofind
    if [ "$?" == '1' ]; then
        echo "Invalid gpio name $1"
    else
        gpioid=$(echo "$str"|cut -c 9)
        offset=$(echo "$str"|cut -d " " -f 2)
        gpioPin=$(("$offset" + ${AST2X00_GPIO_BASE[$gpioid]}))
        gpio_configure_input "$gpioPin"
    fi
}

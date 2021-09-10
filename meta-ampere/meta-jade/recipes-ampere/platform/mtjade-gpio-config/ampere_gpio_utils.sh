#!/bin/bash
source /usr/sbin/gpio-defs.sh
source /usr/sbin/gpio-lib.sh

function usage() {
    echo "usage: ampere_gpio_utils.sh [power] [on|off]";
}

set_gpio_power_off() {
    echo "Setting GPIO before Power off"
    gpio_configure_output $OCP_AUX_PWREN 1
    gpio_configure_output $OCP_MAIN_PWREN 0
    gpio_configure_output $SPI0_PROGRAM_SEL 0
}

set_gpio_power_on() {
    echo "Setting GPIO before Power on"
    gpio_configure_output $OCP_AUX_PWREN 1
    gpio_configure_output $OCP_MAIN_PWREN 1
    gpio_configure_output $SPI0_PROGRAM_SEL 1
    gpio_configure_output $SPI0_BACKUP_SEL 0
}

if [ $# -lt 2 ]; then
    echo "Total number of parameter=$#"
    echo "Insufficient parameter"
    usage;
    exit 0;
fi

if [ $1 == "power" ]; then
    if [ $2 == "on" ]; then
        set_gpio_power_on
    elif [ $2 == "off" ]; then
        set_gpio_power_off
    fi
    exit 0;
else
    echo "Invalid parameter1=$1"
    usage;
    exit 0;
fi
exit 0;

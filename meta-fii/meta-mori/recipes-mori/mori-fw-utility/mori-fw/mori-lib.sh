#!/bin/bash

# Disable check for usage of the definitions within mori-lib.sh
#shellcheck disable=SC2034

# get_gpio_num
# Dynamically obtains GPIO number from chip base and I2C expanders
# line-name
function get_gpio_num() {
    #shellcheck disable=SC2207
    CHIP_PIN=($(gpiofind "$1" | awk '{print substr ($1, 9 ), $2 }'))
    #shellcheck disable=SC2128
    if [ -z "$CHIP_PIN" ]; then
        echo "Could not find GPIO with name: $1"
        return 1
    fi

    if [ "${CHIP_PIN[0]}" -gt 7 ]; then
        BUS_ADDR=$(gpiodetect | grep gpiochip"${CHIP_PIN[0]}" | awk '{print substr($2, 2, length($2) - 2)}')
        GPIO_BASE=$(cat /sys/bus/i2c/devices/"$BUS_ADDR"/gpio/*/base)
        echo "$((GPIO_BASE+CHIP_PIN[1]))"
    else
        echo "$((CHIP_PIN[0]*32+CHIP_PIN[1]))"
    fi
}

# set_gpio_ctrl
# line-name, high(1)/low(0)
function set_gpio_ctrl() {
    #shellcheck disable=SC2046
    gpioset $(gpiofind "$1")="$2"
}

# get_gpio_ctrl
# line-name
function get_gpio_ctrl() {
    GPIO_NUM=$(get_gpio_num "$1")
    echo "$GPIO_NUM" > /sys/class/gpio/export
    cat /sys/class/gpio/gpio"$GPIO_NUM"/value
    echo "$GPIO_NUM" > /sys/class/gpio/unexport
}

# Start definitions

# I2C Definitions 
# The array is (<bus> <address>), where address is in hexadecimal.
I2C_BMC_CPLD=(13 76)
I2C_MB_CPLD=(0 76)
I2C_FANCTRL=(35 2c)
I2C_BMC_PWRSEQ=(48 59)
I2C_MB_PWRSEQ=(40 40)
I2C_CPU_EEPROM=(19 50)
I2C_STBUCK=(33 74)
I2C_HOTSWAP_CTRL=(25 1f)

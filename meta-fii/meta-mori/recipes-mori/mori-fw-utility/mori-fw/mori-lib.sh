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
        BUS_ADDR=$(gpiodetect | grep gpiochip"${CHIP_PIN[0]}" | \
                   grep -o '\[.*]' | tr -d ' \[\]')
        GPIO_BASE_DIR=$(cd /sys/bus/i2c/devices/"$BUS_ADDR"/gpio/ || \
                        exit; ls -1 -v)
        # Check that there is a single gpiobank per i2c device
        GPIO_BANKS=$(cd /sys/bus/i2c/devices/"$BUS_ADDR"/ || \
                     exit ;  ls -1 -d -v gpiochip*)
        # Determine which GPIO_BASE to use based on the place of the GPIO_BANK
        # in comparision to GPIO_BANKS
        # gpiochip# is set in reverse order of numbering for location of
        # GPIO_BASE_DIR
        count=$(echo "$GPIO_BANKS" | wc -w)
        for X in ${GPIO_BANKS}
        do
            if [[ $(gpiofind "$1" | cut -d " " -f 1) == "$X" ]]; then
                # Used to select the correct GPIO_BASE value
                #shellcheck disable=SC2086
                GPIO_BASE_DIR=$(echo ${GPIO_BASE_DIR} | cut -d " " -f $count)
                break
            fi
            count=$((count-1))
        done
        tmp="/sys/bus/i2c/devices/$BUS_ADDR/gpio/${GPIO_BASE_DIR[0]}/base"
        GPIO_BASE=$(cat "$tmp")
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
    # GPIOs added by drivers use different path for value most but not all
    # drivers follow this trend
    # Try reading like traditional GPIO, if fails, try reading in driver format
    if ! cat /sys/class/gpio/gpio"$GPIO_NUM"/value 2> /dev/null ; then
        cat /sys/class/gpio/"$1"/value
    fi
    echo "$GPIO_NUM" > /sys/class/gpio/unexport
}

# rst_bios_spi
# Resets BIOS SPI EEPROM
rst_bios_spi() {
  echo "Reset BIOS SPI EEPROM"
  set_gpio_ctrl RST_BIOS_EEPROM0_N 0
  sleep 1
  set_gpio_ctrl RST_BIOS_EEPROM0_N 1
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

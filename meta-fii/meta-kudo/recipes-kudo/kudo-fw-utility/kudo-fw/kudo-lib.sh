#!/bin/bash

# Disable check for usage of the definitions within kudo-lib.sh
# shellcheck disable=SC2034

# get_gpio_num
# Dynamically obtains GPIO number from chip base and I2C expanders through line name
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

function get_scp_eeprom() {
    scp_eeprom_sel=$(get_gpio_ctrl BACKUP_SCP_SEL)
    case $scp_eeprom_sel in
    0)
        echo " Using Secondary SCP EEPROM"
        ;;
    1)
        echo " Using Primary SCP EEPROM"
        ;;
    esac
}

# I2C Definitions
# The array is (<bus> <address>), where address is in hexadecimal.
I2C_BMC_CPLD=(13 76)
I2C_MB_CPLD=(34 76)
I2C_S0_SMPRO=(2 4f)
I2C_S1_SMPRO=(2 4e)
I2C_FANCTRL=(18 2c)
I2C_BMC_PWRSEQ=(14 59)
I2C_MB_PWRSEQ1=(32 40)
I2C_MB_PWRSEQ2=(32 41)
I2C_CPU_EEPROM=(40 50)
I2C_S1_CLKGEN=(37 68)
I2C_S1_PCIE_CLKGEN1=(16 6a)
I2C_S1_PCIE_CLKGEN2=(17 67)

# Board Version Definitions
BOARDVER_EVT_LAST=64
BOARDVER_DVT_LAST=127
BOARDVER_PVT_LAST=191

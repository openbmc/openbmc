#!/bin/bash

source /usr/sbin/kudo-lib.sh
# sleep so that FRU and all ipmitool Devices are ready before HOST OS
# gpio 143 for HPM_STBY_RST_N do to DC-SCM spec
set_gpio_ctrl 143 out 1
sleep 5 # for the MUX to get ready 
# set all mux route to CPU before power on host
# BMC_CPU_RTC_I2C_SEL #120
set_gpio_ctrl 120 out 1
# BMC_CPU_DDR_I2C_SEL #84
set_gpio_ctrl 84 out 1
# BMC_CPU_EEPROM_I2C_SEL #85
set_gpio_ctrl 85 out 1
# BMC_CPU_PMBUS_SEL #86
set_gpio_ctrl 86 out 1

# LED control
# LED_BMC_LIVE #7
set_gpio_ctrl 7 out 1

# SPI control
# Send command to CPLD to switch the bios spi interface to host
i2cset -y -f -a 13 0x76 0x10 0x00

# Power control
# MON_BMC_ALIVE, GPIO 10(EVT), GPIO137(DVT)
set_gpio_ctrl 10 out 1
set_gpio_ctrl 137 out 1
# S0_BMC_OK, GPIO 69
set_gpio_ctrl 69 out 1

# Disable CPU 1 CLK when cpu not detected
boardver=$(printf '%d' `cat /sys/bus/i2c/drivers/fiicpld/34-0076/CMD00 | awk '{print $6}'`)
# echo init_once cpu $CPU1_STATUS > /dev/ttyS0
# echo init_once board $boardver > /dev/ttyS0
CPU1_STATUS_N=$(get_gpio_ctrl 136)
if [[ $CPU1_STATUS_N == 1 ]]; then
    #Execute this only on DVT systems
    if [[ $boardver == 0 ]]; then
        echo EVT system $boardver
    else
        echo DVT system $boardver
        i2cset -y -a -f 37 0x68 0x05 0x03
    fi
    #These i2c deviecs are already installed on EVT systems
    i2cset -y -a -f 16 0x6a 0 1 0xdf i
    i2cset -y -a -f 16 0x6a 11 1 0x01 i
    i2cset -y -a -f 17 0x67 1 2 0x3f 0x0c i
fi

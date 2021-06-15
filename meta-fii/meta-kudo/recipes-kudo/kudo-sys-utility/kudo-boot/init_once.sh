#!/bin/bash

source /usr/sbin/kudo-lib.sh

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
# MON_BMC_ALIVE, GPIO 10
set_gpio_ctrl 10 out 1
# S0_BMC_OK, GPIO 69
set_gpio_ctrl 69 out 1

#!/bin/bash

# shellcheck source=/dev/null
source /usr/sbin/gpio-lib.sh

# GPIOAC3 BMC_SPI0_BACKUP_SEL Boot from MAIN SPI-HOST
gpio_configure_output 227 0

# GPIOM4 S0_I2C9_ALERT_L
gpio_configure_input 100

# GPIOM5 S1_I2C9_ALERT_L
gpio_configure_input 101

# GPIOQ7 GPIO_BMC_VGA_FRONT_PRES_L
gpio_configure_input 135

# GPIOS0 GPIO_S0_VRHOT_L
gpio_configure_input 144

# GPIOS1 GPIO_S1_VRHOT_L
gpio_configure_input 145

# GPIOY3 BMC_VGA_SEL
gpio_configure_output 195 1

# GPIO_BMC_READY
gpio_configure_output 229 1

# =======================================================
# Below GPIOs are controlled by other services so just
# initialize in A/C power only.
bootstatus=$(cat /sys/class/watchdog/watchdog0/bootstatus)
if [ "$bootstatus" == '32' ]; then
	# BMC_GPIOR2_EXT_HIGHTEMP_L
	gpio_configure_output 138 1

	# GPIOS5 GPIO_BMC_VR_PMBUS_SEL_L
	gpio_configure_output 149 1

	# GPIOH7 GPIO_BMC_I2C6_RESET_L
	gpio_configure_output 63 1

	# GPIO_BMC_OCP_AUX_PWREN
	gpio_configure_output 139 1

	# GPIO_BMC_OCP_MAIN_PWREN
	gpio_configure_output 140 0

	# BMC_GPIOAC2_SPI0_PROGRAM_SEL
	gpio_configure_output 226 0

	# BMC_GPIOB0_I2C_BACKUP_SEL
	gpio_configure_output 8 1
fi

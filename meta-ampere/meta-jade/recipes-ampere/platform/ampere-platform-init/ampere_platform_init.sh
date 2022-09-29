#!/bin/bash

# shellcheck source=/dev/null
source /usr/sbin/gpio-lib.sh
source /usr/sbin/gpio-defs.sh

# Configure to boot from MAIN SPI-HOST
gpio_configure_output "$SPI0_BACKUP_SEL" 0

gpio_configure_input "$S0_I2C9_ALERT_L"
gpio_configure_input "$S1_I2C9_ALERT_L"
gpio_configure_input "$GPIO_BMC_VGA_FRONT_PRES_L"
gpio_configure_input "$GPIO_S0_VRHOT_L"
gpio_configure_input "$GPIO_S1_VRHOT_L"
gpio_configure_output "$BMC_VGA_SEL" 1

# =======================================================
# Below GPIOs are controlled by other services so just
# initialize in A/C power only.
bootstatus=$(cat /sys/class/watchdog/watchdog0/bootstatus)
if [ "$bootstatus" == '32' ]; then
	gpio_configure_output "$BMC_GPIOR2_EXT_HIGHTEMP_L" 1
	gpio_configure_output "$GPIO_BMC_VR_PMBUS_SEL_L" 1
	gpio_configure_output "$GPIO_BMC_I2C6_RESET_L" 1

	# Initialize OCP register
	gpio_configure_output "$OCP_MAIN_PWREN" 0

	# Configure SPI-NOR/EEPROM switching
	gpio_configure_output "$SPI0_PROGRAM_SEL" 0
	gpio_configure_output "$BMC_I2C_BACKUP_SEL" 1
	gpio_configure_output "$SPI0_BACKUP_SEL" 0

	# Initialize BMC_SYS_PSON_L, SHD_REQ_L, BMC_SYSRESET_L
	gpio_configure_output "$SYS_PSON_L" 1
	gpio_configure_output "$S0_SHD_REQ_L" 1
	gpio_configure_output "$S0_SYSRESET_L" 1
	gpio_configure_output "$S1_SYSRESET_L" 1

	# RTC Lock, SPECIAL_BOOT
	gpio_configure_output "$RTC_LOCK" 0
	gpio_configure_output "$S0_SPECIAL_BOOT" 0
	gpio_configure_output "$S1_SPECIAL_BOOT" 0
fi

gpio_configure_output "$BMC_READY" 1

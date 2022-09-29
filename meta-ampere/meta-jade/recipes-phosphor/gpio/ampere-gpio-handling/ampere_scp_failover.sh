#!/bin/bash

# Handle the SCP Failover feature in which:
# - If the BMC receives the SCP_AUTH_FAILURE signal from Socket0,
#   attempts to boot from the failover boot EEPROM.
# - If the second boot fails, treats this as a permanent boot failure
#   and logs an event in SEL.

# shellcheck disable=SC1091
# shellcheck disable=SC2046

source /usr/sbin/gpio-lib.sh
source /usr/sbin/gpio-defs.sh

# Check the I2C_BACKUP_SEL
I2C_BACKUP_SEL=$(gpio_get_val "$BMC_I2C_BACKUP_SEL")
if [ "${I2C_BACKUP_SEL}" == "1" ]; then
	# If it is HIGH, set it LOW. Then reset the Host to boot from
	# the failover Boot EEPROM.
	echo "scp-failover: switch HOST to failover boot EEPROM"
	gpioset $(gpiofind BMC_SELECT_EEPROM)=0

	# Reset the Host to boot on the failover EEPROM
	ampere_power_util.sh mb force_reset
else
	# Turn OFF Host as SCP firmware on both Boot EEPROM fail
	obmcutil chassisoff

	echo "scp-failover: switch HOST back to the main Boot EEPROM"
	gpioset $(gpiofind BMC_SELECT_EEPROM)=1

	# Log event
	ampere_add_redfishevent.sh OpenBMC.0.1.GeneralFirmwareSecurityViolation.Critical "SCP Authentication failure"
fi

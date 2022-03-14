#!/bin/bash

do_fru_upgrade() {
	FRU_DEVICE="/sys/bus/i2c/devices/3-0050/eeprom"

	if ! command -v ampere_fru_upgrade;
	then
		echo "Bypass fru update as no ampere_fru_upgrade available"
		exit
	fi
	ampere_fru_upgrade -d $FRU_DEVICE -f "$IMAGE"

	systemctl restart xyz.openbmc_project.FruDevice.service
	systemctl restart phosphor-ipmi-host.service
}

do_smpmpro_upgrade() {
	I2C_BUS_DEV="1"
	EEPROM_ADDR="0x50"

	if ! command -v ampere_eeprom_prog;
	then
		echo "Bypass SCP firmware update as no ampere_eeprom_prog available"
		exit
	fi
	echo "SECPRO mode: $SECPRO"
	# Turn off the Host if it is currently ON
	chassisstate=$(obmcutil chassisstate | awk -F. '{print $NF}')
	echo "Current Chassis State: $chassisstate"
	if [ "$chassisstate" == 'On' ];
	then
		echo "Turning the Chassis off"
		obmcutil chassisoff
		sleep 15
		# Check if HOST was OFF
		chassisstate_off=$(obmcutil chassisstate | awk -F. '{print $NF}')
		if [ "$chassisstate_off" == 'On' ];
		then
			echo "Error : Failed turning the Chassis off"
			exit
		fi
	fi

	if [[ $SECPRO == 1 ]]; then
		# 3 is S0_SPECIAL_BOOT
		gpioset 0 3=1
		# 66 is S1_SPECIAL_BOOT
		gpioset 0 66=1
	fi

	# Switch EEPROM control to BMC AST2500 I2C
	# 226 is BMC_GPIOAC2_SPI0_PROGRAM_SEL
	gpioset 0 226=0

	# 08 is BMC_GPIOB0_I2C_BACKUP_SEL
	if [[ $DEV_SEL == 1 ]]; then
		echo "Run update primary Boot EEPROM"
		gpioset 0 8=1       # Main EEPROM
	elif [[ $DEV_SEL == 2 ]]; then
		echo "Run update secondary Boot EEPROM"
		gpioset 0 8=0       # Second EEPROM
	else
		echo "Please choose Main (1) or Second EEPROM (2)"
		exit 0
	fi

	# Write Firmware to EEPROM and read back for validation
	ampere_eeprom_prog -b $I2C_BUS_DEV -s $EEPROM_ADDR -p -f "$IMAGE"

	# Switch EEPROM control to Host
	# 08 is BMC_GPIOB0_I2C_BACKUP_SEL
	gpioset 0 8=1
	# 226 is BMC_GPIOAC2_SPI0_PROGRAM_SEL
	gpioset 0 226=1

	if [ "$chassisstate" == 'On' ];
	then
		sleep 5
		echo "Turn on the Host"
		obmcutil poweron
	fi

	# Deassert SECPRO GPIO PINs
	if [[ $SECPRO == 1 ]]; then
		chassisstate=$(obmcutil chassisstate | awk -F. '{print $NF}')
		if [ "$chassisstate_off" == 'Off' ]; then
			obmcutil poweron
		fi

		sleep 30s
		echo "De-asserting special GPIO PINs"
		# 3 is S0_SPECIAL_BOOT
		gpioset 0 3=0
		# 66 is S1_SPECIAL_BOOT
		gpioset 0 66=0
	fi
}


if [ $# -eq 0 ]; then
	echo "Usage:"
	echo "      $(basename "$0") <Type> <Image file> <DEV_SEL> [SECPRO]"
	echo "Where:"
	echo "    <Type>: smpmpro or fru"
	echo "            If Type is smpmpro, then DEV_SEL must is 1 (MAIN EEPROM), 2 (Failover)"
	echo "    SECPRO: Optional, input '1' to enter & flash secpro mode. Default: 0"
	exit 0
fi

TYPE=$1
IMAGE=$2
if [ -z "$3" ]
then
	DEV_SEL="1"    # by default, select Main image
else
	DEV_SEL=$3
fi

SECPRO=0
if [ -n "$4" ]; then
	if [[ "$4" == "1" ]]; then
		SECPRO=1
	fi
fi

MANIFEST="$(echo "$IMAGE" | cut -d'/' -f-4)/MANIFEST"
if [ -f "$MANIFEST" ]; then
	echo "MANIFEST: $MANIFEST"
	if grep -qF "SECPRO" "$MANIFEST"; then
		SECPRO=1
	fi
fi

# Restrict to flash failover in case of SECPRO
if [ $SECPRO == 1 ] && [ "$DEV_SEL" == 2 ]; then
	echo "Not allow to flash the failover with SECPRO image"
	exit
fi

if [[ $TYPE == "smpmpro" ]]; then
	do_smpmpro_upgrade
elif [[ $TYPE == "fru" ]]; then
	do_fru_upgrade
fi

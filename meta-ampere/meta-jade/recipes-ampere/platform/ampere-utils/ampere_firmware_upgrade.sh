#!/bin/bash
# shellcheck disable=SC2046

do_fru_upgrade() {
	FRU_DEVICE="/sys/bus/i2c/devices/3-0050/eeprom"

	if ! command -v ampere_fru_upgrade;
	then
		echo "Bypass fru update as no ampere_fru_upgrade available"
		exit
	fi
	ampere_fru_upgrade -d $FRU_DEVICE -f "$IMAGE"

	systemctl restart xyz.openbmc_project.FruDevice.service
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

		# Wait 60s until Chassis is off
		cnt=30
		while [ "$cnt" -gt 0 ];
		do
			cnt=$((cnt - 1))
			sleep 2
			# Check if HOST was OFF
			chassisstate_off=$(obmcutil chassisstate | awk -F. '{print $NF}')
			if [ "$chassisstate_off" != 'On' ];
			then
				break
			fi

			if [ "$cnt" == "0" ];
			then
				echo "--- Error : Failed turning the Chassis off"
				exit 1
			fi
		done
	fi

	if [[ $SECPRO == 1 ]]; then
		gpioset $(gpiofind host0-special-boot)=1
		gpioset $(gpiofind s1-special-boot)=1
	fi

	# Switch EEPROM control to BMC AST2500 I2C
	gpioset $(gpiofind spi0-program-sel)=0

	# 08 is BMC_GPIOB0_I2C_BACKUP_SEL
	if [[ $DEV_SEL == 1 ]]; then
		echo "Run update primary Boot EEPROM"
		gpioset $(gpiofind i2c-backup-sel)=1       # Main EEPROM
	elif [[ $DEV_SEL == 2 ]]; then
		echo "Run update secondary Boot EEPROM"
		gpioset $(gpiofind i2c-backup-sel)=0       # Second EEPROM
	else
		echo "Please choose Main (1) or Second EEPROM (2)"
		exit 0
	fi

	# Write Firmware to EEPROM and read back for validation
	ampere_eeprom_prog -b $I2C_BUS_DEV -s $EEPROM_ADDR -p -f "$IMAGE"

	# Switch EEPROM control to Host
	# 08 is BMC_GPIOB0_I2C_BACKUP_SEL
	gpioset $(gpiofind i2c-backup-sel)=1
	gpioset $(gpiofind spi0-program-sel)=1

	# Deassert SECPRO GPIO PINs
	if [[ $SECPRO == 1 ]]; then
		echo "De-asserting special GPIO PINs"
		gpioset $(gpiofind host0-special-boot)=0
		gpioset $(gpiofind s1-special-boot)=0
	fi

	if [ "$chassisstate" == 'On' ];
	then
		sleep 5
		echo "Turn on the Host"
		obmcutil poweron
	fi

}


if [ $# -eq 0 ]; then
	echo "Usage:"
	echo "      $(basename "$0") <Type> <Image file> <DEV_SEL> [SECPRO]"
	echo "Where:"
	echo "    <Type>: eeprom or fru"
	echo "            If Type is eeprom, then DEV_SEL must is 1 (MAIN EEPROM), 2 (Failover)"
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

case $TYPE in
	"smpmpro" | "eeprom")
		do_smpmpro_upgrade
		;;
	"fru")
		do_fru_upgrade
		;;
esac

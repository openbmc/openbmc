#!/bin/bash

# Helper script to flash FRU and Boot EEPROM devices.
#
# Syntax for FRU:
#    ampere_firmware_upgrade.sh fru <image> [<dev>]
#      dev: 1 for MB FRU (default), 2 for BMC FRU.
#
# Syntax for EEPROM:
#    ampere_firmware_upgrade.sh eeprom <image> [<dev>]
#      dev: 1 for main Boot EEPROM (default), 2 for secondary Boot EEPROM (if supported)
#
# Syntax for Mainboard CPLD:
#    ampere_firmware_upgrade.sh main_cpld <image>
#
# Syntax for BMC CPLD:
#    ampere_firmware_upgrade.sh bmc_cpld <image>
#
# Syntax for Backplane CPLD:
#    ampere_firmware_upgrade.sh bp_cpld <image> [<target>]
#      target: 1 for Front Backplane 1
#              2 for Front Backplane 2
#              3 for Front Backplane 3
#              4 for Rear Backplane 1
#              5 for Rear Backplane 2
#

# shellcheck disable=SC2046

do_eeprom_flash() {
	FIRMWARE_IMAGE=$IMAGE
	BACKUP_SEL=$2

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
			exit 1
		fi
	fi

	# Switch EEPROM control to BMC AST2600 I2C
	# BMC_GPIOW6_SPI0_PROGRAM_SEL
	gpioset $(gpiofind spi0-program-sel)=1

	# BMC_GPIOX0_I2C_BACKUP_SEL (GPIO 184)
	if [[ $BACKUP_SEL == 1 ]]; then
		echo "Run update Primary EEPROM"
		gpioset $(gpiofind i2c-backup-sel)=0
	elif [[ $BACKUP_SEL == 2 ]]; then
		echo "Run update Failover EEPROM"
		gpioset $(gpiofind i2c-backup-sel)=1
	else
		echo "Please choose Primary EEPROM (1) or Failover EEPROM (2)"
		exit 0
	fi

	# The EEPROM (AT24C64WI) with address 0x50 at BMC_I2C11 bus
	# Write Firmware to EEPROM and read back for validation
	ampere_eeprom_prog -b 10 -s 0x50 -p -f "$FIRMWARE_IMAGE"

	# Switch to primary EEPROM
	gpioset $(gpiofind i2c-backup-sel)=0

	# Switch EEPROM control to CPU HOST
	gpioset $(gpiofind spi0-program-sel)=0

	if [ "$chassisstate" == 'On' ];
	then
		sleep 5
		echo "Turn on the Host"
		obmcutil poweron
	fi
}

do_fru_flash() {
	FRU_IMAGE=$1
	FRU_DEV=$2

	if [[ $FRU_DEV == 1 ]]; then
		if [ -f /sys/bus/i2c/devices/4-0050/eeprom ]; then
			FRU_DEVICE="/sys/bus/i2c/devices/4-0050/eeprom"
		else
			FRU_DEVICE="/sys/bus/i2c/devices/3-0050/eeprom"
		fi
		echo "Flash MB FRU with image $IMAGE at $FRU_DEVICE"
	elif [[ $FRU_DEV == 2 ]]; then
		FRU_DEVICE="/sys/bus/i2c/devices/14-0050/eeprom"
		echo "Flash BMC FRU with image $IMAGE at $FRU_DEVICE"
	else
		echo "Please select MB FRU (1) or BMC FRU (2)"
		exit 0
	fi

	ampere_fru_upgrade -d "$FRU_DEVICE" -f "$FRU_IMAGE"

	systemctl restart xyz.openbmc_project.FruDevice.service
	systemctl restart phosphor-ipmi-host.service

	echo "Done"
}

do_mb_cpld_flash() {
	MB_CPLD_IMAGE=$1
	echo "Flashing MB CPLD"
	gpioset $(gpiofind hpm-fw-recovery)=1
	gpioset $(gpiofind jtag-program-sel)=1
	sleep 2
	ampere_cpldupdate_jtag -t 1 -p "$MB_CPLD_IMAGE"
	gpioset $(gpiofind hpm-fw-recovery)=0
	echo "Done"
}

do_bmc_cpld_flash() {
	BMC_CPLD_IMAGE=$1
	echo "Flashing BMC CPLD"
	gpioset $(gpiofind jtag-program-sel)=0
	sleep 2
	ampere_cpldupdate_jtag -t 1 -p "$BMC_CPLD_IMAGE"
	echo "Done"
}

do_bp_cpld_flash() {
	BP_CPLD_IMAGE=$1
	BP_TARGET=$2
	if [[ $BP_TARGET == 1 ]]; then
		echo "Flashing Front Backplane 1 CPLD"
		ampere_cpldupdate_i2c -b 101 -s 0x40 -t 2 -p "$BP_CPLD_IMAGE"
	elif [[ $BP_TARGET == 2 ]]; then
		echo "Flashing Front Backplane 2 CPLD"
		ampere_cpldupdate_i2c -b 102 -s 0x40 -t 2 -p "$BP_CPLD_IMAGE"
	elif [[ $BP_TARGET == 3 ]]; then
		echo "Flashing Front Backplane 3 CPLD"
		ampere_cpldupdate_i2c -b 100 -s 0x40 -t 2 -p "$BP_CPLD_IMAGE"
		elif [[ $BP_TARGET == 4 ]]; then
		echo "Flashing Rear Backplane 1 CPLD"
		ampere_cpldupdate_i2c -b 103 -s 0x40 -t 2 -p "$BP_CPLD_IMAGE"
		elif [[ $BP_TARGET == 5 ]]; then
		echo "Flashing Rear Backplane 2 CPLD"
		ampere_cpldupdate_i2c -b 104 -s 0x40 -t 2 -p "$BP_CPLD_IMAGE"
	fi

	echo "Done"
}

if [ $# -eq 0 ]; then
	echo "Usage:"
	echo "  - Flash Boot EEPROM"
	echo "     $(basename "$0") eeprom <Image file>"
	echo "  - Flash FRU"
	echo "     $(basename "$0") fru <Image file> [dev]"
	echo "    Where:"
	echo "      dev: 1 - MB FRU, 2 - BMC FRU"
	echo "  - Flash Mainboard CPLD"
	echo "     $(basename "$0") mb_cpld <Image file>"
	echo "  - Flash BMC CPLD (only for DC-SCM BMC board)"
	echo "     $(basename "$0") bmc_cpld <Image file>"
	echo "  - Flash Backplane CPLD"
	echo "     $(basename "$0") bp_cpld <Image file> <Target> "
	echo "    Where:"
	echo "      Target: 1 - FrontBP1, 2 - FrontBP2, 3 - FrontBP3"
	echo "              4 - RearBP1, 5 - RearBP2"
	exit 0
fi

TYPE=$1
IMAGE=$2
TARGET=$3
if [ -z "$3" ]; then
	BACKUP_SEL=1
else
	BACKUP_SEL=$3
fi

if [[ $TYPE == "eeprom" ]]; then
	# Run EEPROM update: write/read/validation with CRC32 checksum
	do_eeprom_flash "$IMAGE" "$BACKUP_SEL"
elif [[ $TYPE == "fru" ]]; then
	# Run FRU update
	do_fru_flash "$IMAGE" "$BACKUP_SEL"
elif [[ $TYPE == "mb_cpld" ]]; then
	# Run Mainboard CPLD update
	do_mb_cpld_flash "$IMAGE"
elif [[ $TYPE == "bmc_cpld" ]]; then
	# Run CPLD BMC update
	do_bmc_cpld_flash "$IMAGE"
elif [[ $TYPE == "bp_cpld" ]]; then
	# Run Backplane CPLD update
	do_bp_cpld_flash "$IMAGE" "$TARGET"
fi

exit 0

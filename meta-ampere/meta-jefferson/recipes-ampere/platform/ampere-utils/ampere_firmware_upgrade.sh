#!/bin/bash

# Helper script to flash FRU and Boot EEPROM devices.
#
# Syntax for FRU:
#    ampere_firmware_upgrade.sh fru <image> [<dev>]
#      dev: 1 for CIO FRU (default), 2 for BMC FRU, 3 for MB-1 FRU, 4 for MB-2 FRU
#
# Syntax for EEPROM:
#    ampere_firmware_upgrade.sh eeprom <image>
#
# Syntax for Mainboard CPLD:
#    ampere_firmware_upgrade.sh main_cpld <image>
#
# Syntax for BMC CPLD:
#    ampere_firmware_upgrade.sh bmc_cpld <image>
#

# shellcheck disable=SC2046

do_eeprom_flash() {
	FIRMWARE_IMAGE=$IMAGE

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

	# Enable the BMC_I2C11_EEPROM_SCL and BMC_I2C11_EEPROM_SDA
	# BMC_GPIOW6_SPI0_PROGRAM_SEL
	gpioset $(gpiofind spi0-program-sel)=1

	# The EEPROM (AT24C64WI) with address 0x50 at BMC_I2C11 bus
	# Write Firmware to EEPROM and read back for validation
	ampere_eeprom_prog -b 10 -s 0x50 -p -f "$FIRMWARE_IMAGE"

	# Disable the BMC_I2C11_EEPROM_SCL and BMC_I2C11_EEPROM_SDA
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
		fi
		echo "Flash CIO FRU with image $IMAGE at $FRU_DEVICE"
	elif [[ $FRU_DEV == 2 ]]; then
		FRU_DEVICE="/sys/bus/i2c/devices/14-0050/eeprom"
		echo "Flash BMC FRU with image $IMAGE at $FRU_DEVICE"
	elif [[ $FRU_DEV == 3 ]]; then
		FRU_DEVICE="/sys/bus/i2c/devices/20-0052/eeprom"
		echo "Flash MB-1 FRU with image $IMAGE at $FRU_DEVICE"
	elif [[ $FRU_DEV == 4 ]]; then
		FRU_DEVICE="/sys/bus/i2c/devices/21-0052/eeprom"
		echo "Flash MB-2 FRU with image $IMAGE at $FRU_DEVICE"
	else
		echo "Please select CIO FRU (1), BMC FRU (2),"
		echo "MB-1 FRU (3), MB-2 FRU (4),"
		exit 0
	fi

	ampere_fru_upgrade -d "$FRU_DEVICE" -f "$FRU_IMAGE"

	busctl call xyz.openbmc_project.FruDevice /xyz/openbmc_project/FruDevice xyz.openbmc_project.FruDeviceManager ReScan

	echo "Done"
}

do_mb_cpld_flash() {
	MB_CPLD_IMAGE=$1
	echo "Flashing MB CPLD"
	gpioset $(gpiofind hpm-fw-recovery)=1
	gpioset $(gpiofind jtag-program-sel)=1
	sleep 2
	ampere_cpldupdate_jtag -p "$MB_CPLD_IMAGE"
	gpioset $(gpiofind hpm-fw-recovery)=0
	echo "Done"
}

do_bmc_cpld_flash() {
	BMC_CPLD_IMAGE=$1
	echo "Flashing BMC CPLD"
	gpioset $(gpiofind jtag-program-sel)=0
	sleep 2
	ampere_cpldupdate_jtag -p "$BMC_CPLD_IMAGE"
	echo "Done"
}

if [ $# -eq 0 ]; then
	echo "Usage:"
	echo "  - Flash Boot EEPROM"
	echo "     $(basename "$0") eeprom <Image file>"
	echo "  - Flash FRU"
	echo "     $(basename "$0") fru <Image file> [dev]"
	echo "    Where:"
	echo "      dev: 1 - CIO FRU (default), 2 - BMC FRU, 3 - MB-1 FRU, 4 - MB-2 FRU"
	echo "  - Flash Mainboard CPLD"
	echo "     $(basename "$0") mb_cpld <Image file>"
	echo "  - Flash BMC CPLD (only for DC-SCM BMC board)"
	echo "     $(basename "$0") bmc_cpld <Image file>"
	exit 0
fi

TYPE=$1
IMAGE=$2
if [ -z "$3" ]; then
	DEV_SEL=1
else
	DEV_SEL=$3
fi

if [[ $TYPE == "eeprom" ]]; then
	# Run EEPROM update: write/read/validation with CRC32 checksum
	do_eeprom_flash "$IMAGE"
elif [[ $TYPE == "fru" ]]; then
	# Run FRU update
	do_fru_flash "$IMAGE" "$DEV_SEL"
elif [[ $TYPE == "mb_cpld" ]]; then
	# Run Mainboard CPLD update
	do_mb_cpld_flash "$IMAGE"
elif [[ $TYPE == "bmc_cpld" ]]; then
	# Run CPLD BMC update
	do_bmc_cpld_flash "$IMAGE"
fi

exit 0

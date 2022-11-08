#!/bin/bash

# This script is used to flash the UEFI/EDKII

# Syntax: ampere_flash_bios.sh $image_file $device_sellect
# Where:
#       $image_file : the image binary file
#       $device_sellect : 1 - Host Main SPI Nor
#                         2 - Host Second SPI Nor

# Author : Chanh Nguyen (chnguyen@amperecomputing.com)

# Note:
#      BMC_GPIOW6_SPI0_PROGRAM_SEL (GPIO 182): 1 => BMC owns SPI bus for upgrading
#                                              0 => HOST owns SPI bus for upgrading

#      BMC_GPIOW7_SPI0_BACKUP_SEL (GPIO 183) :  1 => to switch SPI_CS0_L to primary SPI Nor device
#                                               0 => to switch SPI_CS0_L to second SPI Nor device

# shellcheck disable=SC2046
# shellcheck disable=SC2086

do_flash () {
	# always unbind then bind the ASpeed SMC driver again to prevent
	# the changing of the device erasesize by nvparm
	HOST_MTD=$(< /proc/mtd grep "pnor" | sed -n 's/^\(.*\):.*/\1/p')
	if [ -n "$HOST_MTD" ];
	then
		echo 1e630000.spi > /sys/bus/platform/drivers/spi-aspeed-smc/unbind
		sleep 2
	fi
	echo 1e630000.spi > /sys/bus/platform/drivers/spi-aspeed-smc/bind

	# Check the PNOR partition available
	HOST_MTD=$(< /proc/mtd grep "pnor" | sed -n 's/^\(.*\):.*/\1/p')
	if [ -z "$HOST_MTD" ];
	then
		echo "Fail to probe the Host SPI-NOR device"
		exit 1
	fi

	echo "--- Flashing firmware image $IMAGE to @/dev/$HOST_MTD"
	flashcp -v "$IMAGE" /dev/"$HOST_MTD"
}


if [ $# -eq 0 ]; then
	echo "Usage: $(basename "$0") <UEFI/EDKII image file> <DEV_SEL> [SPECIAL_BOOT]"
	echo "Where:"
	echo "    DEV_SEL 1 is Primary SPI (by default), 2 is Second SPI"
	echo "    SPECIAL_BOOT: Optional, input '1' to flash "Secure Provisioning" image and enter Special Boot mode. Default: 0"
	exit 0
fi

IMAGE="$1"
if [ ! -f "$IMAGE" ]; then
	echo "The image file $IMAGE does not exist"
	exit 1
fi

if [ -z "$2" ]; then
       DEV_SEL="1"    # by default, select primary device
else
       DEV_SEL="$2"
fi

SPECIAL_BOOT=0
if [[ "$3" == "1" ]]; then
	SPECIAL_BOOT=1
fi

echo "SPECIAL_BOOT mode: $SPECIAL_BOOT"
# Turn off the Host if it is currently ON
chassisstate=$(obmcutil chassisstate | awk -F. '{print $NF}')
echo "--- Current Chassis State: $chassisstate"
if [ "$chassisstate" == 'On' ];
then
	echo "--- Turning the Chassis off"
	obmcutil chassisoff
	sleep 10
	# Check if HOST was OFF
	chassisstate_off=$(obmcutil chassisstate | awk -F. '{print $NF}')
	if [ "$chassisstate_off" == 'On' ];
	then
		echo "--- Error : Failed turning the Chassis off"
		exit 1
	fi
fi

# Switch the host SPI bus to BMC"
echo "--- Switch the host SPI bus to BMC."
if ! gpioset $(gpiofind spi0-program-sel)=1; then
	echo "ERROR: Switch the host SPI bus to BMC. Please check gpio state"
	exit 1
fi

# Switch the host SPI bus (between primary and secondary)
# 183 is BMC_GPIOW7_SPI0_BACKUP_SEL
if [[ $DEV_SEL == 1 ]]; then
	echo "Run update Primary Host SPI-NOR"
	gpioset $(gpiofind spi0-backup-sel)=1       # Primary SPI
elif [[ $DEV_SEL == 2 ]]; then
	echo "Run update Secondary Host SPI-NOR"
	gpioset $(gpiofind spi0-backup-sel)=0       # Second SPI
else
	echo "Please choose primary SPI (1) or second SPI (2)"
	exit 0
fi

# Restrict to flash Secondary Host SPI-NOR in case of SPECIAL_BOOT
if [ $SPECIAL_BOOT == 1 ] && [ "$DEV_SEL" == 2 ]; then
	echo "Flashing 2nd Host SPI NOR image with SECProv image is not allowed"
	exit
fi

# Flash the firmware
do_flash

# Assert SPECIAL_BOOT GPIO PIN
if [[ $SPECIAL_BOOT == 1 ]]; then
	gpioset $(gpiofind host0-special-boot)=1
	# Set HOST BOOTCOUNT to 0 to prevent Host reboot
	busctl set-property xyz.openbmc_project.State.Host0 \
		/xyz/openbmc_project/state/host0 \
		xyz.openbmc_project.Control.Boot.RebootAttempts RetryAttempts u 0
fi

# Switch the SPI bus to the primary SPI device
echo "Switch to the Primary Host SPI-NOR"
gpioset $(gpiofind spi0-backup-sel)=1       # Primary SPI

# Switch the host SPI bus to HOST."
echo "--- Switch the host SPI bus to HOST."
if ! gpioset $(gpiofind spi0-program-sel)=0; then
	echo "ERROR: Switch the host SPI bus to HOST. Please check GPIO state"
	exit 1
fi

if [ "$chassisstate" == 'On' ] || [ $SPECIAL_BOOT == 1 ];
then
	sleep 5
	echo "Turn on the Host"
	obmcutil poweron
fi

# Detection SECProv of failure or success
if [[ $SPECIAL_BOOT == 1 ]]; then
	# 30s time out in wait for FW_BOOT_OK
	state=0
	cnt=60
	while [ $cnt -gt 0 ];
	do
		# Monitor FW_BOOT_OK gpio
		state=$(gpioget $(gpiofind s0-fw-boot-ok))
		if [[ "$state" == "1" ]]; then
			break
		fi
		sleep 0.5
		cnt=$((cnt - 1))
	done

	echo "--- Turning the Chassis off"
	obmcutil chassisoff

	# Deassert SPECIAL_BOOT GPIO PIN
	gpioset $(gpiofind host0-special-boot)=0

	sleep 10
	# Recover HOST BOOTCOUNT to default
	busctl set-property xyz.openbmc_project.State.Host0 \
		/xyz/openbmc_project/state/host0 \
		xyz.openbmc_project.Control.Boot.RebootAttempts RetryAttempts u 3
fi

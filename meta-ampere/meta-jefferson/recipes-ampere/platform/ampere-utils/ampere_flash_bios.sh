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

#      BMC_GPIOW7_SPI0_BACKUP_SEL (GPIO 183) : 0 => to switch SPI0_CS0_FL1_L to secondary SPI Nor device
#                                              1 => to switch SPI0_CS0_FL0_L to primary SPI Nor device

# shellcheck disable=SC2046

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
	HOST_MTD=${HOST_MTD/"mtd"/""}
	flashcp -v "$IMAGE" /dev/"$HOST_MTD"
}

if [ $# -eq 0 ]; then
	echo "Usage: $(basename "$0") <UEFI/EDKII image file> <DEV_SEL> "
	echo "Where:"
	echo "    DEV_SEL 1 is Primary SPI (by default), 2 is Second SPI"
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
else
	echo "--- Chassis state : off"
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
	echo "Run update Second Host SPI-NOR"
	gpioset $(gpiofind spi0-backup-sel)=0       # Second SPI
else
	echo "Please choose primary SPI (1) or second SPI (2)"
	exit 0
fi

# Flash the firmware
do_flash

# Switch the SPI bus to the primary spi device
echo "Switch to the Primary Host SPI-NOR"
if ! gpioset $(gpiofind spi0-backup-sel)=1; then
	echo "ERROR: Switch to the Primary Host SPI-NOR. Please check gpio state"
	exit 1
fi

# Switch the host SPI bus to HOST."
echo "--- Switch the host SPI bus to HOST."
if ! gpioset $(gpiofind spi0-program-sel)=0; then
	echo "ERROR: Switch the host SPI bus to HOST. Please check gpio state"
	exit 1
fi

if [ "$chassisstate" == 'On' ];
then
	sleep 5
	echo "Turn on the Host"
	obmcutil poweron
fi

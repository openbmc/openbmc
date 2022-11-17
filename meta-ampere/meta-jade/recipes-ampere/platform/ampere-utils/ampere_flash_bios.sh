#!/bin/bash
#
# Copyright (c) 2021 Ampere Computing LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

do_flash () {
	# Check the PNOR partition available
	HOST_MTD=$(< /proc/mtd grep "pnor-uefi" | sed -n 's/^\(.*\):.*/\1/p')
	if [ -z "$HOST_MTD" ];
	then
		# Check the ASpeed SMC driver bound before
		HOST_SPI=/sys/bus/platform/drivers/spi-aspeed-smc/1e630000.spi
		if [ -d "$HOST_SPI" ]; then
			echo "Unbind the ASpeed SMC driver"
			echo 1e630000.spi > /sys/bus/platform/drivers/spi-aspeed-smc/unbind
			sleep 2
		fi

		# If the PNOR partition is not available, then bind again driver
		echo "--- Bind the ASpeed SMC driver"
		echo 1e630000.spi > /sys/bus/platform/drivers/spi-aspeed-smc/bind
		sleep 2

		HOST_MTD=$(< /proc/mtd grep "pnor-uefi" | sed -n 's/^\(.*\):.*/\1/p')
		if [ -z "$HOST_MTD" ];
		then
			echo "Fail to probe Host SPI-NOR device"
			exit 1
		fi
	fi

	echo "--- Flashing firmware to @/dev/$HOST_MTD"
	flashcp -v "$IMAGE" /dev/"$HOST_MTD"
}


if [ $# -eq 0 ]; then
	echo "Usage: $(basename "$0") <BIOS image file>"
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

# Switch the host SPI bus to BMC"
echo "--- Switch the host SPI bus to BMC."
if ! gpioset 0 226=0; then
	echo "ERROR: Switch the host SPI bus to BMC. Please check gpio state"
	exit 1
fi

# Switch the host SPI bus (between primary and secondary)
# 227 is BMC_SPI0_BACKUP_SEL
if [[ $DEV_SEL == 1 ]]; then
	echo "Run update primary Host SPI-NOR"
	gpioset 0 227=0       # Primary SPI
elif [[ $DEV_SEL == 2 ]]; then
	echo "Run update secondary Host SPI-NOR"
	gpioset 0 227=1       # Second SPI
else
	echo "Please choose primary SPI (1) or second SPI (2)"
	exit 0
fi

# Flash the firmware
do_flash

# Switch the host SPI bus to HOST."
echo "--- Switch the host SPI bus to HOST."
if ! gpioset 0 226=1; then
	echo "ERROR: Switch the host SPI bus to HOST. Please check gpio state"
	exit 1
fi

if [ "$chassisstate" == 'On' ];
then
	sleep 5
	echo "Turn on the Host"
	obmcutil poweron
fi

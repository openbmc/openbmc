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
	HOST_MTD=$(cat /proc/mtd | grep "pnor" | sed -n 's/^\(.*\):.*/\1/p')
	if [ -z "$HOST_MTD" ];
	then
		# If the PNOR partition is not available, then bind again driver
		echo "--- Bind the ASpeed SMC driver"
		echo 1e630000.spi > /sys/bus/platform/drivers/aspeed-smc/bind
		sleep 2

		HOST_MTD=$(cat /proc/mtd | grep "pnor" | sed -n 's/^\(.*\):.*/\1/p')
		if [ -z "$HOST_MTD" ];
		then
			echo "Fail to probe Host SPI-NOR device"
			exit 1
		fi

		echo "--- Flashing firmware to @/dev/$HOST_MTD"
		flash_eraseall /dev/$HOST_MTD
		flashcp -v $IMAGE /dev/$HOST_MTD

		echo "--- Unbind the ASpeed SMC driver"
		echo 1e630000.spi > /sys/bus/platform/drivers/aspeed-smc/unbind
	else
		echo "--- Flashing firmware to @/dev/$HOST_MTD"
		flash_eraseall /dev/$HOST_MTD
		flashcp -v $IMAGE /dev/$HOST_MTD
	fi
}


if [ $# -eq 0 ]; then
	echo "Usage: $(basename $0) <BIOS image file>"
	exit 0
fi

IMAGE="$1"
if [ ! -f $IMAGE ]; then
	echo $IMAGE
	echo "The image file $IMAGE does not exist"
	exit 1
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
fi

# Switch the host SPI bus to BMC"
echo "--- Switch the host SPI bus to BMC."
gpioset 0 226=0

if [[ $? -ne 0 ]]; then
	echo "ERROR: Switch the host SPI bus to BMC. Please check gpio state"
	exit 1
fi

# Flash the firmware
do_flash

# Switch the host SPI bus to HOST."
echo "--- Switch the host SPI bus to HOST."
gpioset 0 226=1

if [[ $? -ne 0 ]]; then
	echo "ERROR: Switch the host SPI bus to HOST. Please check gpio state"
	exit 1
fi

if [ "$chassisstate" == 'On' ];
then
	sleep 5
	echo "Turn on the Host"
	obmcutil chassison
fi

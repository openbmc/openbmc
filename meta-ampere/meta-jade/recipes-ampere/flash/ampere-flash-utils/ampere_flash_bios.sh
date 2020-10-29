#!/bin/bash
#
# Copyright (c) 2020 Ampere Computing LLC
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

do_flash_bios() {
	BIOS_MTD=$(cat /proc/mtd | grep "pnor" | sed -n 's/^\(.*\):.*/\1/p')
	BIOS_IMAGE="$1"

	if [ ! -f $BIOS_IMAGE ]; then
		echo $BIOS_IMAGE
		echo "Cannot find the BIOS image file"
		exit 1
	fi

	echo "turning chassis off"
	obmcutil chassisoff
	sleep 20

	# 226 is BMC_GPIOAC2_SPI0_PROGRAM_SEL
	gpioset 0 226=0
	# 227 is BMC_GPIOAC3_SPI0_BACKUP_SEL
	gpioset 0 227=0

	echo "Flashing BIOS @/dev/$BIOS_MTD"
	flash_eraseall /dev/$BIOS_MTD
	flashcp -v $BIOS_IMAGE /dev/$BIOS_MTD

	# 227 is BMC_GPIOAC3_SPI0_BACKUP_SEL
	gpioset 0 227=0
	# 226 is BMC_GPIOAC2_SPI0_PROGRAM_SEL
	gpioset 0 226=1

}

if [ $# -eq 0 ]; then
	echo "Usage: $(basename $0) <BIOS image file>"
	exit 0
fi

do_flash_bios $@

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

# This script is called by org.openbmc.control.Firmware.service to
# run Firmware update for Ampere Computing LLC platforms
#
#  1- BMC takes the control of EEPROM
#  2- Write firmware image to EEPROM and read data back for validation
#  3- Switch EEPROM control to Host

# $1 will be the type of upgrading
# $2 will be the firmware image
# $3 will be the type of image (optional; 1 is Main , 2 is Failover)
TYPE=$1
IMAGE=$2

if [ -z "$3" ]
then
	DEV_SEL="1"    # by default, select Main image
else
	DEV_SEL=$3
fi

do_smpmpro_upgrade() {
	I2C_BUS_DEV="1"
	EEPROM_ADDR="0x50"
	FIRMWARE_IMAGE=$IMAGE

	# lock the power control
	echo "--- Locking power control"
	systemctl start reboot-guard-enable.service

	# Write Firmware to EEPROM and read back for validation
	ampere_eeprom_prog -b $I2C_BUS_DEV -s $EEPROM_ADDR -p -f $FIRMWARE_IMAGE

	# unlock the power control
	echo "--- Unlocking power control"
	systemctl start reboot-guard-disable.service

}

do_fru_upgrade() {
	FRU_DEVICE="/sys/bus/i2c/devices/3-0050/eeprom"
	FRU_IMAGE=$IMAGE

	ampere_fru_upgrade -d $FRU_DEVICE -f $FRU_IMAGE
	echo "Done"
}

if [ $# -eq 0 ]; then
	echo "Usage: $(basename $0) <Type> <Image file> <DEV_SEL>"
	echo "If Type is smpmpro, then DEV_SEL must is 1 (MAIN EEPROM), 2 (Failover)"
	exit 0
fi

if [[ $TYPE == "smpmpro" ]]; then

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

	# Switch EEPROM control to BMC AST2500 I2C
	# 226 is BMC_GPIOAC2_SPI0_PROGRAM_SEL
	gpioset 0 226=0

	# 08 is BMC_GPIOB0_I2C_BACKUP_SEL
	if [[ $DEV_SEL == 1 ]]; then
		echo "Run update Main EEPROM"
		gpioset 0 8=1       # Main EEPROM
	elif [[ $DEV_SEL == 2 ]]; then
		echo "Run update Second EEPROM"
		gpioset 0 8=0       # Second EEPROM
	else
	echo "Please choose Main (1) or Second EEPROM (2)"
		exit 0
	fi

	# Run EEPROM update: write/read/validation with CRC32 checksum
	do_smpmpro_upgrade $@

	# Switch EEPROM control to Host
	# 08 is BMC_GPIOB0_I2C_BACKUP_SEL
	gpioset 0 8=1
	# 226 is BMC_GPIOAC2_SPI0_PROGRAM_SEL
	gpioset 0 226=1

	if [ "$chassisstate" == 'On' ];
	then
		sleep 5
		echo "Turn on the Host"
		obmcutil chassison
	fi
fi

if [[ $TYPE == "fru" ]]; then
	# Run FRU update
	do_fru_upgrade $@
fi

EXIT_STATUS=$?

exit $EXIT_STATUS

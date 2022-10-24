#!/bin/bash

# Helper script to report firmware version for components on the system (MB CPLD, Backplane CPLD, …)
# Author : Hieu Huynh (hieu.huynh@amperecomputing.com)
#
# Get MB CPLD firmware revision:
#    ampere_firmware_version.sh mb_cpld
#
# Get BMC CPLD firmware revision:
#    ampere_firmware_version.sh bmc_cpld
#
# Get Backplane CPLD firmware revision:
#    ampere_firmware_version.sh bp_cpld <id>
#    <id>: 1 for Front Backplane 1
#          2 for Front Backplane 2
#          3 for Front Backplane 3
#          4 for Rear Backplane 1
#          5 for Rear Backplane 2

# shellcheck disable=SC2046

do_mb_cpld_firmware_report() {
	echo "MB CPLD"
	gpioset $(gpiofind hpm-fw-recovery)=1
	gpioset $(gpiofind jtag-program-sel)=1
	sleep 1
	ampere_cpldupdate_jtag -v
	ampere_cpldupdate_jtag -i
}

do_bmc_cpld_firmware_report() {
	echo "BMC CPLD (Only for DC-SCM board)"
	gpioset $(gpiofind jtag-program-sel)=0
	sleep 1
	ampere_cpldupdate_jtag -v
	ampere_cpldupdate_jtag -i
}

do_bp_cpld_firmware_report() {
	BP_ID=$1
	if [[ $BP_ID == 1 ]]; then
		echo "Front Backplane 1 CPLD"
		ampere_cpldupdate_i2c -b 101 -s 0x40 -t 2 -v
		ampere_cpldupdate_i2c -b 101 -s 0x40 -t 2 -i
	elif [[ $BP_ID == 2 ]]; then
		echo "Front Backplane 2 CPLD"
		ampere_cpldupdate_i2c -b 102 -s 0x40 -t 2 -v
		ampere_cpldupdate_i2c -b 102 -s 0x40 -t 2 -i
	elif [[ $BP_ID == 3 ]]; then
		echo "Front Backplane 3 CPLD"
		ampere_cpldupdate_i2c -b 100 -s 0x40 -t 2 -v
		ampere_cpldupdate_i2c -b 100 -s 0x40 -t 2 -i
	elif [[ $BP_ID == 4 ]]; then
		echo "Rear Backplane 1 CPLD"
		ampere_cpldupdate_i2c -b 103 -s 0x40 -t 2 -v
		ampere_cpldupdate_i2c -b 103 -s 0x40 -t 2 -i
	elif [[ $BP_ID == 5 ]]; then
		echo "Rear Backplane 2 CPLD"
		ampere_cpldupdate_i2c -b 104 -s 0x40 -t 2 -v
		ampere_cpldupdate_i2c -b 104 -s 0x40 -t 2 -i
	fi
}

if [ $# -eq 0 ]; then
	echo "Usage:"
	echo "  - Get MB CPLD firmware revision"
	echo "     $(basename "$0") mb_cpld"
	echo "  - Get BMC CPLD firmware revision"
	echo "     $(basename "$0") bmc_cpld"
	echo "  - Get Backplane CPLD firmware revision"
	echo "     $(basename "$0") bp_cpld <id>"
	echo "    <id>:"
	echo "        1 - FrontBP1"
	echo "        2 - FrontBP2"
	echo "        3 - FrontBP3"
	echo "        4 - RearBP1"
	echo "        5 - RearBP2"
	exit 0
fi

TYPE=$1
ID=$2

if [[ $TYPE == "mb_cpld" ]]; then
	do_mb_cpld_firmware_report
elif [[ $TYPE == "bmc_cpld" ]]; then
	do_bmc_cpld_firmware_report
elif [[ $TYPE == "bp_cpld" ]]; then
	if [ -z "$ID" ]; then
		echo "Please choose backplanes id: 1 - FrontBP1, 2 - FrontBP2, 3 - FrontBP3, 4 - FrontBP4, 5 - FrontBP5"
		exit 0
	elif [[ "$ID" -ge "1" ]] && [[ "$ID" -le "5" ]]; then
		do_bp_cpld_firmware_report "$ID"
	else
		echo "Backplanes id invalid"
	fi
fi

exit 0

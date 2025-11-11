#!/bin/bash

# shellcheck source=meta-ampere/meta-common/recipes-ampere/platform/ampere-utils/ampere_redfish_utils.sh
source /usr/sbin/ampere_redfish_utils.sh

smpro_path() {
	if [ "$1" == 0 ]; then
		echo "/sys/bus/i2c/drivers/smpro-core/2-004f"
	else
		echo "/sys/bus/i2c/drivers/smpro-core/2-004e"
	fi
}

parse_phy_syndrome_s1_type() {
	s1=$1
	slice=$((s1 & 0xf))
	ubit=$(((s1 & 0x10) >> 4))
	lbit=$(((s1 & 0x20) >> 5))
	uMsg="Upper Nibble: No Error"
	lMsg="Lower Nibble: No Error"
	if [ $ubit == 1 ]; then
		uMsg="Upper Nibble: No rising edge error"
	fi
	if [ $lbit == 1 ]; then
		lMsg="Lower Nibble: No rising edge error"
	fi
	echo "Slice $slice: $uMsg, $lMsg"
}

parse_phy_syndrome() {
	s0=$1
	s1=$2
	case $s0 in
		1)
			echo "PHY Training Setup failure"
			;;
		2)
			s1Msg=$(parse_phy_syndrome_s1_type "$s1")
			echo "PHY Write Leveling failure: $s1Msg"
			;;
		3)
			echo "PHY Read Gate Leveling failure"
			;;
		4)
			echo "PHY Read Leveling failure"
			;;
		5)
			echo "PHY Software Training failure"
			;;
		*)
			echo "N/A"
			;;
	esac
}

parse_dimm_syndrome() {
	s0=$1
	case $s0 in
		1)
			echo "DRAM VREFDQ Training failure"
			;;
		2)
			echo "LRDIMM DB Training failure"
			;;
		3)
			echo "LRDIMM DB Software Training failure"
			;;
		*)
			echo "N/A"
			;;
	esac
}

log_err_to_redfish_err() {
	channel="$(printf '%d' "0x$1" 2>/dev/null)"
	data="$(printf '%d' "0x$2" 2>/dev/null)"
	trErrType=$((data & 0x03))
	rank=$(((data & 0x1C) >> 2))
	syndrome0=$(((data & 0xE0) >> 5))
	syndrome1=$(((data & 0xFF00) >> 8))

	# PHY sysdrom errors
	fType=""
	redfisComp="DIMM"
	error=""
	if [ $trErrType == 1 ]; then
		fType="PHY training failure"
		error=$(parse_phy_syndrome $syndrome0 $syndrome1)
	# DIMM traning errors
	elif [ $trErrType == 2 ]; then
		fType="DIMM training failure"
		error=$(parse_dimm_syndrome $syndrome0)
	else
		fType="Invalid DIMM Syndrome error type"
		error="NA"
	fi

	redfisMsg="$redfisComp Slot $channel MCU rank $rank: $fType: $error"

	add_ampere_critical_sel  "Smpro" "$redfisMsg"
}

log_err_to_sel_err() {
	channel="$(printf '%d' "0x$1" 2>/dev/null)"
	data="$(printf '%d' "0x$2" 2>/dev/null)"
	byte0=$(((data & 0xff00) >> 8))
	byte1=$((data & 0xff))
	evtdata0=$((EVENT_DIR_ASSERTION | OEM_SENSOR_SPECIFIC))
	evtdata1=$(((channel << 4) | BOOT_SYNDROME_DATA | (socket << 3)))

	# phy sysdrom errors
	# OEM data bytes
	#   oem id: 3 bytes [0x3a 0xcd 0x00]
	#   sensor num: 1 bytes
	#   sensor type: 1 bytes
	#   data bytes: 4 bytes
	#   sel type: 4 byte [0x00 0x00 0x00 0xC0]
	busctl call xyz.openbmc_project.Logging.IPMI \
		/xyz/openbmc_project/Logging/IPMI \
		xyz.openbmc_project.Logging.IPMI IpmiSelAddOem \
		sayy "" 12 \
		0x3a 0xcd 0x00 \
		"$SENSOR_TYPE_SYSTEM_FW_PROGRESS" "$SENSOR_BOOT_PROGRESS" \
		"$evtdata0" "$evtdata1" "$byte0" "$byte1" \
		0x00 0x00 0x00 0xC0
}

BOOT_SYNDROME_DATA=4
SENSOR_BOOT_PROGRESS=235
EVENT_DIR_ASSERTION=0x00
OEM_SENSOR_SPECIFIC=0x70
SENSOR_TYPE_SYSTEM_FW_PROGRESS=0x0F

socket=$1
base="$(smpro_path "$socket")"

# For the second socket, it is required to read out to
# clear all old boot progress before query the dimm
# training fail info.
# Normally, it would take up to 12 times to read them all
# Make the value to 16 to make sure it always works.
if [ "$socket" == "1" ]; then
	path=("$base"/smpro-misc.*.auto/boot_progress)
	filename="${path[0]}"
	if [ ! -f "$filename" ];
	then
		echo "Error: $filename not found"
	else
		for ((i=0; i<16; i++))
		do
			cat "$filename" > /dev/null 2>&1
		done
	fi
fi

# Checking for DIMM slot 0-15
for ((i=0; i<16; i++))
do
	path=("$base"/smpro-errmon.*.auto/event_dimm"${i}"_syndrome)
	filename="${path[0]}"
	if [ ! -f "$filename" ];
	then
		echo "Error: $filename not found"
		continue
	fi

	line=$(cat "$filename")
	if [ -n "$line" ];
	then
		log_err_to_redfish_err "$i" "$line"
		log_err_to_sel_err "$i" "$line"
	fi
done

exit 0;

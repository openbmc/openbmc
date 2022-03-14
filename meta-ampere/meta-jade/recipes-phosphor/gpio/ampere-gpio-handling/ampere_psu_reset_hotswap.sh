#!/bin/sh

# When system only use single PSU ( ex : PSU1 ) to power ON normal 12V,
# HSC2 will be trigged Fault event (FET health).
# At this time, to plug-in PSU2 in system, PSU2 won't deliver power to
# +12V_MB because HSC2 is protected by Fault event.
# Due to HSC2 protected, the PSU redundancy mechanism can't be created.
# Once PSU1 is plugged out at this moment, system will crash ( reset )
# because +12V_MB dropped.

# BMC SW work-around solution:
# - When BMC detect event PSU is plugged in system, BMC will reset HSC
# by disbale HOT SWAP and then enable HOT SWAP through pmbus command to clear
# Fault event.

# Note:
# In case hot swap occurs during BMC reset, BMC still not in operation state,
# BMC can't detect PSU plug/unplug, then the work-around won't be executed

# Author: Chanh Nguyen <chnguyen@amperecomputing.com>

HSC1_PMBUS_NUM=10
HSC2_PMBUS_NUM=10
HSC1_SLAVE_ADDR=0x10
HSC2_SLAVE_ADDR=0x11
OPERATION=0x01
STATUS_MFR_SPECIFIC=0x80

# $1 will be the name of the psu
PSU=$1

if [ "$PSU" = 0 ]; then
	HSC_PMBUS_NUM=$HSC1_PMBUS_NUM
	HSC_SLAVE_ADDR=$HSC1_SLAVE_ADDR
elif [ "$PSU" = 1 ]; then
	HSC_PMBUS_NUM=$HSC2_PMBUS_NUM
	HSC_SLAVE_ADDR=$HSC2_SLAVE_ADDR
else
	echo "Please choose PSU1 (0) or PSU2 (1)"
	echo "Ex: ampere_psu_reset_hotswap.sh 0"
	exit 0
fi

# Check HOST state
chassisstate=$(obmcutil chassisstate | awk -F. '{print $NF}')
if [ "$chassisstate" = 'Off' ]; then
	echo "HOST is being OFF, so can't access the i2c $HSC_PMBUS_NUM. Please Turn ON HOST !"
	exit 1
fi

# Check FET health problems
if ! data=$(i2cget -f -y $HSC_PMBUS_NUM $HSC_SLAVE_ADDR $STATUS_MFR_SPECIFIC); then
	echo "ERROR: Can't access the i2c. Please check /dev/i2c-$HSC_PMBUS_NUM"
	exit 1
fi

psu_sts=$(((data & 0x80) != 0))

if [ $psu_sts = 1 ]; then
	echo "PSU $PSU: FET health problems have been detected"
	echo "Reset Hot swap output on PSU $PSU"
	# Disable Hot swap output
	write_data=0x00
	i2cset -f -y $HSC_PMBUS_NUM $HSC_SLAVE_ADDR $OPERATION $write_data b

	# Enable Hot swap output
	write_data=0x80;
	i2cset -f -y $HSC_PMBUS_NUM $HSC_SLAVE_ADDR $OPERATION $write_data b

else
	echo "PSU $PSU: FET health problems have not been detected"
fi

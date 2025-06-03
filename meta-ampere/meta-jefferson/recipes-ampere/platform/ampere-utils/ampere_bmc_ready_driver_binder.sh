#!/bin/bash

# Each driver include driver name and driver path
declare -a DRIVER_NAMEs=(
			 "6-0051"
			)
# Driver path should include / at the end
declare -a DRIVER_PATHs=(
			 "/sys/bus/i2c/drivers/rtc-pcf8563/"
			)

# get length of an array
arraylength=${#DRIVER_NAMEs[@]}

# use for loop to read all values and indexes
for (( i=0; i<"${arraylength}"; i++ ));
do
	bindFile="${DRIVER_PATHs[$i]}bind"
	driverDir="${DRIVER_PATHs[$i]}${DRIVER_NAMEs[$i]}"
	echo "binding ${DRIVER_NAMEs[$i]} path ${DRIVER_PATHs[$i]} on BMC booted"
	if [ -d "$driverDir" ]; then
		echo "Driver ${DRIVER_NAMEs[$i]} is already bound."
	else
		echo "${DRIVER_NAMEs[$i]}" > "$bindFile"
	fi
done

exit 0

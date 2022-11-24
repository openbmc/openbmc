#!/bin/bash

DELAY_BEFORE_BIND=5000000
# Each driver include driver name and driver path
declare -a DRIVER_NAMEs=("2-004f"
                         "2-004e"
                        )
# Driver path should include / at the end
declare -a DRIVER_PATHs=("/sys/bus/i2c/drivers/smpro-core/"
                         "/sys/bus/i2c/drivers/smpro-core/"
                        )

# get length of an array
arraylength=${#DRIVER_NAMEs[@]}

usleep $DELAY_BEFORE_BIND
# use for loop to read all values and indexes
for (( i=0; i<"${arraylength}"; i++ ));
do
	bindFile="${DRIVER_PATHs[$i]}bind"
	driverDir="${DRIVER_PATHs[$i]}${DRIVER_NAMEs[$i]}"
	if [ -d "$driverDir" ]; then
		echo "Driver ${DRIVER_NAMEs[$i]} is already bound."
		continue;
	fi
	echo "${DRIVER_NAMEs[$i]}" > "$bindFile"
done

exit 0

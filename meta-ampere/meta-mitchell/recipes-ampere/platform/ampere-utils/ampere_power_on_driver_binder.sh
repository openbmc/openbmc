#!/bin/bash

# Each driver include driver name and driver path
declare -a DRIVER_NAMEs=(
			 "107-0070"
			 "100-0071"
			 "101-0071"
			 "102-0071"
			 "103-0071"
			 "104-0071"
			 "100-0050"
			 "101-0050"
			 "102-0050"
			 "100-004c"
			 "101-004c"
			 "102-004c"
			)
# Driver path should include / at the end
declare -a DRIVER_PATHs=(
			 "/sys/bus/i2c/drivers/pca954x/"
			 "/sys/bus/i2c/drivers/pca954x/"
			 "/sys/bus/i2c/drivers/pca954x/"
			 "/sys/bus/i2c/drivers/pca954x/"
			 "/sys/bus/i2c/drivers/pca954x/"
			 "/sys/bus/i2c/drivers/pca954x/"
			 "/sys/bus/i2c/drivers/at24/"
			 "/sys/bus/i2c/drivers/at24/"
			 "/sys/bus/i2c/drivers/at24/"
			 "/sys/bus/i2c/drivers/lm75/"
			 "/sys/bus/i2c/drivers/lm75/"
			 "/sys/bus/i2c/drivers/lm75/"
			)

# get length of an array
arraylength=${#DRIVER_NAMEs[@]}

# use for loop to read all values and indexes
for (( i=0; i<"${arraylength}"; i++ ));
do
	bindFile="${DRIVER_PATHs[$i]}bind"
	driverDir="${DRIVER_PATHs[$i]}${DRIVER_NAMEs[$i]}"
	echo "binding ${DRIVER_NAMEs[$i]} path ${DRIVER_PATHs[$i]} on Chassi Power On"
	if [ -d "$driverDir" ]; then
		echo "Driver ${DRIVER_NAMEs[$i]} is already bound."
	else
		echo "${DRIVER_NAMEs[$i]}" > "$bindFile"
	fi
done

exit 0

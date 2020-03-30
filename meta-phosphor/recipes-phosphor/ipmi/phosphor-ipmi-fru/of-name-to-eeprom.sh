#!/bin/bash -eu
[ -n "${OF_NAME+1}" ] || exit 0
path="$(grep -xl "$OF_NAME" /sys/bus/i2c/devices/*/of_node/name)"
eeprom="${path%/of_node/name}/eeprom"
sed -i "s,^SYSFS_PATH=.*$,SYSFS_PATH=$eeprom," "$1"

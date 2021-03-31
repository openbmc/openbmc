#!/bin/bash

FAN_TABLE_FILE_IN="/usr/share/swampd/config.json.in"
TEMP_FILE="$(mktemp)"
cp "$FAN_TABLE_FILE_IN" "$TEMP_FILE"

# wait for fan dbus
mapper wait /xyz/openbmc_project/sensors/fan_tach/fan0
mapper wait /xyz/openbmc_project/sensors/fan_tach/fan1
mapper wait /xyz/openbmc_project/sensors/fan_tach/fb_fan0
mapper wait /xyz/openbmc_project/sensors/fan_tach/fb_fan1
mapper wait /xyz/openbmc_project/sensors/fan_tach/fb_fan2

# generate fan table writePath
Fan_0_To_4_Hwmon="$(ls /sys/devices/platform/ahb/ahb\:*/*pwm-fan-controller/hwmon/)"

if [[ "$Fan_0_To_4_Hwmon" != "" ]]; then
     sed -i "s/@Fan_0_To_4_Hwmon@/$Fan_0_To_4_Hwmon/g" $TEMP_FILE
fi

# Use shell parameter expansion to trim the ".in" suffix
mv "$TEMP_FILE" "${FAN_TABLE_FILE_IN%".in"}"

# start read margin temp wait
/usr/bin/read-margin-temp-wait.sh &

exit 0

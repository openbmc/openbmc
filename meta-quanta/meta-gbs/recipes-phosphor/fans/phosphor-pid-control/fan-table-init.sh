#!/bin/bash

# wait for fan dbus
mapper wait /xyz/openbmc_project/sensors/fan_tach/fan0
mapper wait /xyz/openbmc_project/sensors/fan_tach/fan1
mapper wait /xyz/openbmc_project/sensors/fan_tach/fb_fan0
mapper wait /xyz/openbmc_project/sensors/fan_tach/fb_fan1
mapper wait /xyz/openbmc_project/sensors/fan_tach/fb_fan2

/usr/bin/fan-default-speed.sh

# generate fan table writePath
Fan_0_To_4_Hwmon="$(ls -la /sys/class/hwmon |grep pwm |  head -n 1| tail -n +1|cut -d '/' -f 9)"

if [[ "$Fan_0_To_4_Hwmon" != "" ]]
then
     sed -i "s/Fan_0_To_4_Hwmon/$Fan_0_To_4_Hwmon/g" /usr/share/swampd/config.json
fi

# start read margin temp wait
/usr/bin/read-margin-temp-wait.sh &

exit 0

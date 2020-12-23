#!/bin/bash

MARGIN_TABLE_FILE="/usr/share/read-margin-temp/config-margin.json"

target_num="$(cat $MARGIN_TABLE_FILE | grep '"target"' | wc -l)"

cpu_hwmon="$(ls -la /sys/class/hwmon | grep f0082000 |  head -n 1 | tail -n +1 | cut -d '/' -f 11)"

# replace by the real cpu temperature hwmon path in runtime
if [[ "$cpu_hwmon" != "" ]]
then
     sed -i "s/cpu_hwmon/$cpu_hwmon/g" $MARGIN_TABLE_FILE
fi

# wait target dbus
for ((i=0; i<$target_num; i++))
do
    line_num=$((i+1))
    path="$(cat $MARGIN_TABLE_FILE | grep '"target"' | head -n ${line_num} | tail -n +${line_num} | cut -d '"' -f 4)"
    mapper wait $path
done

# start read margin temp
/usr/bin/read-margin-temp &

exit 0

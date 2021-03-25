#!/bin/bash

MARGIN_TABLE_FILE_IN="/usr/share/read-margin-temp/config-margin.json.in"
TEMP_FILE="$(mktemp)"
cp "$MARGIN_TABLE_FILE_IN" "$TEMP_FILE"

target_num="$(cat $TEMP_FILE | grep '"target"' | wc -l)"

# wait target dbus
for ((i = 0; i < ${target_num}; i++)); do
    line_num=$((i+1))
    path="$(cat $TEMP_FILE | grep '"target"' | head -n ${line_num} | tail -n +${line_num} | cut -d '"' -f 4)"
    mapper wait $path
done

nvmePath="/xyz/openbmc_project/sensors/temperature/nvme"
nvmeInventoryPath="/xyz/openbmc_project/inventory/system/chassis/motherboard/nvme"
# Get and Set WCTEMP
for ((i = 0; i < 16; i++)); do
    name="@WCTemp$(printf "%02d" $i)@"
    wcTemp=72000
    presentState=$(busctl get-property \
        xyz.openbmc_project.Inventory.Manager \
        ${nvmeInventoryPath}${i} \
        xyz.openbmc_project.Inventory.Item \
        Present | awk '{print $2}')

    if [[ $presentState == "true" ]]; then
        wcTemp=$(
            busctl get-property xyz.openbmc_project.nvme.manager \
                ${nvmePath}${i} \
                xyz.openbmc_project.Sensor.Threshold.Critical \
                CriticalHigh | awk '{print $2}'
        )
        wcTemp=$((wcTemp * 1000))
    fi

    sed -i "s/$name/${wcTemp}/g" $TEMP_FILE
done

# Use shell parameter expansion to trim the ".in" suffix
mv "$TEMP_FILE" "${MARGIN_TABLE_FILE_IN%".in"}"

# start read margin temp
/usr/bin/read-margin-temp &

exit 0

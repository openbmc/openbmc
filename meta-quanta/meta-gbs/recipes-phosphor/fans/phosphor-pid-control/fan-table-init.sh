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

presentGpio=()
presentState=()
gpioPath="/sys/class/gpio/gpio"
if [[ -f "/etc/nvme/nvme_config.json" ]]; then
    presentGpio=($(cat /etc/nvme/nvme_config.json | grep NVMeDrivePresentPin \
                   | awk '{print $2}' | cut -d "," -f 0))
fi

nvmePath="/xyz/openbmc_project/sensors/temperature/nvme"
nvmeInventoryPath="/xyz/openbmc_project/inventory/system/chassis/motherboard/nvme"
# Get and Set WCTEMP
for ((i = 0; i < 16; i++)); do
    name="@WCTemp$(printf "%02d" $i)@"
    wcTemp=72000

    if [[ -d "${gpioPath}${presentGpio[i]}" ]] &&
       [[ "$(cat ${gpioPath}${presentGpio[i]}/value)" == "0" ]]; then
        presentState[i]="true"
    else
        presentState[i]="false"
    fi

    if [[ "${presentState[i]}" == "true" ]]; then
        actualWCTemp=0
        for ((j = 0; j < 3; j++)); do
            actualWCTemp="$(
                busctl get-property xyz.openbmc_project.nvme.manager \
                    ${nvmePath}${i} \
                    xyz.openbmc_project.Sensor.Threshold.Critical \
                    CriticalHigh | awk '{print $2}'
            )"
            if [[ "${actualWCTemp}" -ne 0 ]]; then
                break
            fi

            echo "${nvmePath}${i} WCTemp was read to be 0, retrying after 1 sec sleep"
            sleep 1
        done

        if [[ "${actualWCTemp}" -ne 0 ]]; then
            wcTemp="$(echo "${actualWCTemp} -7" | awk '{printf $1 + $2}')"
        else
            echo "${nvmePath}${i} WCTemp was read to be 0, using default WCTemp: ${wcTemp}"
        fi
    fi

    sed -i "s/$name/${wcTemp}/g" "$TEMP_FILE"

    if [[ "${presentState[i]}" == "false" ]]; then
        sensorPath="${nvmePath}${i}"
        pathLine=$(grep -nw "$sensorPath" "$TEMP_FILE" | awk -F ':' '{print $1}')
        sed -i "$TEMP_FILE" -re "$((pathLine - 3)),$((pathLine + 6))d"
        if [ $i -eq 15 ]; then
            sed -i "$((pathLine - 4))s/,//" "$TEMP_FILE"
        fi

        listLine=$(grep -n "\"name\": \"nvme${i}\"" "$TEMP_FILE" | awk -F ':' '{print $1}')
        sed -i "$TEMP_FILE" -re "$((listLine - 1)),$((listLine + 21))d"
        if [ $i -eq 15 ]; then
            sed -i "$((listLine - 2))s/,//" "$TEMP_FILE"
        fi
    fi
done

# Use shell parameter expansion to trim the ".in" suffix
mv "$TEMP_FILE" "${FAN_TABLE_FILE_IN%".in"}"

exit 0

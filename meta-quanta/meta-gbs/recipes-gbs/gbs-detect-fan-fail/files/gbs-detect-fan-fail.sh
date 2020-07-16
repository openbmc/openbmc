#!/bin/bash

##
 #  This script is to handle fan fail condition which is described below.
 #  Fan fail means one fan RPM is under its CriticalLow. If BMC encounters
 #  such situation, then sets other fans to full speed.
 #

# get fan state
function get_fan_state() {
    get_property_path='xyz.openbmc_project.Sensor.Threshold.Critical CriticalAlarmLow'
    fan_state="$(busctl get-property $1 $2 $get_property_path | awk '{print $2}')"
    echo "$fan_state"
}

# check fan fail
function is_fan_fail() {
    fan_state=("$@")

    for i in "${fan_state[@]}"
    do
        if [ ! -z "$i" ]
        then
            if [ $i == "true" ]
            then
                echo 1
                return
            fi
        fi
    done
    echo 0
}

fan_tach_path=( '/xyz/openbmc_project/sensors/fan_tach/fan0'
                '/xyz/openbmc_project/sensors/fan_tach/fan1'
                '/xyz/openbmc_project/sensors/fan_tach/fb_fan0'
                '/xyz/openbmc_project/sensors/fan_tach/fb_fan1'
                '/xyz/openbmc_project/sensors/fan_tach/fb_fan2'
                )

check_fail_flag=0
is_fan_fail_flag=0
current_fan_state=()

while true
do
    for i in ${!fan_tach_path[@]}
    do
        mapper wait ${fan_tach_path[$i]}
        hwmon_path="$(mapper get-service ${fan_tach_path[0]})"
        current_fan_state[$i]=$(get_fan_state $hwmon_path ${fan_tach_path[$i]})
    done

    is_fan_fail_flag=$(is_fan_fail "${current_fan_state[@]}")

    # if fan fails then set all fans to full speed
    if [ $is_fan_fail_flag -eq 1 ] && [ $check_fail_flag -eq 0 ]
    then
        check_fail_flag=1
        systemctl stop phosphor-pid-control.service

    # fans recover to normal state
    elif [ $is_fan_fail_flag -eq 0 ] && [ $check_fail_flag -eq 1 ]
    then
        check_fail_flag=0
        systemctl restart phosphor-pid-control.service
    fi

    sleep 2
done

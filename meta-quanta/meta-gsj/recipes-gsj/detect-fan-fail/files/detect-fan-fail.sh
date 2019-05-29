#!/bin/bash

# get fan state by d-bus command
function get_fan_state() {
    get_property_path='xyz.openbmc_project.Sensor.Threshold.Critical CriticalAlarmLow'
    fan_state="$(busctl get-property $1 $2 $get_property_path | awk '{print $2}')"
    echo "$fan_state"
}

# set fan pwm by d-bus command
function set_fan_value() {
    set_property_path='xyz.openbmc_project.Control.FanPwm'
    busctl set-property $1 $2 $set_property_path Target t 255
}

fan_tach_path=( '/xyz/openbmc_project/sensors/fan_tach/Fan0_0_RPM'
                '/xyz/openbmc_project/sensors/fan_tach/Fan0_1_RPM'
                '/xyz/openbmc_project/sensors/fan_tach/Fan1_0_RPM'
                '/xyz/openbmc_project/sensors/fan_tach/Fan1_1_RPM'
                '/xyz/openbmc_project/sensors/fan_tach/Fan2_0_RPM'
                '/xyz/openbmc_project/sensors/fan_tach/Fan2_1_RPM'
                )

check_fail_flag=0
current_fan_state=()
while true; do
    hwmon_path="$(mapper get-service ${fan_tach_path[0]})"
    for i in ${!fan_tach_path[@]};
    do
        current_fan_state[$i%2]=$(get_fan_state $hwmon_path ${fan_tach_path[$i]})

        #Compare state of dual rotors with CriticalAlarmLow to check if fan fail
        if [ ${#current_fan_state[@]} -eq 2 ];then
            if [ "${current_fan_state[0]}" == "true" ] && \
            [ "${current_fan_state[1]}" == "true" ] ;then
                if [ $check_fail_flag -eq 0 ];then
                    systemctl stop phosphor-pid-control
                    for j in ${!fan_tach_path[@]};
                    do
                        #If fan fail is detect, set other fan rpm to pwm 255.
                        set_fan_value $hwmon_path ${fan_tach_path[$j]}
                        check_fail_flag=1
                    done
                fi
                current_fan_state=()
                break
            fi
            current_fan_state=()
        fi

        #fans are going to normal.
        if [ $i -eq $((${#fan_tach_path[@]}-1)) ] && [ $check_fail_flag -eq 1 ]; then
           check_fail_flag=0
           systemctl restart phosphor-pid-control
        fi
    done
    sleep 2
done

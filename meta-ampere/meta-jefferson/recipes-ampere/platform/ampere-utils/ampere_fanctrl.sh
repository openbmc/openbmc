#!/bin/bash

fan_hwmon_num=$(ls /sys/bus/i2c/drivers/adt7462/8-005c/hwmon)
fan_hwmon_path="/sys/class/hwmon"

DISABLED_STAT="Disabled"
ENABLED_STAT="Enabled"
ACTIVE_STR="active"
MANUAL_CONTROL_SERV="ampere-fanctrl-manual.service"

phosphor_fan_service=("phosphor-fan-control@0.service"
                      "phosphor-fan-monitor@0.service"
                      "phosphor-fan-presence-tach@0.service"
                      "phosphor-pid-control.service")

declare -A fan_id_list
# The index in $input_list and $pwm_list for each FAN
fan_list=(0 1 2 3)
# Mapping from real FAN index to the index in $fan_list
fan_id_list=([0]=0 [1]=1 [2]=2 [3]=3)
# The index of fan*_input file in sysfs for each fan
input_list=(1 3 5 7)
# The index of pwm* file in sysfs for each fan
pwm_list=(1 2 3 4)

function print_fan_indices(){
    indices=("${!fan_id_list[@]}")
    echo "${indices[@]}"
}

function stop_phosphor_fan_services() {
    status=$(getstatus)
    if [[ "$status" == *"$DISABLED_STAT" ]]; then
        echo "Fan control status is already disabled"
        exit 0
    fi

    systemctl start $MANUAL_CONTROL_SERV

    for service in "${phosphor_fan_service[@]}"
    do
        systemctl stop "$service"
    done
}

function start_phosphor_fan_services() {
    status=$(getstatus)
    if [[ "$status" == *"$ENABLED_STAT" ]]; then
        echo "Fan control status is already enabled"
        exit 0
    fi

    systemctl stop $MANUAL_CONTROL_SERV

    for service in "${phosphor_fan_service[@]}"
    do
        if [ "$service" == "phosphor-pid-control.service" ] &&
        [ "$(obmcutil chassisstate | awk -F. '{print $NF}')" == 'Off' ]; then
            continue
        fi
        systemctl start "$service"
    done
}

function getstatus() {
    status="$ENABLED_STAT"
    ret=0
    for service in "${phosphor_fan_service[@]}"
    do
        service_stat=$(systemctl is-active "$service")
        if [ "$service_stat" != "$ACTIVE_STR" ]; then
            if [ "$service" == "phosphor-pid-control.service" ] &&
            [ "$(obmcutil chassisstate | awk -F. '{print $NF}')" == 'Off' ]; then
                continue
            fi
            status="$DISABLED_STAT"
            ret=1
            break
        fi
    done
    echo "Thermal Control operational status: $status"
    exit ${ret}
}

function setstatus() {
    if [ "$1" == 0 ]; then
        # Enable fan services
        start_phosphor_fan_services
    else
        # Disable fan services
        stop_phosphor_fan_services
    fi
}

function setspeed() {
    # Get fan_pwm value of the fan
    fan_index=$1
    pwm_val=$2
    id=${fan_id_list[${fan_index}]}

    if [ -n "$id" ]
    then
        if ! { [[ "$pwm_val" =~ ^[0-9]+$ ]] && [ "$pwm_val" -ge 0 ] && [ "$pwm_val" -le 100 ]; }; then
            echo "Error: Invalid target PWM value - ${pwm_val}. The valid range is [0-100]"
            exit 1
        fi

        # Get pwm sysfs file of the fan
        fan_pwm=pwm${pwm_list[$id]}
        # Scale PWM to 255, adding 50 for rounding.
        pwm_scaled=$((((pwm_val * 255) + 50) / 100))

        if ! (echo "$pwm_scaled" > "${fan_hwmon_path}/${fan_hwmon_num}/${fan_pwm}");
        then
            echo "Error: Set Fan PWM Duty Cycle failed!"
            exit 255
        fi
    else
        echo "Error: Fan $fan_index doesn't exist. Valid indices are: $(print_fan_indices)"
        exit 1
    fi
}

function getspeed() {
    fan_index=$1
    id=${fan_id_list[${fan_index}]}
    if [ -n "$id" ]
    then
        fan_input=fan${input_list[$id]}_input
        fan_pwm=pwm${pwm_list[$id]}

        # Get fan speed and pwm from hwmon
        tach_reading=$(cat "${fan_hwmon_path}/${fan_hwmon_num}/${fan_input}")
        ret=$?
        if [ $ret != 0 ]; then
            echo "Error: Get Fan Speed failed!"
            exit 1
        fi

        pwm_reading=$(cat "${fan_hwmon_path}/${fan_hwmon_num}/${fan_pwm}")
        ret=$?
        if [ $ret != 0 ]; then
            echo "Error: Get Fan PWM Duty Cycle failed!"
            exit 1
        fi

        # Scale PWM to 100, adding 127 for rounding.
        pwm_reading_scaled=$(((("$pwm_reading" * 100) + 127) / 255))

        echo "FAN${fan_index}, PWM: $pwm_reading, Duty cycle: $pwm_reading_scaled%, Speed(RPM): $tach_reading"
    else
        echo "Error: Fan $fan_index doesn't exist. Valid indices are: $(print_fan_indices)"
        exit 1
    fi
}

# Usage of this utility
function usage() {
    echo "Usage:"
    echo "  ampere_fanctrl.sh [getstatus] [setstatus <0:enable|1:disable>] [setspeed all/<fan> <duty>] [getspeed all/<fan>]"
    echo "  fan: 0-3"
    echo "  duty: 1-100"
}

if [ "$1" == "getstatus" ]; then
    getstatus
elif [ "$1" == "setstatus" ] && [ -n "$2" ]; then
    setstatus "$2"
elif [ "$1" == "setspeed" ]; then
    if [ "$2" == "force" ]; then
        # Shift to the command argument position
        shift
    else
        status=$(getstatus)
        if [[ "$status" == *"$ENABLED_STAT" ]]; then
            echo "Setting speeds is not allowed when fan control is enabled."
            exit 1
        fi
    fi
    if [ "$2" == "all" ] && [ -n "$3" ]; then
        for fan in "${fan_list[@]}"
        do
            setspeed "$fan" "$3"
        done
    elif [ -n "$2" ] && [ -n "$3" ]; then
        setspeed "$2" "$3"
    else
        usage
    fi
elif [ "$1" == "getspeed" ]; then
    if [ "$2" == "all" ]; then
        for fan in "${fan_list[@]}"
        do
            getspeed "$fan"
        done
    elif [ -n "$2" ]; then
        getspeed "$2"
    else
        usage
    fi
else
    usage
fi
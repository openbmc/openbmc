#!/bin/bash
# shellcheck disable=SC2004

fan_hwmon_num_8_20=$(ls /sys/bus/i2c/drivers/max31790/8-0020/hwmon)
fan_hwmon_num_8_2f=$(ls /sys/bus/i2c/drivers/max31790/8-002f/hwmon)
fan_hwmon_path="/sys/class/hwmon"

phosphor_fan_service=("phosphor-fan-control@0.service"
                      "phosphor-fan-monitor@0.service"
                      "phosphor-fan-presence-tach@0.service")

declare -A fan_id_list
fan_list=(0 1 2 3 4 5)
fan_id_list=([0]=0 [1]=1 [2]=2 [3]=3 [4]=4 [5]=5)
input_list_f=(11 3 1 11 3 1)
input_list_r=(12 4 2 12 4 2)
pwm_list=(4 2 1 4 2 1)
hwmon_list=("$fan_hwmon_num_8_2f"
            "$fan_hwmon_num_8_2f"
            "$fan_hwmon_num_8_2f"
            "$fan_hwmon_num_8_20"
            "$fan_hwmon_num_8_20"
            "$fan_hwmon_num_8_20")

function stop_phosphor_fan_services() {
    for service in "${phosphor_fan_service[@]}"
    do
        systemctl stop "$service"
        busctl call org.freedesktop.systemd1 /org/freedesktop/systemd1 org.freedesktop.systemd1.Manager MaskUnitFiles asbb 1 "$service" true true
    done
    systemctl daemon-reload
}

function start_phosphor_fan_services() {
    for service in "${phosphor_fan_service[@]}"
    do
        busctl call org.freedesktop.systemd1 /org/freedesktop/systemd1 org.freedesktop.systemd1.Manager UnmaskUnitFiles asb 1 "$service" true
    done

    systemctl daemon-reload

    for service in "${phosphor_fan_service[@]}"
    do
        systemctl start "$service"
    done
}

function getstatus() {
    status="Enabled"
    ret=0
    for service in "${phosphor_fan_service[@]}"
    do
        service_stt=$(systemctl is-active "$service" | grep inactive)
        if [ -n "$service_stt" ]; then
            status="Disabled"
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
        # Get pwm sysfs file of the fan
        hwmon_num=${hwmon_list[$id]}
        fan_pwm=pwm${pwm_list[$id]}
        # Scale PWM to 255, adding 50 for rounding.
        pwm_scaled=$(((($pwm_val * 255) + 50) / 100))

        if ! (echo "$pwm_scaled" > "${fan_hwmon_path}/${hwmon_num}/${fan_pwm}");
        then
            echo "Error: Set Fan PWM Duty Cycle failed!"
            exit 255
        fi
    else
        echo "Error: Fan $fan_index doesn't exist"
        exit 1
    fi
}

function getspeed() {
    fan_index=$1
    id=${fan_id_list[${fan_index}]}
    if [ -n "$id" ]
    then
        hwmon_num=${hwmon_list[$id]}
        fan_input_f=fan${input_list_f[$id]}_input
        fan_input_r=fan${input_list_r[$id]}_input
        fan_pwm=pwm${pwm_list[$id]}

        # Get fan speed and pwm from hwmon for front tach
        tach_reading_f=$(cat "${fan_hwmon_path}/${hwmon_num}/${fan_input_f}")
        ret=$?
        if [ $ret != 0 ]; then
            echo "Error: Get Fan Speed failed!"
            exit 1
        fi

        # Get fan speed and pwm from hwmon for rear tach
        tach_reading_r=$(cat "${fan_hwmon_path}/${hwmon_num}/${fan_input_r}")
        ret=$?
        if [ $ret != 0 ]; then
            echo "Error: Get Fan Speed failed!"
            exit 1
        fi

        pwm_reading=$(cat "${fan_hwmon_path}/${hwmon_num}/${fan_pwm}")
        ret=$?
        if [ $ret != 0 ]; then
            echo "Error: Get Fan PWM Duty Cycle failed!"
            exit 1
        fi

        # Scale PWM to 100, adding 127 for rounding.
        pwm_reading_scaled=$(((("$pwm_reading" * 100) + 127) / 255))

        echo "FAN${fan_index}_F, PWM: $pwm_reading, Duty cycle: $pwm_reading_scaled%, Speed(RPM): $tach_reading_f"
        echo "FAN${fan_index}_R, PWM: $pwm_reading, Duty cycle: $pwm_reading_scaled%, Speed(RPM): $tach_reading_r"
    else
        echo "Error: Fan $fan_index doesn't exist"
        exit 1
    fi
}

# Usage of this utility
function usage() {
    echo "Usage:"
    echo "  ampere_fanctrl.sh [getstatus] [setstatus <0:enable|1:disable>] [setspeed all/<fan> <duty>] [getspeed <fan>]"
    echo "  fan: 0-5"
    echo "  duty: 1-100"
}

if [ "$1" == "getstatus" ]; then
    getstatus
elif [ "$1" == "setstatus" ] && [ -n "$2" ]; then
    setstatus "$2"
elif [ "$1" == "setspeed" ]; then
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
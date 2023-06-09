#!/bin/bash

fanSensorService="xyz.openbmc_project.FanSensor"

sensorPath="/xyz/openbmc_project/sensors/fan_tach/"
pwmTargetPath="/xyz/openbmc_project/control/fanpwm/"

sensorValueInterfaceName="xyz.openbmc_project.Sensor.Value"
sensorValuePropertyName="Value"

pwmTargetInterfaceName="xyz.openbmc_project.Control.FanPwm"
pwmTargetPropertyName="Target"

function stop_phosphor_fan_services() {
    systemctl stop phosphor-fan-control@0.service
    systemctl stop phosphor-fan-monitor@0.service
    systemctl stop phosphor-fan-presence-tach@0.service
}

function start_phosphor_fan_services() {
    systemctl start phosphor-fan-control@0.service
    systemctl start phosphor-fan-monitor@0.service
    systemctl start phosphor-fan-presence-tach@0.service
}

function read_speed() {
    fan_val=$(busctl get-property "$fanSensorService" "${sensorPath}$1" "$sensorValueInterfaceName" "$sensorValuePropertyName")
    busctl_error=$?
    if (( busctl_error != 0 )); then
      echo "Error: get-property $sensorValuePropertyName failed! "
      exit 1
    fi

    pwm_target=$(busctl get-property "$fanSensorService" "${pwmTargetPath}$2" "$pwmTargetInterfaceName" "$pwmTargetPropertyName")
    busctl_error=$?
    if (( busctl_error != 0 )); then
      echo "Error: get-property $pwmTargetPropertyName failed! "
      exit 1
    fi

    fan_val=$(echo "$fan_val" | cut -d " " -f 2)
    pwm_target=$(echo "$pwm_target" | cut -d " " -f 2)

    # Convert fan PWM to Duty cycle, adding 127 for rounding.
    pwm_duty_cyle=$(((("$pwm_target" * 100) + 127) / 255))

    echo "$1, PWM: $pwm_target, Duty cycle: $pwm_duty_cyle%, Speed(RPM): $fan_val"
}

function set_pwm() {

    # Convert Fan Duty cycle to PWM, adding 50 for rounding.
    fan_pwm=$(((($2 * 255) + 50) / 100))

    busctl set-property "$fanSensorService" "${pwmTargetPath}$1" "$pwmTargetInterfaceName" "$pwmTargetPropertyName" t "$fan_pwm"
    busctl_error=$?
    if (( busctl_error != 0 )); then
      echo "Error: set-property $pwmTargetPropertyName failed! "
      exit 255
    fi
}

function getstatus() {
    fan_ctl_stt=$(systemctl is-active phosphor-fan-control@0.service | grep inactive)
    fan_monitor_stt=$(systemctl is-active phosphor-fan-monitor@0.service | grep inactive)
    if [[ -z "$fan_ctl_stt" && -z "$fan_monitor_stt" ]]; then
        echo "Thermal Control operational status: Enabled"
        exit 0
    else
        echo "Thermal Control operational status: Disabled"
        exit 1
    fi
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
    case "$1" in
    0) fan_pwm=PWM7
    ;;
    1) fan_pwm=PWM5
    ;;
    2) fan_pwm=PWM4
    ;;
    3) fan_pwm=PWM3
    ;;
    4) fan_pwm=PWM1
    ;;
    5) fan_pwm=PWM0
    ;;
    *) echo "fan $1 doesn't exit"
        exit 1
    ;;
    esac

    set_pwm "$fan_pwm" "$2"
    exit 0
}

function getspeed() {

    # Mapping fan number to fan_input and fan_pwm index
    case "$1" in
    0) fan_input_f=FAN0_F
       fan_input_r=FAN0_R
       fan_pwm=PWM7
    ;;
    1) fan_input_f=FAN1_F
       fan_input_r=FAN1_R
       fan_pwm=PWM5
    ;;
    2) fan_input_f=FAN2_F
       fan_input_r=FAN2_R
       fan_pwm=PWM4
    ;;
    3) fan_input_f=FAN3_F
       fan_input_r=FAN3_R
       fan_pwm=PWM3
    ;;
    4) fan_input_f=FAN4_F
       fan_input_r=FAN4_R
       fan_pwm=PWM1
    ;;
    5) fan_input_f=FAN5_F
       fan_input_r=FAN5_F
       fan_pwm=PWM0
    ;;
    *) echo "fan $1 doesn't exit"
        exit 1
    ;;
    esac

    # Get fan speed, each fan number has two values is front and rear
    read_speed "$fan_input_f" "$fan_pwm"
    read_speed "$fan_input_r" "$fan_pwm"

    exit 0
}

# Usage of this utility
function usage() {
    echo "Usage:"
    echo "  ampere_fanctrl.sh [getstatus] [setstatus <0|1>] [setspeed <fan> <duty>] [getspeed <fan>]"
    echo "  fan: 0-5"
    echo "  duty: 1-100"
}

if [ "$1" == "getstatus" ]; then
    getstatus
elif [ "$1" == "setstatus" ]; then
    setstatus "$2"
elif [ "$1" == "setspeed" ]; then
    stop_phosphor_fan_services
    setspeed "$2" "$3"
elif [ "$1" == "getspeed" ]; then
    getspeed "$2"
else
    usage
fi

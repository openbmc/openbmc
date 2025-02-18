#!/bin/bash

if [ "$1" == "setspeed" ]; then
    if [ "$2" == "all" ] && [ -n "$3" ]; then
        pwm_val=$3
        pwm_scaled=$((((pwm_val * 255) + 50) / 100))
        for filename in /sys/class/hwmon/*/pwm*
        do
            echo $pwm_scaled > "$filename"
        done
    else
        echo "Set speed for each fan is not supported!"
    fi
else
    echo "Only support ampere_fanctrl.sh setspeed all <duty_cycle%100>"
fi


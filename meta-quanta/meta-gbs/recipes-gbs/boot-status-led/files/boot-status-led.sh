#!/bin/bash

BOOT_SERVICE_NAME="xyz.openbmc_project.State.Host"
BOOT_STATUS_OBJPATH="/xyz/openbmc_project/state/os"
BOOT_INTERFACE_NAME="xyz.openbmc_project.State.OperatingSystem.Status"
BOOT_Property="OperatingSystemState"

LED_SERVICE_NAME="xyz.openbmc_project.LED.GroupManager"
LED_INACTIVE_OBJPATH="/xyz/openbmc_project/led/groups/boot_status_inactive"
LED_STANDBY_OBJPATH="/xyz/openbmc_project/led/groups/boot_status_standby"
LED_INTERFACE_NAME="xyz.openbmc_project.Led.Group"
LED_Property="Asserted"

PWR_STATE_SERVICE="xyz.openbmc_project.State.Chassis"
PWR_STATE_OBJPATH="/xyz/openbmc_project/state/chassis0"
PWR_STATE_INTERFACE_NAME="xyz.openbmc_project.State.Chassis"
PWR_STATE_Property="CurrentPowerState"

boot_status=""
power_state=""
led_status=""

mapper wait $LED_INACTIVE_OBJPATH
mapper wait $LED_STANDBY_OBJPATH
while true; do
    power_state="$(busctl get-property $PWR_STATE_SERVICE $PWR_STATE_OBJPATH $PWR_STATE_INTERFACE_NAME $PWR_STATE_Property | awk '{print $2}')"

    boot_status="$(busctl get-property $BOOT_SERVICE_NAME $BOOT_STATUS_OBJPATH $BOOT_INTERFACE_NAME $BOOT_Property | awk '{print $2}')"

    if [[ $power_state != "\"xyz.openbmc_project.State.Chassis.PowerState.On\"" ]];then
        if [[ $led_status != "OFF" ]];then
            busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
            busctl set-property $LED_SERVICE_NAME $LED_STANDBY_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
            led_status="OFF"
        fi
        continue
    else
        if [[ $boot_status != "\"Standby\"" ]] && [[ $led_status != "BLINKING" ]];then
            busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
            led_status="BLINKING"
        elif [[ $boot_status == "\"Standby\"" ]] && [[ $led_status != "ON" ]];then
            busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
            busctl set-property $LED_SERVICE_NAME $LED_STANDBY_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
            led_status="ON"
        fi
    fi
    sleep 1
done

exit 0

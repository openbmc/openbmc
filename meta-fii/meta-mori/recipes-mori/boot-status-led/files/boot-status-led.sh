#!/bin/bash

BOOT_SERVICE_NAME="xyz.openbmc_project.State.Host"
BOOT_STATUS_OBJPATH="/xyz/openbmc_project/state/host0"
BOOT_INTERFACE_NAME="xyz.openbmc_project.State.OperatingSystem.Status"
BOOT_Property="OperatingSystemState"

LED_SERVICE_NAME="xyz.openbmc_project.LED.GroupManager"
LED_INACTIVE_OBJPATH="/xyz/openbmc_project/led/groups/boot_status_inactive"
LED_STANDBY_OBJPATH="/xyz/openbmc_project/led/groups/boot_status_standby"
LED_INTERFACE_NAME="xyz.openbmc_project.Led.Group"
LED_Property="Asserted"

boot_status=""
led_status=""

while true; do
    boot_status="$(busctl get-property $BOOT_SERVICE_NAME $BOOT_STATUS_OBJPATH $BOOT_INTERFACE_NAME $BOOT_Property | awk '{print $2}')"

    if [[ $boot_status != "\"xyz.openbmc_project.State.OperatingSystem.Status.OSStatus.Standby\"" ]] && [[ $led_status != "BLINKING" ]];then
        busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
        busctl set-property $LED_SERVICE_NAME $LED_STANDBY_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
        led_status="BLINKING"
    elif [[ $boot_status == "\"xyz.openbmc_project.State.OperatingSystem.Status.OSStatus.Standby\"" ]] && [[ $led_status != "ON" ]];then
        busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
        busctl set-property $LED_SERVICE_NAME $LED_STANDBY_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
        led_status="ON"
    fi

    sleep 10

done

exit 0

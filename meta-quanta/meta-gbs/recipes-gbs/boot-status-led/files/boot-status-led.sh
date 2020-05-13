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

dbus_state=0
boot_status=""
check_flag=0

mapper wait $LED_INACTIVE_OBJPATH
mapper wait $LED_STANDBY_OBJPATH
while true; do
    boot_status=$(busctl get-property $BOOT_SERVICE_NAME $BOOT_STATUS_OBJPATH $BOOT_INTERFACE_NAME $BOOT_Property | awk '{print $2}')

    if [ $boot_status != "\"Standby\"" ] && [ $check_flag -eq 0 ];then
        busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
        check_flag=1
    elif [ $boot_status == "\"Standby\"" ];then
        busctl set-property $LED_SERVICE_NAME $LED_INACTIVE_OBJPATH $LED_INTERFACE_NAME $LED_Property b false
        busctl set-property $LED_SERVICE_NAME $LED_STANDBY_OBJPATH $LED_INTERFACE_NAME $LED_Property b true
        break
    fi
    sleep 1
done

exit 0

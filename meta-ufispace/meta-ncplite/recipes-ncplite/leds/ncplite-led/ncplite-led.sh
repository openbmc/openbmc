#!/bin/bash -e

HOST_SERVICE="xyz.openbmc_project.State.HostCondition.Gpio0"
HOST_OBJPATH="/xyz/openbmc_project/Gpios/host0"
HOST_INTERFACE="xyz.openbmc_project.Condition.HostFirmware"
HOST_PROPERTY="CurrentFirmwareCondition"

FAN_SERVICE="xyz.openbmc_project.State.FanCtrl"
FAN_OBJPATH="/xyz/openbmc_project/settings/fanctrl/zone1"
FAN_INTERFACE="xyz.openbmc_project.Control.Mode"
FAN_PROPERTY="FailSafe"

PSU_SERVICE="xyz.openbmc_project.Inventory.Manager"
PSU0_PRESENT_OBJPATH="/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU0_PRSNT_L"
PSU1_PRESENT_OBJPATH="/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU1_PRSNT_L"
PSU0_POWER_OBJPATH="/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU0_POWER_OK"
PSU1_POWER_OBJPATH="/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU1_POWER_OK"
PSU_INTERFACE="xyz.openbmc_project.Inventory.Item"
PSU_PROPERTY="Present"

CPLD_LED_offset1=0x80
CPLD_LED_offset2=0x81
CPLD_SYS_FAN_reg=0
CPLD_PSU0_PSU1_reg=0

while true; do
    #System status LED
    system_status=$(busctl get-property ${HOST_SERVICE} ${HOST_OBJPATH} ${HOST_INTERFACE} ${HOST_PROPERTY} | awk '{print $2}' | tr -d "\"" | awk -F . '{print $NF}')

    if [ "${system_status}" == "Running" ]; then
        #Solid Green
        CPLD_SYS_reg=0x90
    else
        #Solid Yellow
        CPLD_SYS_reg=0x80
    fi

    #Fan status LED
    fan_status=$(busctl get-property  ${FAN_SERVICE} ${FAN_OBJPATH} ${FAN_INTERFACE} ${FAN_PROPERTY} | awk '{print $2}')

    if [ "${fan_status}" == "true" ]; then
        #Blink Yellow
        CPLD_FAN_reg=0xc
    else
        #Solid Green
        CPLD_FAN_reg=0x9
    fi

    CPLD_SYS_FAN_reg=$((CPLD_SYS_reg | CPLD_FAN_reg))

    #PSU0 status LED
    psu0_prsnt_status=$(busctl get-property  ${PSU_SERVICE} ${PSU0_PRESENT_OBJPATH} ${PSU_INTERFACE} ${PSU_PROPERTY} | awk '{print $2}')

    psu0_power_status=$(busctl get-property  ${PSU_SERVICE} ${PSU0_POWER_OBJPATH} ${PSU_INTERFACE} ${PSU_PROPERTY} | awk '{print $2}')

    if [ "${psu0_prsnt_status}" == "true" ]; then
        if [ "${psu0_power_status}" == "true" ]; then
            #Solid Green
            CPLD_PSU0_reg=0x9
        else
            #Blink Yellow
            CPLD_PSU0_reg=0xc
        fi
    else
        CPLD_PSU0_reg=0x0
    fi

    #PSU1 status LED
    psu1_prsnt_status=$(busctl get-property  ${PSU_SERVICE} ${PSU1_PRESENT_OBJPATH} ${PSU_INTERFACE} ${PSU_PROPERTY} | awk '{print $2}')

    psu1_power_status=$(busctl get-property  ${PSU_SERVICE} ${PSU1_POWER_OBJPATH} ${PSU_INTERFACE} ${PSU_PROPERTY} | awk '{print $2}')

    if [ "${psu1_prsnt_status}" == "true" ]; then
        if [ "${psu1_power_status}" == "true" ]; then
            #Solid Green
            CPLD_PSU1_reg=0x90
        else
            #Blink Yellow
            CPLD_PSU1_reg=0xc0
        fi
    else
        CPLD_PSU1_reg=0x0
    fi

    CPLD_PSU0_PSU1_reg=$((CPLD_PSU0_reg | CPLD_PSU1_reg))

    i2cset -f -y 2 0x40 "${CPLD_LED_offset1}" "${CPLD_SYS_FAN_reg}" > /dev/null 2>&1
    i2cset -f -y 2 0x40 "${CPLD_LED_offset2}" "${CPLD_PSU0_PSU1_reg}" > /dev/null 2>&1

    sleep 2
done

exit 0

#!/bin/bash

SERVICE_NAME="xyz.openbmc_project.Inventory.Manager"
INVENTORY_OBJPATH=( \
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/ALL_PWR_GOOD_H"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/FAN_STATUS_INT_L"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/THERMAL_ALERT_L"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/CPU_CATERR_L"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/CPU_THERMTEIP_L"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU0_INT_L"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU1_INT_L"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU0_POWER_OK"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU1_POWER_OK"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU0_PRSNT_L"
    "/xyz/openbmc_project/inventory/system/chassis/motherboard/PSU1_PRSNT_L"
)
INTERFACE_NAME="xyz.openbmc_project.Inventory.Item"

IPMI_LOG_SERVICE="xyz.openbmc_project.Logging.IPMI"
IPMI_LOG_OBJPATH="/xyz/openbmc_project/Logging/IPMI"
IPMI_LOG_INTERFACE="xyz.openbmc_project.Logging.IPMI"
IPMI_LOG_FUNCT="IpmiSelAdd"
IPMI_LOG_PARA_FORMAT="ssaybq" #5 parameters, s : string, s : string, ay : byte array, b : boolean, q : UINT16

LOG_ERR="Configuration Error(Incorrect_interconnection)"
LOG_EVENT_DATA="3 0x01 0xff 0xff"
LOG_ASSERT_FLAG="true"
LOG_DEASSERT_FLAG="false"
LOG_GENID_FLAG="0x0020"

initial_state=("false" "false" "false" "false" "false" "false" "false" "false" "false" "false" "false" "false")

for i in "${!INVENTORY_OBJPATH[@]}"
do
    mapper wait "${INVENTORY_OBJPATH[$i]}"
done

while true; do
    for i in "${!INVENTORY_OBJPATH[@]}"
    do
        current_status="$(busctl get-property $SERVICE_NAME "${INVENTORY_OBJPATH[$i]}" $INTERFACE_NAME Present | awk '{print $2}')"

        if [ "$current_status" == "true" ] && [ "${initial_state[$i]}" == "false" ];then
            initial_state[i]="true"
            busctl call $IPMI_LOG_SERVICE $IPMI_LOG_OBJPATH $IPMI_LOG_INTERFACE $IPMI_LOG_FUNCT $IPMI_LOG_PARA_FORMAT "$LOG_ERR" "${INVENTORY_OBJPATH[$i]}" "$LOG_EVENT_DATA" $LOG_ASSERT_FLAG $LOG_GENID_FLAG
        elif [ "$current_status" == "false" ] && [ "${initial_state[$i]}" == "true" ]; then
            initial_state[i]="false"
            busctl call $IPMI_LOG_SERVICE $IPMI_LOG_OBJPATH $IPMI_LOG_INTERFACE $IPMI_LOG_FUNCT $IPMI_LOG_PARA_FORMAT "$LOG_ERR" "${INVENTORY_OBJPATH[$i]}" "$LOG_EVENT_DATA" $LOG_DEASSERT_FLAG $LOG_GENID_FLAG
        fi
    done
    usleep 100000
done

exit 0

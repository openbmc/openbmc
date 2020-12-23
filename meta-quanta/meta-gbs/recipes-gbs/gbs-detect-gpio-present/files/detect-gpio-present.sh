#!/bin/bash

SERVICE_NAME="xyz.openbmc_project.Inventory.Manager"
PRESENT_OBJPATH=("/xyz/openbmc_project/inventory/system/chassis/cable/ss_cab0_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/cable/ss_cab1_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/cable/ss_cab2_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/cable/ss_cab3_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/cable/hsbp_cab_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/cable/fanbd_cab_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/cable/bp12v_cab_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/entity/sata0_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/entity/pe_slot0_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/entity/pe_slot1_prsnt")
INTERFACE_NAME="xyz.openbmc_project.Inventory.Item"

IPMI_LOG_SERVICE="xyz.openbmc_project.Logging.IPMI"
IPMI_LOG_OBJPATH="/xyz/openbmc_project/Logging/IPMI"
IPMI_LOG_INTERFACE="xyz.openbmc_project.Logging.IPMI"
IPMI_LOG_FUNCT="IpmiSelAdd"
IPMI_LOG_PARA_FORMAT="ssaybq" #5 parameters, s : string, s : string, ay : byte array, b : boolean, y : UINT16
LOG_ERR="Configuration Error(Incorrect_interconnection)"
LOG_EVENT_DATA="3 0x01 0xff 0xfe"
LOG_ASSERT_FLAG="true"
LOG_DEASSERT_FLAG="false"
LOG_GENID_FLAG="0x0020"
present_state=("true" "true" "true" "true" "true" "true" "true" "true" "true" "true")

while true; do
    for i in ${!PRESENT_OBJPATH[@]}
    do
        mapper wait ${PRESENT_OBJPATH[$i]}
        boot_status="$(busctl get-property $SERVICE_NAME ${PRESENT_OBJPATH[$i]} $INTERFACE_NAME Present | awk '{print $2}')"

        if [ $boot_status == "false" ] && [ ${present_state[$i]} == "true" ];then
            echo "Update cable $(($i+1)) state."
            present_state[$i]="false"
            busctl call $IPMI_LOG_SERVICE $IPMI_LOG_OBJPATH $IPMI_LOG_INTERFACE $IPMI_LOG_FUNCT $IPMI_LOG_PARA_FORMAT "$LOG_ERR" ${PRESENT_OBJPATH[$i]} $LOG_EVENT_DATA $LOG_ASSERT_FLAG $LOG_GENID_FLAG
        elif [ $boot_status == "true" ] && [ ${present_state[$i]} == "false" ];then
            echo "Update cable $(($i+1)) state."
            present_state[$i]="true"
            busctl call $IPMI_LOG_SERVICE $IPMI_LOG_OBJPATH $IPMI_LOG_INTERFACE $IPMI_LOG_FUNCT $IPMI_LOG_PARA_FORMAT "$LOG_ERR" ${PRESENT_OBJPATH[$i]} $LOG_EVENT_DATA $LOG_DEASSERT_FLAG $LOG_GENID_FLAG
        fi
    done
    sleep 1
done

exit 0

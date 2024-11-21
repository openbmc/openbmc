#!/bin/bash

set -e

# Check if PCH is on standby to proceed with the
# host firmware update
#
# Find the GPIO pin associated with "pch-ready"
# and read the value
PCH_READY_GPIO_PIN=$(gpiofind "pch-ready")

if [ -z "${PCH_READY_GPIO_PIN}" ]; then
    echo "gpio 'pch-ready' not found in device tree. Exiting."
    exit 0
fi

read -r PCH_READY_GPIO_CHIP PCH_READY_GPIO_LINE <<< "$PCH_READY_GPIO_PIN"
GPIO_VALUE=$(gpioget "$PCH_READY_GPIO_CHIP" "$PCH_READY_GPIO_LINE")

if [ "${GPIO_VALUE}" != "0" ]; then
    echo "PCH is not on standby. Exiting host firmware version read."
    exit 0
fi

IMAGE_FILE=$(find "$1" -name "*.rom")

IPMB_OBJ="xyz.openbmc_project.Ipmi.Channel.Ipmb"
IPMB_PATH="/xyz/openbmc_project/Ipmi/Channel/Ipmb"
IPMB_INTF="org.openbmc.Ipmb"

# me address, 0x2e oen, 0x00 - lun, 0xdf - force recovery
ME_CMD_RECOVER=(1 0x2e 0 0xdf 4 0x57 0x01 0x00 0x01)
# me address, 0x6 App Fn, 0x00 - lun, 0x2 - cold reset
ME_CMD_RESET=(1 6 0 0x2 0)
# me address, 0x6 App Fn, 0x00 - lun, 0x1 - get device id
ME_GET_DEVICE_ID=(1 6 0 0x1 0)

echo "Bios firmware upgrade started at $(date)"

me_wait_poweron() {
    echo "Wait for ME firmware to start"
    for _ in {1..10}; do
        busctl call --timeout=1 "$IPMB_OBJ" "$IPMB_PATH" "$IPMB_INTF" sendRequest yyyyay "${ME_GET_DEVICE_ID[@]}" 2>/dev/null
        exit_code=$?

        if [ "$exit_code" -eq 0 ]; then
            break
        fi

        sleep 1
    done

    if [ "$exit_code" -ne 0 ]; then
        echo "Failed to communicate with SPS firmware after 10 attempts."
        exit 1
    fi
}

me_force_recovery_mode() {
    # Set ME to recovery mode
    echo "Set ME to recovery mode"
    busctl call "$IPMB_OBJ" "$IPMB_PATH" "$IPMB_INTF" sendRequest yyyyay "${ME_CMD_RECOVER[@]}"
}

me_reset() {
    #Reset ME to boot from new bios
    echo "Reset ME to boot from new firmware"
    busctl call "$IPMB_OBJ" "$IPMB_PATH" "$IPMB_INTF" sendRequest yyyyay "${ME_CMD_RESET[@]}"
}

configure_flash_env() {
    # 1. Assert PCH RESET
    # 2. Enable/Disable FM_FLASH_SEC_OVRD
    # 3. De-assert PCH RESET
    local action="$1"
    if [ "$action" == "enable" ]; then
        echo "Asserting PCH RESET and enabling flash write override"
        gpioset "${PCH_RESET_GPIO_CHIP}" "${PCH_RESET_GPIO_LINE}=0"
        gpioset "${FLASH_OVERRIDE_GPIO_CHIP}" "${FLASH_OVERRIDE_GPIO_LINE}=1"
        gpioset "${PCH_RESET_GPIO_CHIP}" "${PCH_RESET_GPIO_LINE}=1"
        sleep 2
    elif [ "$action" == "disable" ]; then
        echo "Disabling flash write override and resetting PCH RESET"
        gpioset "${PCH_RESET_GPIO_CHIP}" "${PCH_RESET_GPIO_LINE}=0"
        gpioset "${FLASH_OVERRIDE_GPIO_CHIP}" "${FLASH_OVERRIDE_GPIO_LINE}=0"
        gpioset "${PCH_RESET_GPIO_CHIP}" "${PCH_RESET_GPIO_LINE}=1"
    else
        echo "Invalid action specified for configure_flash_env. Use 'enable' or 'disable'."
        exit 1
    fi
}

cleanup_env() {
    # Disable flash protection and clean up
    configure_flash_env "disable"
}

reset_and_cleanup_env() {
    echo "Reset ME and disable flash-write-override GPIO"
    me_reset
    sleep 5
    cleanup_env
}


PCH_RESET_GPIO_PIN=$(gpiofind "pch-reset")
if [ -z "${PCH_RESET_GPIO_PIN}" ]; then
    echo "gpio 'pch-ready' not found in device tree. Exiting."
    exit 0
fi
read -r PCH_RESET_GPIO_CHIP PCH_RESET_GPIO_LINE <<< "$PCH_RESET_GPIO_PIN"

FLASH_OVERRIDE_GPIO_PIN="$(gpiofind flash-write-override)"
if [ -z "${FLASH_OVERRIDE_GPIO_PIN}" ]; then
    echo "gpio 'flash-write-override' not found in device tree. Exiting."
    exit 0
fi
read -r FLASH_OVERRIDE_GPIO_CHIP FLASH_OVERRIDE_GPIO_LINE <<< "$FLASH_OVERRIDE_GPIO_PIN"

configure_flash_env "enable"
sleep 2

me_wait_poweron

me_force_recovery_mode

# Fetch the MTD device number for the specified espi flash device
DEVICE_NAME="espi-flash-mafs"
MTD_DEVICE_NUMBER=$(grep "$DEVICE_NAME" /proc/mtd | awk -F: '{print $1}' | awk -F'mtd' '{print $2}')

if [ -n "$MTD_DEVICE_NUMBER" ]; then
    echo "Found MTD device number: $MTD_DEVICE_NUMBER"
else
    echo "Error: MTD device with name '$DEVICE_NAME' not found!"
    reset_and_cleanup_env
    exit 1
fi

#Flashcp image to device.
if [ -e "$IMAGE_FILE" ];
then
    echo "Bios image is $IMAGE_FILE"
    flashrom -p linux_mtd:dev="${MTD_DEVICE_NUMBER}" -w "$IMAGE_FILE"
else
    echo "Bios image $IMAGE_FILE doesn't exist"
fi

reset_and_cleanup_env

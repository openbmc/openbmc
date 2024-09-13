#!/bin/bash

set -e

# Check if PCH is on standby to proceed with the
# host firmware update
#
# Find the GPIO pin associated with "pch-ready"
GPIO_PIN=$(gpiofind "pch-ready")

if [ -z "${GPIO_PIN}" ]; then
    echo "gpio 'pch-ready' not found in device tree. Exiting."
    exit 0
fi

# Read the value of the GPIO pin
GPIO_CHIP=$(echo "$GPIO_PIN" | cut -d' ' -f1) # Extract gpiochip
GPIO_LINE=$(echo "$GPIO_PIN" | cut -d' ' -f2) # Extract line offset
GPIO_VALUE=$(gpioget "$GPIO_CHIP" "$GPIO_LINE")

if [ "${GPIO_VALUE}" != "0" ]; then
    echo "PCH is not on standby. Exiting host firmware version read."
    exit 0
fi

IMAGE_FILE=$(find "$1" -name "*.FD")

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

reset_and_cleanup_env() {
    echo "Reset ME and disable GPIO"
    me_reset
    sleep 5
    # Disable flash-write-override
    gpioset "${gpiochip}" "${gpio_line}=0"
}

# Enable flash-write-override
gpio_info="$(gpiofind flash-write-override)"
read -r gpiochip gpio_line <<< "$gpio_info"
gpioset "${gpiochip}" "${gpio_line}=1"

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

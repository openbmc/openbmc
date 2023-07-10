#!/bin/bash

set -e

IMAGE_FILE=$(find "$1" -name "*.FD")

IPMB_OBJ="xyz.openbmc_project.Ipmi.Channel.Ipmb"
IPMB_PATH="/xyz/openbmc_project/Ipmi/Channel/Ipmb"
IPMB_INTF="org.openbmc.Ipmb"


# me address, 0x2e oen, 0x00 - lun, 0xdf - force recovery
ME_CMD_RECOVER="1 0x2e 0 0xdf 4 0x57 0x01 0x00 0x01"
# me address, 0x6 App Fn, 0x00 - lun, 0x2 - cold reset
ME_CMD_RESET="1 6 0 0x2 0"
# me address, 0x6 App Fn, 0x00 - lun, 0x1 - get device id
ME_GET_DEVICE_ID="1 6 0 0x1 0"

echo "Bios upgrade started at $(date)"

power_status() {
    st=$(busctl get-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis CurrentPowerState | cut -d"." -f6)
    if [ "$st" == "On\"" ]; then
        echo "on"
    else
        echo "off"
    fi
}

power_off() {
    echo "Shutting down Server"
    busctl set-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis RequestedPowerTransition s xyz.openbmc_project.State.Chassis.Transition.Off
    # shellcheck disable=SC2034
    for i in {1..10}; do
      if [ "$(power_status)" == "off" ]; then
        break
      else
        sleep 1
      fi
    done

    if [ "$(power_status)" != "off" ]; then
      echo "Failed to power off Server"
      exit 1
    fi
}

power_on() {
    echo "Powering on Server"
    busctl set-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis RequestedPowerTransition s xyz.openbmc_project.State.Chassis.Transition.On
    # shellcheck disable=SC2034
    for i in {1..10}; do
      if [ "$(power_status)" == "on" ]; then
        break
      else
        sleep 1
      fi
    done

    if [ "$(power_status)" != "on" ]; then
      echo "Failed to power on Server"
      exit 1
    fi
}

me_wait_poweron() {
    echo "Wait for ME firmware to start"
    # shellcheck disable=SC2034
    for i in {1..10}; do
      # shellcheck disable=SC2086
      if ! busctl call --timeout=1 "$IPMB_OBJ" "$IPMB_PATH" "$IPMB_INTF" sendRequest yyyyay $ME_GET_DEVICE_ID 2>/dev/null; then
        sleep 1
      else
        break
      fi
    done
    # shellcheck disable=SC2086
    if ! busctl call --timeout=1 "$IPMB_OBJ" "$IPMB_PATH" "$IPMB_INTF" sendRequest yyyyay $ME_GET_DEVICE_ID 2>/dev/null; then
      echo "Failed to communicate with SPS firmware"
      exit 1
    fi
}

me_force_recovery_mode() {
    # Set ME to recovery mode
    echo "Set ME to recovery mode"
    # shellcheck disable=SC2086
    busctl call "$IPMB_OBJ" "$IPMB_PATH" "$IPMB_INTF" sendRequest yyyyay $ME_CMD_RECOVER
}

me_reset() {
    #Reset ME to boot from new bios
    echo "Reset ME to boot from new firmware"
    # shellcheck disable=SC2086
    busctl call "$IPMB_OBJ" "$IPMB_PATH" "$IPMB_INTF" sendRequest yyyyay $ME_CMD_RESET
}

# Power off PCH
power_off

# Enable FM_FLASH_SEC_OVRD
# shellcheck disable=SC2046 # Intended splitting of OPTIONS
gpioset $(gpiofind FM_FLASH_SEC_OVRD)=1

# Power on PCH
power_on

me_wait_poweron

me_force_recovery_mode

#Flashcp image to device.
if [ -e "$IMAGE_FILE" ];
then
    echo "Bios image is $IMAGE_FILE"
    flashrom -p linux_mtd:dev=12 -w "$IMAGE_FILE"
else
    echo "Bios image $IMAGE_FILE doesn't exist"
fi

# Reset ME
me_reset

sleep 5

# Power off PCH
power_off

# Disable FM_FLASH_SEC_OVRD
# shellcheck disable=SC2046 # Intended splitting of OPTIONS
gpioset $(gpiofind FM_FLASH_SEC_OVRD)=0

# Delete cached bios version file if it exist.
rm /var/cache/bios_version || true

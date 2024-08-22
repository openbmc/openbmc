#!/bin/bash
set -e

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

# Fetch the MTD device number for the specified espi flash device
DEVICE_NAME="espi-flash-mafs"
MTD_DEVICE_NUMBER=$(grep "$DEVICE_NAME" /proc/mtd | awk -F: '{print $1}' | awk -F'mtd' '{print $2}')

if [ -n "$MTD_DEVICE_NUMBER" ]; then
    echo "Found MTD device number: $MTD_DEVICE_NUMBER"
else
    echo "Error: MTD device with name '$DEVICE_NAME' not found!"
    exit 0
fi

BIOS_FILE="$(mktemp)"
flashrom -p linux_mtd:dev="${MTD_DEVICE_NUMBER}" --ifd -i bios -r "${BIOS_FILE}"

bios_version=$(strings "${BIOS_FILE}" | grep COREBOOT_EXTR | head -n 1 | awk '{ print $3}' | sed 's/"//g' | sed 's/^-\(.*\)/\1/')

if [ "${bios_version}" == "" ] ; then
  bios_version=$(strings "${BIOS_FILE}" | grep COREBOOT_VERS | head -n 1 | awk '{ print $3}' | sed 's/"//g' | sed 's/^-\(.*\)/\1/')
fi

# Clean up the temporary BIOS file
rm "${BIOS_FILE}"

# If BIOS version is found, cache it and update the BMC property
if [ "${bios_version}" != "" ] ; then
  if [ -f /var/cache/bios_version ] ; then
    rm /var/cache/bios_version
  fi

  echo "coreboot-${bios_version}" > /var/cache/bios_version
fi

if [ -f /var/cache/bios_version ] ; then
  busctl set-property xyz.openbmc_project.Software.BMC.Updater \
      /xyz/openbmc_project/software/bios_active \
      xyz.openbmc_project.Software.Version Version s "$(cat /var/cache/bios_version)"
fi

#!/bin/bash

usage () {
	echo "Usage:"
	echo "      $(basename "$0") <image path> "
	echo "Where:"
	echo "	<image path>: the path link to folder, which include image file and MANIFEST"
	echo "Example:"
	echo "     $(basename "$0") /tmp/images/ghdh1393"
}


IMG_PATH="$1"
if [ ! -d "$IMG_PATH" ]; then
	echo "The folder $IMG_PATH does not exist"
	usage
	exit 1
fi

MANIFEST_PATH="${IMG_PATH}/MANIFEST"
if [ ! -f "$MANIFEST_PATH" ]; then
	echo "The MANIFEST file $MANIFEST_PATH does not exist"
	usage
	exit 1
fi

EXTENDED_VERSION=$(awk '/ExtendedVersion/ {print}' "${MANIFEST_PATH}" | cut -d "=" -f 2)

# If the ExtendedVersion is empty, set default to update UEFI/EDKII on primary device
if [ -z "$EXTENDED_VERSION" ]
then
	EXTENDED_VERSION="primary"
fi

# Assign the command based on the ExtendedVersion
case ${EXTENDED_VERSION} in
	"primary")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.img" -o -name "*.bin" -o -name "*.rom" \))
		CMD="/usr/sbin/ampere_flash_bios.sh $IMAGE 1"
		;;

	"secondary")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.img" -o -name "*.bin" -o -name "*.rom" \))
		CMD="/usr/sbin/ampere_flash_bios.sh $IMAGE 2"
		;;

	"eeprom" | "eeprom-primary" | "scp-primary")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.img" -o -name "*.slim" -o -name "*.rom" -o -name "*.bin" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh eeprom $IMAGE 1"
		;;

	"eeprom-secondary" | "scp-secondary")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.img" -o -name "*.slim" -o -name "*.rom" -o -name "*.bin" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh eeprom $IMAGE 2"
		;;

	"fru" | "mbfru")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.bin" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh fru $IMAGE 1"
		;;


	"bmcfru")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.bin" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh fru $IMAGE 2"
		;;

	"mbcpld")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.jed" -o -name "*.bin" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh mb_cpld $IMAGE"
		;;
	"bmccpld")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.jed" -o -name "*.bin" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh bmc_cpld $IMAGE"
		;;
	"bpcpld"*)
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.jed" -o -name "*.bin" \))
		TARGET="${EXTENDED_VERSION:6}"
		CMD="/usr/sbin/ampere_firmware_upgrade.sh bp_cpld $IMAGE $TARGET"
		;;
	*)
		echo "Invalid ExtendedVersion: ${EXTENDED_VERSION}. Please check MANIFEST file!"
		exit 1
		;;
esac


if [ -z "$IMAGE" ]
then
	echo "ERROR: The image file: No such file or directory"
	exit 1
fi

if ! eval "$CMD";
then
	echo "ERROR: The firmware update not successful"
	exit 1
fi

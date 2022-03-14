#!/bin/bash
#
# Copyright (c) 2021 Ampere Computing LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script updates the EDKII / SCP firmware.
# Author : Chanh Nguyen (chnguyen@amperecomputing.com)
# Date : Sep 7, 2021
# Modified:

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

	"scp-primary")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.img" -o -name "*.slim" -o -name "*.rom" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh smpmpro $IMAGE 1"
		;;

	"scp-secondary")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.img" -o -name "*.slim" -o -name "*.rom" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh smpmpro $IMAGE 2"
		;;

	"fru")
		IMAGE=$(find "${IMG_PATH}" -type f \( -name "*.bin" \))
		CMD="/usr/sbin/ampere_firmware_upgrade.sh fru $IMAGE"
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
	echo "ERROR: The firmware update not successfull"
	exit 1
fi

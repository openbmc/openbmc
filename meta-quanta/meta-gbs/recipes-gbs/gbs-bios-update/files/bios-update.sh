#!/bin/sh
# Copyright 2020 Google LLC
# Copyright 2020 Quanta Computer Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Global variables

# GPIO to control the host SPI mux
SPI_SW_SELECT=169

# Kernel control string for bind/unbind
KERNEL_FIU_ID="c0000000.fiu"

# Kernel sysfs path for bind/unbind
KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"

IMAGE_FILE="/tmp/image-bios"

# Taken from /run/initramfs/update
# Given label name, return mtd node. e.g. `findmtd bmc` returns 'mtd0'
findmtd() {
    m=$(grep -xl "$1" /sys/class/mtd/*/name)
    m=${m%/name}
    m=${m##*/}
    echo $m
}

cleanup() {
    if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
        echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
    fi
    echo low > /sys/class/gpio/gpio${SPI_SW_SELECT}/direction  # Switch mux to host
    rm -f ${IMAGE_FILE}
}
trap cleanup EXIT SIGHUP SIGINT SIGTERM

main() {
    if [ ! -f ${IMAGE_FILE} ]; then
        echo "Invalid bios image file!"
        exit 1
    fi

    echo "Starting bios update..."
    if [ ! -d "/sys/class/gpio/gpio${SPI_SW_SELECT}" ]; then
        echo "${SPI_SW_SELECT}" > /sys/class/gpio/export
    fi

    echo high > /sys/class/gpio/gpio${SPI_SW_SELECT}/direction  # Switch mux to BMC

    if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
        echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
    fi
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/bind

    # BIOS flash is labelled 'pnor'
    pnor_mtd=$(findmtd pnor)
    if [ -z "${pnor_mtd}" ]; then
        echo "Cannot find bios flash mtd partition!"
        exit 1
    fi

    flashcp -v $IMAGE_FILE /dev/"${pnor_mtd}"
    if [ $? -eq 0 ]; then
        echo "bios update successfully..."
    else
        echo "bios update failed..."
        exit 1
    fi
}
# Exit without running main() if sourced
return 0 2>/dev/null

main "$@"

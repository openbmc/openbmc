# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

KBUILD_DEFCONFIG:raspberrypi-armv7 = "bcm2709_defconfig"
KERNEL_PACKAGE_NAME = "${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}"
PROVIDES:remove = "virtual/kernel"

KERNEL_IMAGETYPE_DIRECT ?= "zImage"

COMPATIBLE_MACHINE = "^raspberrypi-armv7$"

KERNEL_DEVICETREE = ""

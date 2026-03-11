# SPDX-License-Identifier: Apache-2.0
#
# Copyright (c) 2020 Arm Limited
#
SUMMARY = "Total Compute Images"
DESCRIPTION = "Build all the images required for Total Compute platform"
LICENSE = "Apache-2.0"

COMPATIBLE_MACHINE = "(tc?)"

inherit nopackages

# The last image to be built is trusted-firmware-a
DEPENDS += " trusted-firmware-a"

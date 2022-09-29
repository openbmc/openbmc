# SPDX-License-Identifier: Apache-2.0
#
# Copyright (c) 2021 Arm Limited
#

DESCRIPTION = "Linux Android Common Kernel"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

require recipes-kernel/linux/linux-yocto.inc

COMPATIBLE_MACHINE ?= "invalid"

ARCH = "arm64"

S = "${WORKDIR}/git"

LINUX_VERSION ?= "${PV}"
KERNEL_VERSION_SANITY_SKIP = "1"
KBRANCH = ""

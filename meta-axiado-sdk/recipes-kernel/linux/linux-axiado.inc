# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

DESCRIPTION = "Linux kernel for Axiado"
SECTION = "kernel"
LICENSE = "CLOSED"

LIC_FILES_CHKSUM ?= "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PROVIDES += "virtual/kernel"

KCONFIG_MODE = "--alldefconfig"

inherit kernel_axiado
require recipes-kernel/linux/linux-yocto.inc

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
KSRC ?= "git://git@sourcevault.axiadord:7999/linux/kernel.git;protocol=ssh;branch=${SRCBRANCH}"
SRC_URI += "${KSRC}"

LINUX_VERSION ?= "6.6.16"
PV = "${LINUX_VERSION}+git${SRCPV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

ERROR_QA:remove = "buildpaths"
WARN_QA:append = "buildpaths"

# Conditional assignments for different SOC revisions
KBUILD_DEFCONFIG_revA = "axiado_defconfig"
KBUILD_DEFCONFIG_revB = "axiado_b0_defconfig"

# Dynamically set KBUILD_DEFCONFIG based on SOC_REVISION
KBUILD_DEFCONFIG ?= "${KBUILD_DEFCONFIG_${SOC_REVISION}}"

# Fallback to default if SOC_REVISION is not set or doesn't match
KBUILD_DEFCONFIG ??= "axiado_defconfig"

# Linux don't support zImage for ARM64 platform
KERNEL_IMAGETYPE:remove = "zImage"

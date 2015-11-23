DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KBRANCH ?= "dev-4.3"
KCONFIG_MODE="--alldefconfig"

SRC_URI = "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"

LINUX_VERSION ?= "4.3"
LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

SRCREV="openbmc-20151123-1"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "openbmc"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

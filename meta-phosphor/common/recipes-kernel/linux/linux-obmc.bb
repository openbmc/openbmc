DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KBRANCH ?= "dev"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

SRC_URI = "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI += "file://obmc-bsp.scc \
            file://obmc-bsp.cfg \
            file://obmc-bsp-user-config.cfg \
            file://obmc-bsp-user-patches.scc \
           "

LINUX_VERSION ?= "4.3-rc1"
LINUX_VERSION_EXTENSION ?= "-openbmc"

SRCREV="${AUTOREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "openbmc"

S = "${WORKDIR}/git"

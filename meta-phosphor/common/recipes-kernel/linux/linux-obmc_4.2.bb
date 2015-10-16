DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KBRANCH ?= "dev"

SRC_URI = "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI += "file://obmc-bsp.scc \
            file://obmc-bsp.cfg \
            file://obmc-bsp-user-config.cfg \
            file://obmc-bsp-user-patches.scc \
           "

LINUX_VERSION ?= "4.2"
LINUX_VERSION_EXTENSION ?= "-openbmc-${SRCPV}"

SRCREV="${AUTOREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "openbmc"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

do_configure_prepend() {
        sed -i 's/CONFIG_LOCALVERSION=.\+/CONFIG_LOCALVERSION=${LINUX_VERSION_EXTENSION}/' ${S}/arch/${ARCH}/configs/${KMACHINE}_defconfig
}

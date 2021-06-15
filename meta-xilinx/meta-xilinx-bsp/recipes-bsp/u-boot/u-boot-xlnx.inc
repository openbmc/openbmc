require recipes-bsp/u-boot/u-boot.inc

DEPENDS += "bc-native dtc-native bison-native"

XILINX_RELEASE_VERSION ?= ""
UBOOT_VERSION_EXTENSION ?= "-xilinx-${XILINX_RELEASE_VERSION}"
PV = "${UBOOT_VERSION}${UBOOT_VERSION_EXTENSION}+git${SRCPV}"

UBOOTURI ?= "git://github.com/Xilinx/u-boot-xlnx.git;protocol=https"
UBRANCH ?= ""
UBRANCHARG = "${@['nobranch=1', 'branch=${UBRANCH}'][d.getVar('UBRANCH', True) != '']}"

SRC_URI = "${UBOOTURI};${UBRANCHARG}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot:"

SYSROOT_DIRS += "/boot"

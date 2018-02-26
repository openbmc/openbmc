SUMMARY = "Basic task to get a device booting"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r58"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

#
# those ones can be set in machine config to supply packages needed to get machine booting
#
MACHINE_ESSENTIAL_EXTRA_RDEPENDS ?= ""
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS ?= ""

# Make sure we build the kernel
DEPENDS = "virtual/kernel"

#
# minimal set of packages - needed to boot
#
RDEPENDS_${PN} = "\
    base-files \
    base-passwd \
    busybox \
    netbase \
    ${@bb.utils.contains("MACHINE_FEATURES", "keyboard", "keymaps", "", d)} \
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
"

RRECOMMENDS_${PN} = "\
    kernel \
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS} \
"

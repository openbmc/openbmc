SUMMARY = "Basic task to get a device booting"


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
RDEPENDS:${PN} = "\
    base-files \
    base-passwd \
    busybox \
    netbase \
    ${@bb.utils.contains("MACHINE_FEATURES", "keyboard", "keymaps", "", d)} \
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
"

RRECOMMENDS:${PN} = "\
    kernel \
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS} \
"

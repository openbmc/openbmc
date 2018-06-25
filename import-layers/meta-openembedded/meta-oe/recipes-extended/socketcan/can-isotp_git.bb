LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=72d977d697c3c05830fdff00a7448931"
SRCREV = "d1248b0f22ea8dc3b9d84a61bd35a83309bdb4b4"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/hartkopp/can-isotp.git;protocol=https"

S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

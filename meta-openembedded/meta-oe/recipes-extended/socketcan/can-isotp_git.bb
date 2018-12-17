LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=72d977d697c3c05830fdff00a7448931"
SRCREV = "6003f9997587e6a563cebf1f246bcd0eb6deff3d"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/hartkopp/can-isotp.git;protocol=https"

S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

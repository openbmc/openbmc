LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=72d977d697c3c05830fdff00a7448931"
SRCREV = "beb4650660179963a8ed5b5cbf2085cc1b34f608"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/hartkopp/can-isotp.git;protocol=https;branch=master"


S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

do_install:append() {
    install -Dm 644 ${S}/include/uapi/linux/can/isotp.h ${D}${includedir}/linux/can/isotp.h
}

EXCLUDE_FROM_WORLD = "1"

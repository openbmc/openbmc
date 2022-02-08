LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=72d977d697c3c05830fdff00a7448931"
SRCREV = "b31bce98d65f894aad6427bcf6f3f7822e261a59"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/hartkopp/can-isotp.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

PNBLACKLIST[can-isotp] = "Kernel module Needs forward porting to kernel 5.2+"

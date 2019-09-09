SUMMARY = "Distributed block device driver for Linux"
DESCRIPTION = "DRBD is a block device which is designed to build high \
               availability clusters. This is done by mirroring a whole \
               block device via (a dedicated) network. You could see \
               it as a network raid-1."
HOMEPAGE = "http://oss.linbit.com/drbd/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"
DEPENDS = "virtual/kernel"

SRC_URI = "http://www.linbit.com/downloads/drbd/9.0/drbd-${PV}.tar.gz \
           file://check_existence_of_modules_before_installing.patch \
           "
SRC_URI[md5sum] = "bd1202eaaf81641752d0c9b003b10f6c"
SRC_URI[sha256sum] = "5b3d2bcb99542940e40af903dea783cf04524e9f41cc530e210d2c15ef84fa58"

inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}'"

do_install () {
    oe_runmake install DESTDIR="${D}"
}

PNBLACKLIST[drbd] = "Kernel module Needs forward porting to kernel 5.2+"

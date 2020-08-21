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
SRC_URI[sha256sum] = "14970459f55bc465503b88b24d1a266b2ace0d69fe3cb387005b8477cd6475ed"

inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}' SPAAS=true"

do_install () {
    oe_runmake install DESTDIR="${D}"
}

PNBLACKLIST[drbd] ?= "Needs coccinelle to build with SPAAS"

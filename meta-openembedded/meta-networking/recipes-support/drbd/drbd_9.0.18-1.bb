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

SRC_URI[md5sum] = "f2e6eaa92861252af0b564f0100f1859"
SRC_URI[sha256sum] = "d6b4188ed01d8555c78b04b5e31532d5990ca98bf063230f3e949ee8a7338d58"

inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}'"

do_install () {
    oe_runmake install DESTDIR="${D}"
}

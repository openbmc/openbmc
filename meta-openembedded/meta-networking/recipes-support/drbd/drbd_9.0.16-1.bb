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
           file://check_existence_of_modules_before_installing.patch"

SRC_URI[md5sum] = "6414a9eef2807c8210b6403e36ce3ea4"
SRC_URI[sha256sum] = "6074b0d643f4bbae4641d3b4bc30f4840e74f2a212dd9c6555b8141e530e5789"

inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}'"

do_install () {
    oe_runmake install DESTDIR="${D}"
}

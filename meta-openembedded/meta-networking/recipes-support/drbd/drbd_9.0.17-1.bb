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
           file://0001-drbd-kbuild-fix-use-M-don-t-forget-addprefix-obj.patch \
           "

SRC_URI[md5sum] = "ae8d5030760b2820a4b3e250447890a0"
SRC_URI[sha256sum] = "86dd6cc0fdc8123056a3bb67a634cd7ba62a7b05b23caab9995cce7730891da8"

inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}'"

do_install () {
    oe_runmake install DESTDIR="${D}"
}

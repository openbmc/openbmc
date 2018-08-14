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

SRC_URI[md5sum] = "c1dd58043f46e9926b579aa65d4ea980"
SRC_URI[sha256sum] = "87f72d46db9bad926415b3ab9f5f1397de8c581d2e2ec1addbdd5ce2604e6123"
inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}'"

do_install () {
    oe_runmake install DESTDIR="${D}"
}

PNBLACKLIST[drbd] = "implicit declaration of function 'setup_timer'; 4.15 head file issue?" 

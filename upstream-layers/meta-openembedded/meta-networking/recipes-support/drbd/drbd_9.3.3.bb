SUMMARY = "Distributed block device driver for Linux"
DESCRIPTION = "DRBD is a block device which is designed to build high \
               availability clusters. This is done by mirroring a whole \
               block device via (a dedicated) network. You could see \
               it as a network raid-1."
HOMEPAGE = "http://oss.linbit.com/drbd/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"
DEPENDS = "virtual/kernel"

SRC_URI = "https://pkg.linbit.com//downloads/drbd/9/${BP}.tar.gz \
           "
SRC_URI[sha256sum] = "a7bfb016070c31df1c738569ca8cc5e5fc337dd449147f7bf62e746d87d38f21"

inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}' SPAAS=true"

do_install () {
    oe_runmake install DESTDIR="${D}"
}

SKIP_RECIPE[drbd] ?= "Needs coccinelle to build with SPAAS"

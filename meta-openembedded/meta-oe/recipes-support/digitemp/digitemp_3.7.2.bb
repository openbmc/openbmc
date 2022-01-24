SUMMARY = "read temperature sensors in a 1-Wire net"
SECTION = "util"
DEPENDS = "libusb1"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=44fee82a1d2ed0676cf35478283e0aa0"

SRC_URI = "git://github.com/bcl/digitemp;branch=master;protocol=https"

SRCREV = "a162e63aad35358aab325388f3d5e88121606419"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "ds9097 ds9097u \
                SYSTYPE='Linux' \
"
do_configure() {
    rm -f digitemp_*
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 digitemp_* ${D}${sbindir}
}

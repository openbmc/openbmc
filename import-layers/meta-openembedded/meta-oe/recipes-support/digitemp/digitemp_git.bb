SUMMARY = "read temperature sensors in a 1-Wire net"
SECTION = "util"
DEPENDS = "libusb1"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=44fee82a1d2ed0676cf35478283e0aa0"

PV = "3.7.1+git${SRCPV}"

SRC_URI = "git://github.com/bcl/digitemp"

SRCREV = "389f67655efa1674f595106c3a47b5ad082609a7"

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

SUMMARY = "read temperature sensors in a 1-Wire net"
SECTION = "util"
DEPENDS = "libusb1"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=44fee82a1d2ed0676cf35478283e0aa0"

PR = "r2"

SRC_URI = "http://www.digitemp.com/software/linux/digitemp-${PV}.tar.gz \
           file://makefile-fix.patch"
SRC_URI[md5sum] = "9be2e48db37920f21925ae6e88f83b84"
SRC_URI[sha256sum] = "14cfc584cd3714fe8c9a2cdc8388be49e08b5e395d95e6bcd11d4410e2505ca2"

EXTRA_OEMAKE = "ds9097 ds9097u \
                SYSTYPE='Linux' \
"
# Fix GNU_HASH QA errors
TARGET_CC_ARCH += "${CFLAGS} ${LDFLAGS}"

do_configure() {
    rm -f digitemp_*
}

do_install() {
    install -d ${D}${sbindir}
    install digitemp_* ${D}${sbindir}
}

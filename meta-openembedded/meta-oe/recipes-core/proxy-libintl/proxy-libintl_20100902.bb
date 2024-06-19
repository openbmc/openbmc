SUMMARY = "Proxy libintl"
HOMEPAGE = "http://ftp.gnome.org/pub/GNOME/binaries/win32/dependencies/"
SECTION = "libs"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/src/proxy-libintl/COPYING.LIB.txt;md5=bc400bc21422f9a92e76ec2c5167ca2e"

PROVIDES = "virtual/libintl"

SRC_URI = " \
    http://ftp.gnome.org/pub/GNOME/binaries/win32/dependencies/${BPN}-dev_${PV}_win32.zip \
"
SRC_URI[md5sum] = "aef407c2b97ee829383aadd867c61d1e"
SRC_URI[sha256sum] = "291ac350cc5eb4a01b0d651ca99fae64cee8a1c06b2005277fab5a4356f9ae91"

PACKAGES = "${PN} ${PN}-dev"
FILES:${PN}-dev = "${includedir}/libintl.h ${libdir}/libintl.a"
INSANE_SKIP:${PN}-dev = "staticdev"
ALLOW_EMPTY:${PN} = "1"
CFLAGS:append = " -fPIC -Wall -I ../../include ${@['-DSTUB_ONLY', ''][d.getVar('USE_NLS') != 'no']}"
TARGET_CC_ARCH += "${LDFLAGS}"

do_compile() {
    cd ${UNPACKDIR}/src/proxy-libintl
    oe_runmake ../../lib/libintl.a
}

do_install() {
    install -d ${D}/${includedir}
    install -d ${D}/${libdir}
    install -m 0644 ${UNPACKDIR}/include/libintl.h ${D}/${includedir}
    install -m 0644 ${UNPACKDIR}/lib/libintl.a ${D}/${libdir}
}

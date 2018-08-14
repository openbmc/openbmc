SUMMARY = "reimplement libdjb"
DESCRIPTION = "libowfat is a library of general purpose APIs extracted from Dan \
Bernstein's software (libdjb), reimplemented and covered by the GNU \
General Public License Version 2 (no later versions)."
HOMEPAGE = "http://www.fefe.de/libowfat"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "https://www.fefe.de/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "120798fab86cfd72dc6b12284d248dd0"
SRC_URI[sha256sum] = "d1e4ac1cfccbb7dc51d77d96398e6302d229ba7538158826c84cb4254c7e8a12"

EXTRA_OEMAKE = "\
    DIET= \
    CC='${BUILD_CC} ${BUILD_CPPFLAGS} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}' \
    CCC='${CC}' CFLAGS='${CFLAGS} -I.' CFLAGS_OPT='${CFLAGS} -I.' \
"

do_install() {
    make install \
        DESTDIR=${D} \
        INCLUDEDIR=${includedir}/${BPN} \
        LIBDIR=${libdir} \
        MAN3DIR=${mandir}/man3
}

BBCLASSEXTEND = "native nativesdk"

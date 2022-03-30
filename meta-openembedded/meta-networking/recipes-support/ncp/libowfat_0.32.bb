SUMMARY = "reimplement libdjb"
DESCRIPTION = "libowfat is a library of general purpose APIs extracted from Dan \
Bernstein's software (libdjb), reimplemented and covered by the GNU \
General Public License Version 2 (no later versions)."
HOMEPAGE = "http://www.fefe.de/libowfat"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "https://www.fefe.de/${BPN}/${BP}.tar.xz \
           file://0001-Depend-on-haveuint128.h-for-umult64.c.patch \
           file://0001-replace-__pure__-with-compiler-attribute-pure.patch \
          "
SRC_URI[md5sum] = "ee015ccf45cb2bc61c942642038c2bdc"
SRC_URI[sha256sum] = "f4b9b3d9922dc25bc93adedf9e9ff8ddbebaf623f14c8e7a5f2301bfef7998c1"

EXTRA_OEMAKE = "\
    DIET= \
    CC='${BUILD_CC} ${BUILD_CPPFLAGS} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}' \
    CCC='${CC}' CFLAGS='${CFLAGS} -I.' CFLAGS_OPT='${CFLAGS} -I.' \
"

do_install() {
    make install \
        DESTDIR=${D} \
        INCLUDEDIR=${includedir} \
        LIBDIR=${libdir} \
        MAN3DIR=${mandir}/man3
}

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"

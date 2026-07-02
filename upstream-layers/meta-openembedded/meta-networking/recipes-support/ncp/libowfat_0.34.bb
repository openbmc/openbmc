SUMMARY = "reimplement libdjb"
DESCRIPTION = "libowfat is a library of general purpose APIs extracted from Dan \
Bernstein's software (libdjb), reimplemented and covered by the GNU \
General Public License Version 2 (no later versions)."
HOMEPAGE = "http://www.fefe.de/libowfat"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "http://www.fefe.de/${BPN}/${BP}.tar.xz \
           file://0001-Depend-on-haveuint128.h-for-umult64.c.patch \
           file://0001-fix-incompatible-type-error-with-gcc-15.patch \
           file://0001-GNUmakefile-build-json-helper-for-the-build-host.patch \
          "
SRC_URI[sha256sum] = "d4330d373ac9581b397bc24a22ad1f7f5d58a7fe36d9d239fe352ceffc5d304b"

# GCC 14+ promotes -Wincompatible-pointer-types to an error. libowfat 0.34
# passes read()/write() directly where a typeless op pointer is expected.
CFLAGS += "-Wno-error=incompatible-pointer-types"

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

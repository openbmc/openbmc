SUMMARY = "Library that provides weak aliases for pthread functions"
DESCRIPTION = "This library provides weak aliases for pthread functions \
not provided in libc or otherwise available by default."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "http://bugs.freedesktop.org/buglist.cgi?product=XCB"
SECTION = "x11/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6edc1fea03d959f0c2d743fe5ca746ad"

SRC_URI = "http://xcb.freedesktop.org/dist/${BP}.tar.bz2"
SRC_URI[md5sum] = "48c1544854a94db0e51499cc3afd797f"
SRC_URI[sha256sum] = "e4d05911a3165d3b18321cc067fdd2f023f06436e391c6a28dff618a78d2e733"

inherit autotools

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"

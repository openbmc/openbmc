SUMMARY = "Library that provides weak aliases for pthread functions"
DESCRIPTION = "This library provides weak aliases for pthread functions \
not provided in libc or otherwise available by default."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "http://bugs.freedesktop.org/buglist.cgi?product=XCB"
SECTION = "x11/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6edc1fea03d959f0c2d743fe5ca746ad"

#DEPENDS = "xcb-proto xproto libxau libxslt-native"
# DEPENDS += "xsltproc-native gperf-native"

ALLOW_EMPTY_${PN} = "1"

SRC_URI = "http://xcb.freedesktop.org/dist/libpthread-stubs-${PV}.tar.bz2"

SRC_URI[md5sum] = "e8fa31b42e13f87e8f5a7a2b731db7ee"
SRC_URI[sha256sum] = "35b6d54e3cc6f3ba28061da81af64b9a92b7b757319098172488a660e3d87299"

inherit autotools pkgconfig

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"

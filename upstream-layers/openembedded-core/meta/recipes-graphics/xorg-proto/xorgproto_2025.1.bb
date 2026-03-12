SUMMARY = "X Window System unified protocol definitions"
DESCRIPTION = "This package provides the headers and specification documents defining \
the core protocol and (many) extensions for the X Window System"
HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING-x11proto;md5=0b9fe3db4015bcbe920e7c67a39ee3f1"

SRC_URI = "${XORG_MIRROR}/individual/proto/${BP}.tar.xz"
SRC_URI[sha256sum] = "56898c716c0578df8a2d828c9c3e5c528277705c0484381a81960fe1a67668e8"

inherit meson

PACKAGECONFIG ??= ""
PACKAGECONFIG[legacy] = "-Dlegacy=true,-Dlegacy=false"

# Datadir only used to install pc files, $datadir/pkgconfig
datadir = "${libdir}"
# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
DEV_PKG_DEPENDENCY = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"

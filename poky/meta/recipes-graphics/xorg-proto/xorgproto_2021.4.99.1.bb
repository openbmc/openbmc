SUMMARY = "X Window System unified protocol definitions"
DESCRIPTION = "This package provides the headers and specification documents defining \
the core protocol and (many) extensions for the X Window System"
HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING-x11proto;md5=dfc4bd2b0568b31725b85b0604e69b56"

SRC_URI = "${XORG_MIRROR}/individual/proto/${BP}.tar.bz2"
SRC_URI[sha256sum] = "0bce7d4fe800dcb5581cc59a99946c12e6e0be292636544221ec73e96f1a28ed"

inherit meson

PACKAGECONFIG ??= ""
PACKAGECONFIG[legacy] = "-Dlegacy=true,-Dlegacy=false"

# Datadir only used to install pc files, $datadir/pkgconfig
datadir="${libdir}"
# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "Generic PCI access library for X"
DESCRIPTION = "libpciaccess provides functionality for X to access the \
PCI bus and devices in a platform-independent way."

HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"
SECTION = "x11/libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=54c978968e565218eea36cf03ef24352"

DEPENDS = "util-macros"

SRC_URI = "${XORG_MIRROR}/individual/lib/${BP}.tar.xz"
SRC_URI[sha256sum] = "3c55aa86c82e54a4e3109786f0463530d53b36b6d1cfd14616454f985dd2aa43"

inherit pkgconfig meson

PACKAGECONFIG ?= "zlib"
PACKAGECONFIG[zlib] = "-Dzlib=enabled,-Dzlib=disabled,zlib"

BBCLASSEXTEND = "native nativesdk"

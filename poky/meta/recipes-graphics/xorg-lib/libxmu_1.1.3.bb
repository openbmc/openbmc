SUMMARY = "Xmu and Xmuu: X Miscellaneous Utility libraries"

DESCRIPTION = "The Xmu Library is a collection of miscellaneous (some \
might say random) utility functions that have been useful in building \
various applications and widgets. This library is required by the Athena \
Widgets. A subset of the functions that do not rely on the Athena \
Widgets (libXaw) or X Toolkit Instrinsics (libXt) are provided in a \
second library, libXmuu."

require xorg-lib-common.inc

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=def3d8e4e9c42004f1941fa22f01dc18"

DEPENDS += "libxt libxext"
PROVIDES = "xmu"

PE = "1"

XORG_PN = "libXmu"

LEAD_SONAME = "libXmu"

PACKAGES =+ "libxmuu"

FILES:libxmuu = "${libdir}/libXmuu.so.*"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "ac774cff8b493f566088a255dbf91201"
SRC_URI[sha256sum] = "9c343225e7c3dc0904f2122b562278da5fed639b1b5e880d25111561bac5b731"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

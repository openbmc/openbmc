SUMMARY = "Xmu and Xmuu: X Miscellaneous Utility libraries"

DESCRIPTION = "The Xmu Library is a collection of miscellaneous (some \
might say random) utility functions that have been useful in building \
various applications and widgets. This library is required by the Athena \
Widgets. A subset of the functions that do not rely on the Athena \
Widgets (libXaw) or X Toolkit Instrinsics (libXt) are provided in a \
second library, libXmuu."

require xorg-lib-common.inc

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e79ad4fcc53b9bfe0fc38507a56446b9"

DEPENDS += "libxt libxext"
PROVIDES = "xmu"

PE = "1"

XORG_PN = "libXmu"

LEAD_SONAME = "libXmu"

PACKAGES =+ "libxmuu"

FILES:libxmuu = "${libdir}/libXmuu.so.*"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "210de3ab9c3e9382572c25d17c2518a854ce6e2c62c5f8315deac7579e758244"

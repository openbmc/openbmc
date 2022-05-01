SUMMARY = "XvMC: X Video Motion Compensation extension library"

DESCRIPTION = "XvMC extends the X Video extension (Xv) and enables \
hardware rendered motion compensation support."

require xorg-lib-common.inc
SRC_URI = "${XORG_MIRROR}/individual/lib/${XORG_PN}-${PV}.tar.xz"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0a207f08d4961489c55046c9a5e500da \
                    file://wrapper/XvMCWrapper.c;endline=26;md5=5151daa8172a3f1bb0cb0e0ff157d9de"

DEPENDS += "libxext libxv xorgproto"

PE = "1"

XORG_PN = "libXvMC"

SRC_URI[sha256sum] = "0a9ebe6dea7888a747e5aca1b891d53cd7d3a5f141a9645f77d9b6a12cee657c"

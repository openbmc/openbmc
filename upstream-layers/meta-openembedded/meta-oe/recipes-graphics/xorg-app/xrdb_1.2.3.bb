require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "X server resource database utility"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1167c4f586bd41f0c62166db4384a69"

DEPENDS += "libxmu"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "c88f560243278c896ce4fc92ae5a45a2b505a316ffa427fe55b02e5d5914c4e4"

EXTRA_OECONF += "--with-cpp=${bindir}/cpp"

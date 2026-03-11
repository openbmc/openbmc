require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "X server resource database utility"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1167c4f586bd41f0c62166db4384a69"

DEPENDS += "libxmu"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "31f5fcab231b38f255b00b066cf7ea3b496df712c9eb2d0d50c670b63e5033f4"

EXTRA_OECONF += "--with-cpp=${bindir}/cpp"

require xorg-driver-video.inc

SUMMARY = "X.Org X server -- cirrus display driver"
DESCRIPTION = "cirrus is an Xorg driver for Cirrus Logic VGA adapters. These \
devices are not so common in the wild anymore, but QEMU can emulate one, so \
the driver is still useful."

LIC_FILES_CHKSUM = "file://COPYING;md5=6ddc7ca860dc5fd014e7f160ea699295"

SRC_URI[sha256sum] = "0ef3fa0083de3d9a040f11b3af38598d8405f1165b908fdd6712c30038326401"

DEPENDS += "libpciaccess"

XORG_DRIVER_COMPRESSOR = ".tar.xz"

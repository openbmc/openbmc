require xorg-driver-video.inc

SUMMARY = "X.Org X server -- fbdev display driver"
DESCRIPTION = "fbdev is an Xorg driver for framebuffer devices. This is a non-accelerated driver."

LIC_FILES_CHKSUM = "file://COPYING;md5=d8cbd99fff773f92e844948f74ef0df8"

SRC_URI[sha256sum] = "9c2bc0fb9af092804138e8d5cb5627cabf2919ef60f0d1544a95c4ac2047f387"
XORG_DRIVER_COMPRESSOR = ".tar.xz"

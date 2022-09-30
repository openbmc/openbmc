require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xwud/"
DESCRIPTION = "xwud allows X users to display in a window an image saved \
in a specially formatted dump file, such as produced by xwd."
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=31e8892c80b7a0c1c5f37c8e8ae6d794"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "64048cd15eba3cd9a3d2e3280650391259ebf6b529f2101d1a20f441038c1afe"

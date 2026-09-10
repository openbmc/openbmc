require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xstdcmap"
DESCRIPTION = "The xstdcmap utility can be used to selectively define \
standard colormap properties."
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b08d9e2e718ac83e6fe2b974d4b5fd8"

DEPENDS += "libxmu"
BBCLASSEXTEND = "native"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "062a15722769799524eee542fc82ae778f5ac180b618295d913f34962b134ffe"


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

SRC_URI[md5sum] = "eb5473acaef15a5db9b50df29c6a7f90"
SRC_URI[sha256sum] = "f648e2b0cf16aa29856de998e2c7204be39dc1f8daeda9464d32288e0b580fc1"


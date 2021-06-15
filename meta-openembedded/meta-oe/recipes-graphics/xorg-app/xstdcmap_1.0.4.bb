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

SRC_URI[md5sum] = "da67142c128d18386ff145882e0afc66"
SRC_URI[sha256sum] = "06898b3f1eaad0b205ff3c75bdefa3207868b889d4cb37b32b8267b2bbfe6f8b"


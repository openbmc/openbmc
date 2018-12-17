SUMMARY = "XFree86-Misc: XFree86 miscellaneous extension library"

DESCRIPTION = "The XFree86-Misc extension, provides a means to access \
input device configuration settings specific to the XFree86/Xorg DDX."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=27c91ecc435bd3d2bfad868914c94b45"

DEPENDS += "libxext"
PROVIDES = "xxf86misc"

PE = "1"

XORG_PN = "libXxf86misc"

SRC_URI[md5sum] = "37ad70f8b53b94b550f9290be97fbe2d"
SRC_URI[sha256sum] = "a89c03e2b0f16239d67a2031b9003f31b5a686106bbdb3c797fb88ae472af380"

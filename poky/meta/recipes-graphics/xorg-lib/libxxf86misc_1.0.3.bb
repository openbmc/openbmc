SUMMARY = "XFree86-Misc: XFree86 miscellaneous extension library"

DESCRIPTION = "The XFree86-Misc extension, provides a means to access \
input device configuration settings specific to the XFree86/Xorg DDX."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=27c91ecc435bd3d2bfad868914c94b45"

DEPENDS += "libxext xf86miscproto"
PROVIDES = "xxf86misc"

PE = "1"

XORG_PN = "libXxf86misc"

SRC_URI[md5sum] = "6bc0bf78909fd71021c466c793d4385c"
SRC_URI[sha256sum] = "563f4200862efd3334c33a669e0a0aae5bab31f3998db75b87a99a697cc26b5b"


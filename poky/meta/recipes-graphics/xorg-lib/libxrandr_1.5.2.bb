SUMMARY = "XRandR: X Resize, Rotate and Reflect extension library"

DESCRIPTION = "The X Resize, Rotate and Reflect Extension, called RandR \
for short, brings the ability to resize, rotate and reflect the root \
window of a screen. It is based on the X Resize and Rotate Extension as \
specified in the Proceedings of the 2001 Usenix Technical Conference \
[RANDR]."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9d1a2118a6cd5727521db8e7a2fee69"

DEPENDS += "virtual/libx11 xorgproto libxrender libxext"

PE = "1"

XORG_PN = "libXrandr"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "18f3b20d522f45e4dadd34afb5bea048"
SRC_URI[sha256sum] = "8aea0ebe403d62330bb741ed595b53741acf45033d3bda1792f1d4cc3daee023"

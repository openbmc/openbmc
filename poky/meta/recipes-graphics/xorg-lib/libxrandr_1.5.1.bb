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

SRC_URI[md5sum] = "28e486f1d491b757173dd85ba34ee884"
SRC_URI[sha256sum] = "1ff9e7fa0e4adea912b16a5f0cfa7c1d35b0dcda0e216831f7715c8a3abcf51a"

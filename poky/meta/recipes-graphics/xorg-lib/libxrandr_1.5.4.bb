SUMMARY = "XRandR: X Resize, Rotate and Reflect extension library"

DESCRIPTION = "The X Resize, Rotate and Reflect Extension, called RandR \
for short, brings the ability to resize, rotate and reflect the root \
window of a screen. It is based on the X Resize and Rotate Extension as \
specified in the Proceedings of the 2001 Usenix Technical Conference \
[RANDR]."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9d1a2118a6cd5727521db8e7a2fee69"

DEPENDS += "virtual/libx11 xorgproto libxrender libxext"

PE = "1"

XORG_PN = "libXrandr"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "1ad5b065375f4a85915aa60611cc6407c060492a214d7f9daf214be752c3b4d3"

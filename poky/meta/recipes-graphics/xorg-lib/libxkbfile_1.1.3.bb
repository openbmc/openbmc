SUMMARY = "XKB: X Keyboard File manipulation library"

DESCRIPTION = "libxkbfile provides an interface to read and manipulate \
description files for XKB, the X11 keyboard configuration extension."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8be7367f7e5d605a426f76bb37d4d61f"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

BBCLASSEXTEND = "native"
SRC_URI[sha256sum] = "a9b63eea997abb9ee6a8b4fbb515831c841f471af845a09de443b28003874bec"

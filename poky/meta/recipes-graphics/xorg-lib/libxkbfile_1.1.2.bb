SUMMARY = "XKB: X Keyboard File manipulation library"

DESCRIPTION = "libxkbfile provides an interface to read and manipulate \
description files for XKB, the X11 keyboard configuration extension."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8be7367f7e5d605a426f76bb37d4d61f"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

BBCLASSEXTEND = "native"
SRC_URI[sha256sum] = "b8a3784fac420b201718047cfb6c2d5ee7e8b9481564c2667b4215f6616644b1"

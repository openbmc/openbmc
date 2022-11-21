SUMMARY = "XKB: X Keyboard File manipulation library"

DESCRIPTION = "libxkbfile provides an interface to read and manipulate \
description files for XKB, the X11 keyboard configuration extension."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8be7367f7e5d605a426f76bb37d4d61f"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

BBCLASSEXTEND = "native"
XORG_EXT = "tar.xz"
SRC_URI[sha256sum] = "8623dc26e7aac3c5ad8a25e57b566f4324f5619e5db38457f0804ee4ed953443"

SUMMARY = "XKB: X Keyboard File manipulation library"

DESCRIPTION = "libxkbfile provides an interface to read and manipulate \
description files for XKB, the X11 keyboard configuration extension."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=8be7367f7e5d605a426f76bb37d4d61f"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "dd7e1e946def674e78c0efbc5c7d5b3b"
SRC_URI[sha256sum] = "758dbdaa20add2db4902df0b1b7c936564b7376c02a0acd1f2a331bd334b38c7"

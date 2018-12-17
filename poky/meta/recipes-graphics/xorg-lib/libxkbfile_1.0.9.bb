SUMMARY = "XKB: X Keyboard File manipulation library"

DESCRIPTION = "libxkbfile provides an interface to read and manipulate \
description files for XKB, the X11 keyboard configuration extension."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=8be7367f7e5d605a426f76bb37d4d61f"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "4a4cfeaf24dab1b991903455d6d7d404"
SRC_URI[sha256sum] = "51817e0530961975d9513b773960b4edd275f7d5c72293d5a151ed4f42aeb16a"

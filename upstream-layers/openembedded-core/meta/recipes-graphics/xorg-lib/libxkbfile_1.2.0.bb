SUMMARY = "XKB: X Keyboard File manipulation library"

DESCRIPTION = "libxkbfile provides an interface to read and manipulate \
description files for XKB, the X11 keyboard configuration extension."
HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8be7367f7e5d605a426f76bb37d4d61f"

DEPENDS += "util-macros virtual/libx11 xorgproto"

XORG_PN = "${BPN}"
XORG_EXT ?= "tar.xz"

SRC_URI = "${XORG_MIRROR}/individual/lib/${XORG_PN}-${PV}.${XORG_EXT}"
SRC_URI[sha256sum] = "7f71884e5faf56fb0e823f3848599cf9b5a9afce51c90982baeb64f635233ebf"

S = "${UNPACKDIR}/${XORG_PN}-${PV}"

PE = "1"

inherit meson features_check pkgconfig

REQUIRED_DISTRO_FEATURES ?= "x11"

BBCLASSEXTEND = "native"

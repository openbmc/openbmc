require recipes-graphics/xorg-lib/xorg-lib-common.inc
SUMMARY = "X Athena Widget Set"
DEPENDS += "xorgproto libxfixes libxrandr"

LIC_FILES_CHKSUM = "file://COPYING;md5=3c1ce42c334a6f5cccb0277556a053e0"

SRC_URI = "http://xorg.freedesktop.org/archive/individual/lib/libXpresent-${PV}.tar.bz2"
SRC_URI[md5sum] = "edd87ac15bb666081898dd7382fcbff5"
SRC_URI[sha256sum] = "c11ae015141a9afbe10f4f2b8ee00b11adca6373dc1b9808d7c6c138b2da7b8a"

XORG_PN = "libXpresent"

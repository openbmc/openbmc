require recipes-graphics/xorg-lib/xorg-lib-common.inc
SUMMARY = "X Athena Widget Set"
DEPENDS += "xorgproto libxfixes libxrandr"

LIC_FILES_CHKSUM = "file://COPYING;md5=3c1ce42c334a6f5cccb0277556a053e0"

XORG_EXT = "tar.bz2"

SRC_URI = "http://xorg.freedesktop.org/archive/individual/lib/libXpresent-${PV}.tar.xz"
SRC_URI[sha256sum] = "4e5b21b4812206a4b223013606ae31170502c1043038777a1ef8f70c09d37602"

XORG_PN = "libXpresent"

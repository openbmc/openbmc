require recipes-graphics/xorg-lib/xorg-lib-common.inc
SUMMARY = "X Athena Widget Set"
DEPENDS += "xorgproto libxfixes libxrandr"

LIC_FILES_CHKSUM = "file://COPYING;md5=3c1ce42c334a6f5cccb0277556a053e0"

XORG_EXT = "tar.bz2"

SRC_URI = "http://xorg.freedesktop.org/archive/individual/lib/libXpresent-${PV}.tar.xz"
SRC_URI[sha256sum] = "b964df9e5a066daa5e08d2dc82692c57ca27d00b8cc257e8e960c9f1cf26231b"

XORG_PN = "libXpresent"

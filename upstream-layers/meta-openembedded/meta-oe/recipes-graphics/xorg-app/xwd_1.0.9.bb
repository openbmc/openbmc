require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "xwd is a tool to capture an X window or screen to file"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xwd/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0cdb783e9a0198237371fdaa26a18bf"
DEPENDS = "libxmu libxkbfile"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "dc121b84947eb4a3d1131bff1e9844cfa2124d95b47b35f9932340fa931fbd3f"

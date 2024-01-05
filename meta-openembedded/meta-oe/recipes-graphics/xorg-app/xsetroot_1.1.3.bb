require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xsetroot/"
DESCRIPTION = "xsetroot is a root window parameter setting utility for X"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ea29dbee22324787c061f039e0529de"

DEPENDS += "xbitmaps libxcursor libxmu"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "6081b45a9eb4426e045d259d1e144b32417fb635e5b96aa90647365ac96638d1"

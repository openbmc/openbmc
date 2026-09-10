require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xsetroot/"
DESCRIPTION = "xsetroot is a root window parameter setting utility for X"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ea29dbee22324787c061f039e0529de"

DEPENDS += "xbitmaps libxcursor libxmu"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "1315a3f7e9abe06357363b93461e272601f67225ce0bc075c430cce35073362b"

require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xlsatoms/"
DESCRIPTION = "Xlsatoms lists the interned atoms defined on an X11 server"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b08d9e2e718ac83e6fe2b974d4b5fd8"

DEPENDS += "libxmu"
BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "5dcb6e6c4b28c8d7aeb45257f5a72a7d"
SRC_URI[sha256sum] = "47e5dc7c3dbda6db2cf8c00cedac1722835c1550aa21cfdbc9ba83906694dea4"

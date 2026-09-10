require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xlsatoms/"
DESCRIPTION = "Xlsatoms lists the interned atoms defined on an X11 server"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b5c50f6b3acc044e9a0cca8b74c8eb93"

DEPENDS += "libxmu"
BBCLASSEXTEND = "native"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "9ac9400328fbeffe15c9d94a90dedb90d09dcaa9d51482efabc55b33a736e317"

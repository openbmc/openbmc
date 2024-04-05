require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xrefresh/"
DESCRIPTION = "xrefresh - refresh all or part of an X screen"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=dad633bce9c3cd0e3abf72a16e0057cf"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "29ed592d5ece35a3029004d8c46f3002f92970870a96c11e38baf7f1122b8b5f"
SRC_URI_EXT = "xz"

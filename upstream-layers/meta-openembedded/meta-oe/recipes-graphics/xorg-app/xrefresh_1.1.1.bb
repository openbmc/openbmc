require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xrefresh/"
DESCRIPTION = "xrefresh - refresh all or part of an X screen"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=dad633bce9c3cd0e3abf72a16e0057cf"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "e947048130f6300e8be86573af8f225b486884fac14fcfe01bd913b41fa73bd3"
SRC_URI_EXT = "xz"

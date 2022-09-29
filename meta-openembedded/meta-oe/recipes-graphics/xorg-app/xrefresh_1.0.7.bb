require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xrefresh/"
DESCRIPTION = "xrefresh - refresh all or part of an X screen"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=dad633bce9c3cd0e3abf72a16e0057cf"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "a9f1d635f2f42283d0174e94d09ab69190c227faa5ab542bfe15ed9607771b1e"
SRC_URI_EXT = "xz"

require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xlsatoms/"
DESCRIPTION = "Xlsatoms lists the interned atoms defined on an X11 server"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b08d9e2e718ac83e6fe2b974d4b5fd8"

DEPENDS += "libxmu"
BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "61671fee12535347db24ec3a715032a7"
SRC_URI[sha256sum] = "57868f958c263976727881f3078e55b86b4a109dc578d2b92f5c6d690850a382"

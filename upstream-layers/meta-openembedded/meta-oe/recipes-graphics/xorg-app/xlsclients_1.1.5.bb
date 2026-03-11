require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xlsclients/"
DESCRIPTION = "xlsclients is a utility for listing information about the \
client applications running on a X11 server."
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=350e1b718a56df39cfe8ca9345ea4575"

BBCLASSEXTEND = "native"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "68baee57e70250ac4a7759fb78221831f97d88bc8e51dcc2e64eb3f8ca56bae3"

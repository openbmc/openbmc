require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xlsclients/"
DESCRIPTION = "xlsclients is a utility for listing information about the \
client applications running on a X11 server."
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=350e1b718a56df39cfe8ca9345ea4575"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "4fa92377e0ddc137cd226a7a87b6b29a"
SRC_URI[sha256sum] = "773f2af49c7ea2c44fba4213bee64325875c1b3c9bc4bbcd8dac9261751809c1"

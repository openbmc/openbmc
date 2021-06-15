require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xsetroot/"
DESCRIPTION = "xsetroot is a root window parameter setting utility for X"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ea29dbee22324787c061f039e0529de"

DEPENDS += "xbitmaps libxcursor"
BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "5fe769c8777a6e873ed1305e4ce2c353"
SRC_URI[sha256sum] = "10c442ba23591fb5470cea477a0aa5f679371f4f879c8387a1d9d05637ae417c"

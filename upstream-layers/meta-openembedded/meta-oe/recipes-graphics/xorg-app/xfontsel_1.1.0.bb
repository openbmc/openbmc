require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "xfontsel provides point and click selection of X11 font names"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xfontsel/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4669d2703c60d585cc29ba7e9a69bcb3"
DEPENDS += " libxaw"

LIC_FILES_CHKSUM = "file://COPYING;md5=4669d2703c60d585cc29ba7e9a69bcb3"

SRC_URI[sha256sum] = "17052c3357bbfe44b8468675ae3d099c2427ba9fcac10540aef524ae4d77d1b4"
SRC_URI_EXT = "xz"

require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "bdftopcf is a tool to convert BDF fonts to PCF fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/bdftopcf/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f9a35333adf75edd1eaef84bca65a490"
DEPENDS = "libxfont"

SRC_URI[md5sum] = "53a48e1fdfec29ab2e89f86d4b7ca902"
SRC_URI[sha256sum] = "38f447be0c61f94c473f128cf519dd0cff63b5d7775240a2e895a183a61e2026"

BBCLASSEXTEND = "native"

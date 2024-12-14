require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "bdftopcf is a tool to convert BDF fonts to PCF fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/bdftopcf/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f9a35333adf75edd1eaef84bca65a490"
DEPENDS = "libxfont"

SRC_URI[sha256sum] = "4b4df05fc53f1e98993638d6f7e178d95b31745c4568cee407e167491fd311a2"

BBCLASSEXTEND = "native"

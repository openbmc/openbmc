require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"

DESCRIPTION = "A collection of utilities used to tweak and query the runtime configuration\
of the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=13f70acf3c27f5f834bbc954df775f8e"

BBCLASSEXTEND = "native"

DEPENDS += "libice"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "2cb9dfcb545683af77fb1029bea3fc52dcc8a0666f7b8b2d7373b6ed4c408c05"


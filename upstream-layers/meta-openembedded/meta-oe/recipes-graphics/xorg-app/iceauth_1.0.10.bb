require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"

DESCRIPTION = "A collection of utilities used to tweak and query the runtime configuration\
of the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=13f70acf3c27f5f834bbc954df775f8e"

BBCLASSEXTEND = "native"

DEPENDS += "libice"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "3deefb7da26af9dc799b5628d929d91c9af68c78575639944db3b955f29aa029"


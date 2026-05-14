require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"

DESCRIPTION = "A collection of utilities used to tweak and query the runtime configuration\
of the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=13f70acf3c27f5f834bbc954df775f8e"

BBCLASSEXTEND = "native"

DEPENDS += "libice"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "9d633cf0d4d1d98e3ef02d18660372958b60a67016e8a72cd04093a8d8f8d4e9"


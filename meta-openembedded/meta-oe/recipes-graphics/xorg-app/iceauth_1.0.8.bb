require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"

DESCRIPTION = "A collection of utilities used to tweak and query the runtime configuration\
of the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=13f70acf3c27f5f834bbc954df775f8e"

BBCLASSEXTEND = "native"

DEPENDS += "libice"

SRC_URI[md5sum] = "3b9b79fa0f9928161f4bad94273de7ae"
SRC_URI[sha256sum] = "e6ee213a217265cc76050e4293ea70b98c32dce6505c6421227efbda62ab60c6"


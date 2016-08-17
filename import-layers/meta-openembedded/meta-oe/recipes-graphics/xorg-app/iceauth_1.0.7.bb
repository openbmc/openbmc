require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"

DESCRIPTION = "A collection of utilities used to tweak and query the runtime configuration\
of the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=13f70acf3c27f5f834bbc954df775f8e"

BBCLASSEXTEND = "native"

DEPENDS += "libice"

SRC_URI[md5sum] = "25dab02f8e40d5b71ce29a07dc901b8c"
SRC_URI[sha256sum] = "1216af2dee99b318fcf8bf9a259915273bcb37a7f1e7859af4f15d0ebf6f3f0a"


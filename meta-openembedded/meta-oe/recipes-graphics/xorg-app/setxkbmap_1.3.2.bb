require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "A program to compile XKB keyboard description"

DESCRIPTION = "The xkbcomp keymap compiler converts a description of an \
XKB keymap into one of several output formats. The most common use for \
xkbcomp is to create a compiled keymap file (.xkm extension) which can \
be read directly by XKB-capable X servers or utilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=5feafdbe6dfe9e2bd32325be0cfc86f8"

PE = "1"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "93e736c98fb75856ee8227a0c49a128d"
SRC_URI[sha256sum] = "8ff27486442725e50b02d7049152f51d125ecad71b7ce503cfa09d5d8ceeb9f5"

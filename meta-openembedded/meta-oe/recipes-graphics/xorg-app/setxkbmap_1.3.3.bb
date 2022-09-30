require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "A program to compile XKB keyboard description"

DESCRIPTION = "The xkbcomp keymap compiler converts a description of an \
XKB keymap into one of several output formats. The most common use for \
xkbcomp is to create a compiled keymap file (.xkm extension) which can \
be read directly by XKB-capable X servers or utilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=5feafdbe6dfe9e2bd32325be0cfc86f8"

PE = "1"

DEPENDS += "libxkbfile xrandr"

BBCLASSEXTEND = "native"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "b560c678da6930a0da267304fa3a41cc5df39a96a5e23d06f14984c87b6f587b"

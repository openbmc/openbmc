require xorg-app-common.inc

SUMMARY = "A program to compile XKB keyboard description"

DESCRIPTION = "The xkbcomp keymap compiler converts a description of an \
XKB keymap into one of several output formats. The most common use for \
xkbcomp is to create a compiled keymap file (.xkm extension) which can \
be read directly by XKB-capable X servers or utilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=be5e191f04d3f2cd80aa83b928ba984c"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

EXTRA_OECONF += "--disable-selective-werror"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "0a288114e5f44e31987042c79aecff1ffad53a8154b8ec971c24a69a80f81f77"

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
SRC_URI[sha256sum] = "fa50d611ef41e034487af7bd8d8c718df53dd18002f591cca16b0384afc58e98"

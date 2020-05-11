require xorg-app-common.inc

SUMMARY = "A program to compile XKB keyboard description"

DESCRIPTION = "The xkbcomp keymap compiler converts a description of an \
XKB keymap into one of several output formats. The most common use for \
xkbcomp is to create a compiled keymap file (.xkm extension) which can \
be read directly by XKB-capable X servers or utilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=08436e4f4476964e2e2dd7e7e41e076a"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

EXTRA_OECONF += "--disable-selective-werror"

SRC_URI[md5sum] = "6e4751d99373f85d459ab4dff28893f5"
SRC_URI[sha256sum] = "06242c169fc11caf601cac46d781d467748c6a330e15b36dce46520b8ac8d435"

require xorg-app-common.inc

SUMMARY = "A program to compile XKB keyboard description"

DESCRIPTION = "The xkbcomp keymap compiler converts a description of an \
XKB keymap into one of several output formats. The most common use for \
xkbcomp is to create a compiled keymap file (.xkm extension) which can \
be read directly by XKB-capable X servers or utilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=08436e4f4476964e2e2dd7e7e41e076a"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "12610df19df2af3797f2c130ee2bce97"
SRC_URI[sha256sum] = "6dd8bcb9be7e85bd7294abe261b8c7b0539d2fc93e41b80fb8bd013767ce8424"

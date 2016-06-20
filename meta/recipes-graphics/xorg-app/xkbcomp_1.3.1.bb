require xorg-app-common.inc

SUMMARY = "A program to compile XKB keyboard description"

DESCRIPTION = "The xkbcomp keymap compiler converts a description of an \
XKB keymap into one of several output formats. The most common use for \
xkbcomp is to create a compiled keymap file (.xkm extension) which can \
be read directly by XKB-capable X servers or utilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=08436e4f4476964e2e2dd7e7e41e076a"

PR = "${INC_PR}.0"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "a4d8353daf6cb0a9c47379b7413c42c6"
SRC_URI[sha256sum] = "0304dc9e0d4ac10831a9ef5d5419722375ddbc3eac3ff4413094d57bc1f1923d"

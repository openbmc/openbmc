require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "A program to compile XKB keyboard description"
DESCRIPTION = "xkbprint generates a printable or encapsulated PostScript \
description of an XKB keyboard description."

LIC_FILES_CHKSUM = "file://COPYING;md5=20f28f97555b220fde762bc2a4406a8f"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "6dbb3dc79b1d1e17045a65f86e7581e149c75ee5aacae69a6cf685acf78fc31b"
SRC_URI_EXT = "xz"

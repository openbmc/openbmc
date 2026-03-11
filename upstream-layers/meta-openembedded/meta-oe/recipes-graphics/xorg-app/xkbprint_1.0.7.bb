require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "A program to compile XKB keyboard description"
DESCRIPTION = "xkbprint generates a printable or encapsulated PostScript \
description of an XKB keyboard description."

LIC_FILES_CHKSUM = "file://COPYING;md5=20f28f97555b220fde762bc2a4406a8f"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "1c4f49c030329e0defd8c7bc3739e60f2aa1d2aabe0e2d7e63740ab629aa59cc"
SRC_URI_EXT = "xz"

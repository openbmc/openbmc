require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "A program to compile XKB keyboard description"
DESCRIPTION = "xkbprint generates a printable or encapsulated PostScript \
description of an XKB keyboard description."

LIC_FILES_CHKSUM = "file://COPYING;md5=20f28f97555b220fde762bc2a4406a8f"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "99cc9404f7b90289ae04944c0d98a208cc8b158492ad6481386e31d4d09aa7b0"
SRC_URI_EXT = "xz"

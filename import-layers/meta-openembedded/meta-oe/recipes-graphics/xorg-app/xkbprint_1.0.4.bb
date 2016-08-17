require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "A program to compile XKB keyboard description"
DESCRIPTION = "xkbprint generates a printable or encapsulated PostScript \
description of an XKB keyboard description."

LIC_FILES_CHKSUM = "file://COPYING;md5=20f28f97555b220fde762bc2a4406a8f"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "3c25b40de530112c08bf7d229c5c6a28"
SRC_URI[sha256sum] = "0b3faab8fefad03dfb7e866f634cf859822801de6b5fc6cf5e0a62857ed93e12"

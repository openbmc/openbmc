require xorg-doc-common.inc
PE = "1"

DEPENDS += "util-macros"

LIC_FILES_CHKSUM = "file://COPYING;md5=c8c6c808cd3c797a07b24e443af1c449"

SRC_URI[sha256sum] = "65a9fdddedc17bd5e9c0b00d904960f03f047c3a62de5458989d493c29fec806"

FILES:${PN} += " /usr/share/sgml/X11"

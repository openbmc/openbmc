require xorg-doc-common.inc
PE = "1"
PR = "${INC_PR}.0"

DEPENDS += "util-macros"

LIC_FILES_CHKSUM = "file://COPYING;md5=c8c6c808cd3c797a07b24e443af1c449"

SRC_URI[md5sum] = "ee6dc0a8a693a908857af328b2462661"
SRC_URI[sha256sum] = "e71ce1df82dcc51eb14be6f42171dcc6bdd11ef46c0c605d6da8af12bd73b74c"

FILES_${PN} += " /usr/share/sgml/X11"

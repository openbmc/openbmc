require xorg-app-common.inc

SUMMARY = "X11 color name database"
DESCRIPTION = "This package includes both the list mapping X color names \
to RGB values (rgb.txt) and, if configured to use a database for color \
lookup, the rgb program to convert the text file into the binary database \
format."

DEPENDS += " xorgproto util-macros"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc4f23db04773a414e3c39cc97a9f415"
PE = "1"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "c80ff280a02f56c30fadc2dfa210fc6979c4ab968afa315278cb97768b64ecab"

FILES:${PN} += "${datadir}/X11"

require xorg-app-common.inc

SUMMARY = "X11 color name database"
DESCRIPTION = "This package includes both the list mapping X color names \
to RGB values (rgb.txt) and, if configured to use a database for color \
lookup, the rgb program to convert the text file into the binary database \
format."

DEPENDS += " xorgproto util-macros"
LIC_FILES_CHKSUM = "file://COPYING;md5=ef598adbe241bd0b0b9113831f6e249a"
PE = "1"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "fc03d7f56e5b2a617668167f8927948cce54f93097e7ccd9f056077f479ed37b"

FILES:${PN} += "${datadir}/X11"

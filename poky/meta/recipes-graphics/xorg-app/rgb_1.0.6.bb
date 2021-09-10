require xorg-app-common.inc

SUMMARY = "X11 color name database"
DESCRIPTION = "This package includes both the list mapping X color names \
to RGB values (rgb.txt) and, if configured to use a database for color \
lookup, the rgb program to convert the text file into the binary database \
format."

DEPENDS += " xorgproto util-macros"
LIC_FILES_CHKSUM = "file://COPYING;md5=ef598adbe241bd0b0b9113831f6e249a"
PE = "1"

SRC_URI[md5sum] = "eab5bbd7642e5c784429307ec210d198"
SRC_URI[sha256sum] = "bbca7c6aa59939b9f6a0fb9fff15dfd62176420ffd4ae30c8d92a6a125fbe6b0"

FILES:${PN} += "${datadir}/X11"

SUMMARY = "Xcursor: X Cursor management library"

DESCRIPTION = "Xcursor is a simple library designed to help locate and \
load cursors. Cursors can be loaded from files or memory. A library of \
common cursors exists which map to the standard X cursor names. Cursors \
can exist in several sizes and the library automatically picks the best \
size."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=8902e6643f7bcd7793b23dcd5d8031a4"

DEPENDS += "libxrender libxfixes"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

XORG_PN = "libXcursor"

SRC_URI[md5sum] = "1e7c17afbbce83e2215917047c57d1b3"
SRC_URI[sha256sum] = "9bc6acb21ca14da51bda5bc912c8955bc6e5e433f0ab00c5e8bef842596c33df"

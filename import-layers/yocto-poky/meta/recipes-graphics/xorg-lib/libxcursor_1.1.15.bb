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

SRC_URI[md5sum] = "58fe3514e1e7135cf364101e714d1a14"
SRC_URI[sha256sum] = "294e670dd37cd23995e69aae626629d4a2dfe5708851bbc13d032401b7a3df6b"

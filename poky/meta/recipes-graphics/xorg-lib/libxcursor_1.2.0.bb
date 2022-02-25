SUMMARY = "Xcursor: X Cursor management library"

DESCRIPTION = "Xcursor is a simple library designed to help locate and \
load cursors. Cursors can be loaded from files or memory. A library of \
common cursors exists which map to the standard X cursor names. Cursors \
can exist in several sizes and the library automatically picks the best \
size."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8902e6643f7bcd7793b23dcd5d8031a4"

DEPENDS += "libxrender libxfixes"

PE = "1"

XORG_PN = "libXcursor"

SRC_URI[md5sum] = "9b9be0e289130fb820aedf67705fc549"
SRC_URI[sha256sum] = "3ad3e9f8251094af6fe8cb4afcf63e28df504d46bfa5a5529db74a505d628782"

BBCLASSEXTEND = "native nativesdk"

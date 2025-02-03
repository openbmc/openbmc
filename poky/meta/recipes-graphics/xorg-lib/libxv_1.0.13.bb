SUMMARY = "Xv: X Video extension library"

DESCRIPTION = "libXv provides an X Window System client interface to the \
X Video extension to the X protocol. The X Video extension allows for \
accelerated drawing of videos.  Hardware adaptors are exposed to \
clients, which may draw in a number of colourspaces, including YUV."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=827da9afab1f727f2a66574629e0f39c"

DEPENDS += "libxext xorgproto"

XORG_PN = "libXv"

SRC_URI[sha256sum] = "7d34910958e1c1f8d193d828fea1b7da192297280a35437af0692f003ba03755"


SUMMARY = "Xv: X Video extension library"

DESCRIPTION = "libXv provides an X Window System client interface to the \
X Video extension to the X protocol. The X Video extension allows for \
accelerated drawing of videos.  Hardware adaptors are exposed to \
clients, which may draw in a number of colourspaces, including YUV."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=827da9afab1f727f2a66574629e0f39c"

DEPENDS += "libxext videoproto"

XORG_PN = "libXv"

SRC_URI[md5sum] = "e0af49d7d758b990e6fef629722d4aca"
SRC_URI[sha256sum] = "55fe92f8686ce8612e2c1bfaf58c057715534419da700bda8d517b1d97914525"

SUMMARY = "Xv: X Video extension library"

DESCRIPTION = "libXv provides an X Window System client interface to the \
X Video extension to the X protocol. The X Video extension allows for \
accelerated drawing of videos.  Hardware adaptors are exposed to \
clients, which may draw in a number of colourspaces, including YUV."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=827da9afab1f727f2a66574629e0f39c"

DEPENDS += "libxext xorgproto"

XORG_PN = "libXv"

SRC_URI[md5sum] = "210b6ef30dda2256d54763136faa37b9"
SRC_URI[sha256sum] = "d26c13eac99ac4504c532e8e76a1c8e4bd526471eb8a0a4ff2a88db60cb0b088"


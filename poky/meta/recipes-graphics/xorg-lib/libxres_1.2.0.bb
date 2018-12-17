SUMMARY = "XRes: X Resource extension library"

DESCRIPTION = "libXRes provides an X Window System client interface to \
the Resource extension to the X protocol.  The Resource extension allows \
for X clients to see and monitor the X resource usage of various clients \
(pixmaps, et al)."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c89441a8df261bdc56587465e13c7fa"

DEPENDS += "libxext xorgproto"

PE = "1"

XORG_PN = "libXres"

SRC_URI[md5sum] = "5d6d443d1abc8e1f6fc1c57fb27729bb"
SRC_URI[sha256sum] = "ff75c1643488e64a7cfbced27486f0f944801319c84c18d3bd3da6bf28c812d4"

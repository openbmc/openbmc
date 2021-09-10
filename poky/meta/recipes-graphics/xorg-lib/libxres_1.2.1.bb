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

SRC_URI[sha256sum] = "b6e6fb1ebb61610e56017edd928fb89a5f53b3f4f990078309877468663b2b11"

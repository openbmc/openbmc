SUMMARY = "XRes: X Resource extension library"

DESCRIPTION = "libXRes provides an X Window System client interface to \
the Resource extension to the X protocol.  The Resource extension allows \
for X clients to see and monitor the X resource usage of various clients \
(pixmaps, et al)."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c89441a8df261bdc56587465e13c7fa"

DEPENDS += "libxext resourceproto"

PE = "1"

XORG_PN = "libXres"

SRC_URI[md5sum] = "45ef29206a6b58254c81bea28ec6c95f"
SRC_URI[sha256sum] = "26899054aa87f81b17becc68e8645b240f140464cf90c42616ebb263ec5fa0e5"

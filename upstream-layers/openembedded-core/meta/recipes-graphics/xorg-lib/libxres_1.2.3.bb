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

SRC_URI[sha256sum] = "d2de8f5401d6c86a8992791654547eb8def585dfdc0c08cc16e24ef6aeeb69dc"

SUMMARY = "XExt: X Extension library"

DESCRIPTION = "libXext provides an X Window System client interface to \
several extensions to the X protocol.  The supported protocol extensions \
are DOUBLE-BUFFER, DPMS, Extended-Visual-Information, LBX, MIT_SHM, \
MIT_SUNDRY-NONSTANDARD, Multi-Buffering, SECURITY, SHAPE, SYNC, TOG-CUP, \
XC-APPGROUP, XC-MISC, XTEST.  libXext also provides a small set of \
utility functions to aid authors of client APIs for X protocol \
extensions."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4234bb3b2f1521ea101e4e9db7c33c69"

DEPENDS += "xorgproto virtual/libx11"
PROVIDES = "xext"

PE = "1"

XORG_PN = "libXext"
BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "6c643c7035cdacf67afd68f25d01b90ef889d546c9fcd7c0adf7c2cf91e3a32d"

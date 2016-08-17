SUMMARY = "XExt: X Extension library"

DESCRIPTION = "libXext provides an X Window System client interface to \
several extensions to the X protocol.  The supported protocol extensions \
are DOUBLE-BUFFER, DPMS, Extended-Visual-Information, LBX, MIT_SHM, \
MIT_SUNDRY-NONSTANDARD, Multi-Buffering, SECURITY, SHAPE, SYNC, TOG-CUP, \
XC-APPGROUP, XC-MISC, XTEST.  libXext also provides a small set of \
utility functions to aid authors of client APIs for X protocol \
extensions."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=879ce266785414bd1cbc3bc2f4d9d7c8"

DEPENDS += "xproto virtual/libx11 xextproto libxau libxdmcp"
PROVIDES = "xext"

PE = "1"

XORG_PN = "libXext"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "52df7c4c1f0badd9f82ab124fb32eb97"
SRC_URI[sha256sum] = "b518d4d332231f313371fdefac59e3776f4f0823bcb23cf7c7305bfb57b16e35"

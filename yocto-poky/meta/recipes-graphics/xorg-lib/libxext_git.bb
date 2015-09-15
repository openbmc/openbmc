require xorg-lib-common.inc

SUMMARY = "XExt: X Extension library"

DESCRIPTION = "libXext provides an X Window System client interface to \
several extensions to the X protocol.  The supported protocol extensions \
are DOUBLE-BUFFER, DPMS, Extended-Visual-Information, LBX, MIT_SHM, \
MIT_SUNDRY-NONSTANDARD, Multi-Buffering, SECURITY, SHAPE, SYNC, TOG-CUP, \
XC-APPGROUP, XC-MISC, XTEST.  libXext also provides a small set of \
utility functions to aid authors of client APIs for X protocol \
extensions."

DEPENDS += "xproto virtual/libx11 xextproto libxau libxdmcp"
PROVIDES = "xext"
SRCREV = "d1f3bc77a48c8e42771579e3fdf3370b35d3209d"
PE = "1"
PV = "1.0.99.1+gitr${SRCPV}"

XORG_PN = "libXext"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/lib/${XORG_PN}"
S = "${WORKDIR}/git/"

BBCLASSEXTEND = "nativesdk"

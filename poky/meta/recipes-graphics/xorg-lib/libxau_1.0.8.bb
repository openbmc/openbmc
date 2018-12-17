SUMMARY = "Xau: X Authority Database library"

DESCRIPTION = "libxau provides the main interfaces to the X11 \
authorisation handling, which controls authorisation for X connections, \
both client-side and server-side."

require xorg-lib-common.inc

inherit gettext

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=7908e342491198401321cec1956807ec"

DEPENDS += " xorgproto"
PROVIDES = "xau"

PE = "1"

XORG_PN = "libXau"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "685f8abbffa6d145c0f930f00703b21b"
SRC_URI[sha256sum] = "fdd477320aeb5cdd67272838722d6b7d544887dfe7de46e1e7cc0c27c2bea4f2"

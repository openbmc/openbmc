SUMMARY = "Xau: X Authority Database library"

DESCRIPTION = "libxau provides the main interfaces to the X11 \
authorisation handling, which controls authorisation for X connections, \
both client-side and server-side."

require xorg-lib-common.inc

inherit gettext

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7908e342491198401321cec1956807ec"

DEPENDS += " xorgproto"
PROVIDES = "xau"

PE = "1"

XORG_PN = "libXau"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "74d0e4dfa3d39ad8939e99bda37f5967aba528211076828464d2777d477fc0fb"

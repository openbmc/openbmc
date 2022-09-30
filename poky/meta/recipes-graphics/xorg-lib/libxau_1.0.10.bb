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
XORG_EXT = "tar.xz"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "8be6f292334d2f87e5b919c001e149a9fdc27005d6b3e053862ac6ebbf1a0c0a"

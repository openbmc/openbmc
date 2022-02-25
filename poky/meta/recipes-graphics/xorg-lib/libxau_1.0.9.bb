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

SRC_URI[md5sum] = "c5f16288f2da9f071b29111d68797480"
SRC_URI[sha256sum] = "ccf8cbf0dbf676faa2ea0a6d64bcc3b6746064722b606c8c52917ed00dcb73ec"

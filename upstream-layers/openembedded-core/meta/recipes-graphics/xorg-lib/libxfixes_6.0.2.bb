SUMMARY = "XFixes: X Fixes extension library"

DESCRIPTION = "X applications have often needed to work around various \
shortcomings in the core X window system.  This extension is designed to \
provide the minimal server-side support necessary to eliminate problems \
caused by these workarounds."

require xorg-lib-common.inc
SRC_URI[sha256sum] = "39f115d72d9c5f8111e4684164d3d68cc1fd21f9b27ff2401b08fddfc0f409ba"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5a9755c8921cc7dc08a5cfe4267d0ff"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

XORG_PN = "libXfixes"
XORG_EXT = "tar.xz"

BBCLASSEXTEND = "native nativesdk"

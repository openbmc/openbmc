SUMMARY = "XFixes: X Fixes extension library"

DESCRIPTION = "X applications have often needed to work around various \
shortcomings in the core X window system.  This extension is designed to \
provide the minimal server-side support necessary to eliminate problems \
caused by these workarounds."

require xorg-lib-common.inc
SRC_URI[sha256sum] = "b695f93cd2499421ab02d22744458e650ccc88c1d4c8130d60200213abc02d58"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5a9755c8921cc7dc08a5cfe4267d0ff"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

XORG_PN = "libXfixes"
XORG_EXT = "tar.xz"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "XFixes: X Fixes extension library"

DESCRIPTION = "X applications have often needed to work around various \
shortcomings in the core X window system.  This extension is designed to \
provide the minimal server-side support necessary to eliminate problems \
caused by these workarounds."

require xorg-lib-common.inc
SRC_URI[sha256sum] = "a7c1a24da53e0b46cac5aea79094b4b2257321c621b258729bc3139149245b4c"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3c1ce42c334a6f5cccb0277556a053e0"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

XORG_PN = "libXfixes"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "XFixes: X Fixes extension library"

DESCRIPTION = "X applications have often needed to work around various \
shortcomings in the core X window system.  This extension is designed to \
provide the minimal server-side support necessary to eliminate problems \
caused by these workarounds."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=3c1ce42c334a6f5cccb0277556a053e0"

DEPENDS += "virtual/libx11 xproto fixesproto xextproto"

PE = "1"

XORG_PN = "libXfixes"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "544d73df94e638ba7b64147be416e576"
SRC_URI[sha256sum] = "9bd20edfec084a1bed481d48dd4815dee88139fffad091418cdda081129a9aea"

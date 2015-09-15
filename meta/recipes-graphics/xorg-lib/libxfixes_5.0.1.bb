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

SRC_URI[md5sum] = "b985b85f8b9386c85ddcfe1073906b4d"
SRC_URI[sha256sum] = "63bec085084fa3caaee5180490dd871f1eb2020ba9e9b39a30f93693ffc34767"

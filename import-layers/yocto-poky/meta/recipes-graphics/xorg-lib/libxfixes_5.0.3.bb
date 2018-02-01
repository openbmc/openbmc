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

SRC_URI[md5sum] = "07e01e046a0215574f36a3aacb148be0"
SRC_URI[sha256sum] = "de1cd33aff226e08cefd0e6759341c2c8e8c9faf8ce9ac6ec38d43e287b22ad6"


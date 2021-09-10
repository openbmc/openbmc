SUMMARY = "Xlib: C Language X Interface library"

DESCRIPTION = "This package provides a client interface to the X Window \
System, otherwise known as 'Xlib'.  It provides a complete API for the \
basic functions of the window system."

require xorg-lib-common.inc

FILESEXTRAPATHS =. "${FILE_DIRNAME}/libx11:"

PE = "1"

SRC_URI += "file://Fix-hanging-issue-in-_XReply.patch \
           file://disable_tests.patch \
           file://keysym.patch \
           "

SRC_URI[sha256sum] = "1cfa35e37aaabbe4792e9bb690468efefbfbf6b147d9c69d6f90d13c3092ea6c"

PROVIDES = "virtual/libx11"

XORG_PN = "libX11"
LICENSE = "MIT & MIT-style & BSD-4-Clause & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=172255dee66bb0151435b2d5d709fcf7"

DEPENDS += "xorgproto xtrans libxcb"
DEPENDS += "xorgproto-native"

EXTRA_OECONF += "--with-keysymdefdir=${STAGING_INCDIR}/X11/ --disable-xf86bigfont"
EXTRA_OEMAKE += 'CWARNFLAGS=""'

PACKAGECONFIG ??= "xcms"
PACKAGECONFIG[xcms] = "--enable-xcms,--disable-xcms"

PACKAGES =+ "${PN}-xcb"

inherit gettext

FILES:${PN} += "${datadir}/X11/XKeysymDB ${datadir}/X11/XErrorDB ${datadir}/X11/Xcms.txt"
FILES:${PN}-xcb += "${libdir}/libX11-xcb.so.*"
FILES:${PN}-locale += "${datadir}/X11/locale ${libdir}/X11/locale"

BBCLASSEXTEND = "native nativesdk"

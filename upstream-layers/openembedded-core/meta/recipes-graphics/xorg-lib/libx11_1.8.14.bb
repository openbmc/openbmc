SUMMARY = "Xlib: C Language X Interface library"

DESCRIPTION = "This package provides a client interface to the X Window \
System, otherwise known as 'Xlib'.  It provides a complete API for the \
basic functions of the window system."

require xorg-lib-common.inc

LICENSE = "BSD-1-Clause AND HPND AND HPND-sell-variant AND ISC AND MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f2a1d92c4a50eedcf7c12672b908851"

DEPENDS += "xorgproto \
            xtrans \
            libxcb \
            xorgproto-native \
            "

PROVIDES = "virtual/libx11"

PE = "1"

XORG_PN = "libX11"

SRC_URI += "file://disable_tests.patch"

SRC_URI[sha256sum] = "bbc78c46217174655c2b32b138ad119f3ac15f50808b4d0b3a2677a4b1afc950"

inherit gettext

EXTRA_OECONF += "--with-keysymdefdir=${STAGING_INCDIR}/X11/ --disable-xf86bigfont"
EXTRA_OEMAKE += 'CWARNFLAGS=""'

PACKAGECONFIG ??= "xcms"
PACKAGECONFIG[xcms] = "--enable-xcms,--disable-xcms"

PACKAGES =+ "${PN}-xcb"

FILES:${PN} += "${datadir}/X11/XKeysymDB ${datadir}/X11/XErrorDB ${datadir}/X11/Xcms.txt"
FILES:${PN}-xcb += "${libdir}/libX11-xcb.so.*"
FILES:${PN}-locale += "${datadir}/X11/locale ${libdir}/X11/locale"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "Xlib: C Language X Interface library"

DESCRIPTION = "This package provides a client interface to the X Window \
System, otherwise known as 'Xlib'.  It provides a complete API for the \
basic functions of the window system."

require xorg-lib-common.inc

FILESEXTRAPATHS =. "${FILE_DIRNAME}/libx11:"

PE = "1"

SRC_URI += "file://Fix-hanging-issue-in-_XReply.patch \
            file://disable_tests.patch \
            file://libx11-whitespace.patch"

SRC_URI[md5sum] = "55adbfb6d4370ecac5e70598c4e7eed2"
SRC_URI[sha256sum] = "9cc7e8d000d6193fa5af580d50d689380b8287052270f5bb26a5fb6b58b2bed1"

PROVIDES = "virtual/libx11"

XORG_PN = "libX11"
LICENSE = "MIT & MIT-style & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=172255dee66bb0151435b2d5d709fcf7"

DEPENDS += "xorgproto xtrans libxcb"
DEPENDS += "xorgproto-native"

EXTRA_OECONF += "--with-keysymdefdir=${STAGING_INCDIR}/X11/ --disable-xf86bigfont"
EXTRA_OEMAKE += 'CWARNFLAGS=""'

PACKAGECONFIG ??= "xcms"
PACKAGECONFIG[xcms] = "--enable-xcms,--disable-xcms"

# src/util/makekeys is built natively but needs -D_GNU_SOURCE defined.
CPPFLAGS_FOR_BUILD += "-D_GNU_SOURCE"

PACKAGES =+ "${PN}-xcb"

inherit gettext

FILES_${PN} += "${datadir}/X11/XKeysymDB ${datadir}/X11/XErrorDB ${datadir}/X11/Xcms.txt"
FILES_${PN}-xcb += "${libdir}/libX11-xcb.so.*"
FILES_${PN}-locale += "${datadir}/X11/locale ${libdir}/X11/locale"

BBCLASSEXTEND = "native nativesdk"

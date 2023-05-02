SUMMARY = "Xlib: C Language X Interface library"

DESCRIPTION = "This package provides a client interface to the X Window \
System, otherwise known as 'Xlib'.  It provides a complete API for the \
basic functions of the window system."

require xorg-lib-common.inc

FILESEXTRAPATHS =. "${FILE_DIRNAME}/libx11:"

PE = "1"

# temporarily override SRC_URI which is hard-coded in xorg-lib-common.inc
# since new versions of packages use a new compression format - .tar.gz
SRC_URI = "${XORG_MIRROR}/individual/lib/${XORG_PN}-${PV}.tar.xz"

SRC_URI += "file://disable_tests.patch \
            file://CVE-2022-3554.patch \
            file://CVE-2022-3555.patch \
           "
SRC_URI[sha256sum] = "2ffd417266fb875028fdc0ef349694f63dbcd76d0b0cfacfb52e6151f4b60989"

PROVIDES = "virtual/libx11"

XORG_PN = "libX11"
LICENSE = "MIT & MIT & BSD-1-Clause & HPND & HPND-sell-variant"
LIC_FILES_CHKSUM = "file://COPYING;md5=172255dee66bb0151435b2d5d709fcf7"

DEPENDS += "xorgproto \
            xtrans \
            libxcb \
            xorgproto-native \
            autoconf-archive \
           "

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

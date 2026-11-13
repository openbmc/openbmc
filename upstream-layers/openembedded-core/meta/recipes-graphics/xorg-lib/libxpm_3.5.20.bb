require xorg-lib-common.inc

# libxpm requires xgettext to build
inherit gettext

SUMMARY = "Xpm: X Pixmap extension library"

DESCRIPTION = "libXpm provides support and common operation for the XPM \
pixmap format, which is commonly used in legacy X applications.  XPM is \
an extension of the monochrome XBM bitmap specificied in the X \
protocol."

LICENSE = "MIT AND X11"
LIC_FILES_CHKSUM = "file://COPYING;md5=903942ebc9d807dfb68540f40bae5aff"
DEPENDS += "libxext libsm libxt gettext-native"
PE = "1"

XORG_PN = "libXpm"
EXTRA_OECONF += "--disable-open-zfile"

PACKAGES =+ "sxpm cxpm"
FILES:cxpm = "${bindir}/cxpm"
FILES:sxpm = "${bindir}/sxpm"

SRC_URI[sha256sum] = "c064ead03e4dde78afdeb121fe0b644d25c550444cf903fbdc537f1a78f3ef74"

BBCLASSEXTEND = "native"

require xorg-lib-common.inc

# libxpm requires xgettext to build
inherit gettext

SUMMARY = "Xpm: X Pixmap extension library"

DESCRIPTION = "libXpm provides support and common operation for the XPM \
pixmap format, which is commonly used in legacy X applications.  XPM is \
an extension of the monochrome XBM bitmap specificied in the X \
protocol."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=903942ebc9d807dfb68540f40bae5aff"
DEPENDS += "libxext libsm libxt gettext-native"
PE = "1"

XORG_PN = "libXpm"
EXTRA_OECONF += "--disable-open-zfile"

PACKAGES =+ "sxpm cxpm"
FILES:cxpm = "${bindir}/cxpm"
FILES:sxpm = "${bindir}/sxpm"

SRC_URI[sha256sum] = "60bb906c5c317a6db863e39b69c4a83fdbd2ae2154fcf47640f8fefc9fdfd1c1"

BBCLASSEXTEND = "native"

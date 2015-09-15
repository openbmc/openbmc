require xorg-lib-common.inc

# libxpm requires xgettext to build
inherit gettext

SUMMARY = "Xpm: X Pixmap extension library"

DESCRIPTION = "libXpm provides support and common operation for the XPM \
pixmap format, which is commonly used in legacy X applications.  XPM is \
an extension of the monochrome XBM bitmap specificied in the X \
protocol."

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=51f4270b012ecd4ab1a164f5f4ed6cf7"
DEPENDS += "libxext libsm libxt"
PE = "1"

XORG_PN = "libXpm"

PACKAGES =+ "sxpm cxpm"
FILES_cxpm = "${bindir}/cxpm"
FILES_sxpm = "${bindir}/sxpm"

SRC_URI[md5sum] = "769ee12a43611cdebd38094eaf83f3f0"
SRC_URI[sha256sum] = "c5bdafa51d1ae30086fac01ab83be8d47fe117b238d3437f8e965434090e041c"

BBCLASSEXTEND = "native"

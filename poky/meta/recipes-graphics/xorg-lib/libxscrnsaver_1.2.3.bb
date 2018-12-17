require xorg-lib-common.inc

SUMMARY = "Xscrnsaver: X Screen Saver extension library"

DESCRIPTION = "The X Window System provides support for changing the \
image on a display screen after a user-settable period of inactivity to \
avoid burning the cathode ray tube phosphors. However, no interfaces are \
provided for the user to control the image that is drawn. This extension \
allows an external \"screen saver\" client to detect when the alternate \
image is to be displayed and to provide the graphics."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=21fd154ee757813632ada871a34113fb"

DEPENDS += "libxext xorgproto"
PROVIDES = "libxss"
RREPLACES_${PN} = "libxss"
PE = "1"

XORG_PN = "libXScrnSaver"

SRC_URI[md5sum] = "eeea9d5af3e6c143d0ea1721d27a5e49"
SRC_URI[sha256sum] = "f917075a1b7b5a38d67a8b0238eaab14acd2557679835b154cf2bca576e89bf8"

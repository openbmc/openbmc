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
RREPLACES:${PN} = "libxss"
PE = "1"

XORG_PN = "libXScrnSaver"

SRC_URI[sha256sum] = "5057365f847253e0e275871441e10ff7846c8322a5d88e1e187d326de1cd8d00"

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

DEPENDS += "libxext scrnsaverproto"
PROVIDES = "libxss"
RREPLACES_${PN} = "libxss"
PE = "1"

XORG_PN = "libXScrnSaver"

SRC_URI[md5sum] = "7a773b16165e39e938650bcc9027c1d5"
SRC_URI[sha256sum] = "8ff1efa7341c7f34bcf9b17c89648d6325ddaae22e3904e091794e0b4426ce1d"

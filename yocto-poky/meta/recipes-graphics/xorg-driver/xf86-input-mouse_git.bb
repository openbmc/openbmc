require xorg-driver-input.inc

SUMMARY = "X.Org X server -- mouse input driver"

DESCRIPTION = "mouse is an Xorg input driver for mice. The driver \
supports most available mouse types and interfaces.  The mouse driver \
functions as a pointer input device, and may be used as the X server's \
core pointer. Multiple mice are supported by multiple instances of this \
driver."

SRCREV = "ea5cfe804e112f320f14ad896c7802d53551d3e6"
PV = "1.3.0+git${SRCPV}"
PR = "${INC_PR}.0"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/driver/xf86-input-mouse \
           file://unbreak.patch"
S = "${WORKDIR}/git"


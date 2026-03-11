require xorg-app-common.inc

SUMMARY = "Utility to display window and font properties of an X server"

DESCRIPTION = "The xprop utility is for displaying window and font \
properties in an X server. One window or font is selected using the \
command line arguments or possibly in the case of a window, by clicking \
on the desired window. A list of properties is then given, possibly with \
formatting information."

LIC_FILES_CHKSUM = "file://COPYING;md5=e226ab8db88ac0bc0391673be40c9f91"

DEPENDS += "libxmu"

PE = "1"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "d689e2adb7ef7b439f6469b51cda8a7daefc83243854c2a3b8f84d0f029d67ee"

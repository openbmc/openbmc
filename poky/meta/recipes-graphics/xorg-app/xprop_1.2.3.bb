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

SRC_URI[md5sum] = "4becb3ddc4674d741487189e4ce3d0b6"
SRC_URI[sha256sum] = "d22afb28c86d85fff10a50156a7d0fa930c80ae865d70b26d805fd28a17a521b"

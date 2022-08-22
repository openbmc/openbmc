require xorg-app-common.inc

SUMMARY = "X Event Viewer"
DESCRIPTION = "Xev creates a window and then asks the X server to send it events \
whenever anything happens to the window (such as it being moved, resized, \
typed in, clicked in, etc.). You can also attach it to an existing window."

LIC_FILES_CHKSUM = "file://xev.c;beginline=1;endline=33;md5=577c99421f1803b891d2c79097ae4682"
LICENSE = "MIT"

PE = "1"

DEPENDS += "libxrandr xorgproto"

SRC_URI += "file://diet-x11.patch"

SRC_URI[sha256sum] = "c9461a4389714e0f33974f9e75934bdc38d836a0f059b8dc089c7cbf2ce36ec1"

SRC_URI_EXT = "xz"

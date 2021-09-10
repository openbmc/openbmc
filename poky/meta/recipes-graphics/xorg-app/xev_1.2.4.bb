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

SRC_URI[sha256sum] = "d700e08bfe751ed2dbf802baa204b056d0e49348b6eb3c6f9cb035d8ae4885e2"

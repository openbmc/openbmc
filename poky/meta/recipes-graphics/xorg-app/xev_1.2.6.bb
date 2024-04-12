require xorg-app-common.inc

SUMMARY = "X Event Viewer"
DESCRIPTION = "Xev creates a window and then asks the X server to send it events \
whenever anything happens to the window (such as it being moved, resized, \
typed in, clicked in, etc.). You can also attach it to an existing window."

LIC_FILES_CHKSUM = "file://xev.c;beginline=1;endline=33;md5=577c99421f1803b891d2c79097ae4682"
LICENSE = "MIT"

PE = "1"

DEPENDS += "libxrandr xorgproto"

SRC_URI[sha256sum] = "61e1c5e008ac9973aca7cdddf36e9df7410e77083b030eb04f4dc737c51807d7"

SRC_URI_EXT = "xz"

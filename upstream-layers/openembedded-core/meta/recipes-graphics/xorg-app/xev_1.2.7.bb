require xorg-app-common.inc

SUMMARY = "X Event Viewer"
DESCRIPTION = "Xev creates a window and then asks the X server to send it events \
whenever anything happens to the window (such as it being moved, resized, \
typed in, clicked in, etc.). You can also attach it to an existing window."

LIC_FILES_CHKSUM = "file://xev.c;beginline=1;endline=33;md5=577c99421f1803b891d2c79097ae4682"
LICENSE = "MIT"

PE = "1"

DEPENDS += "libxrandr xorgproto"

SRC_URI[sha256sum] = "95167895924de58e34b1013b2b0c8476e90d0888c6c39e7ae9bc35e3a19dba04"

SRC_URI_EXT = "xz"

XORGBUILDCLASS = "meson"

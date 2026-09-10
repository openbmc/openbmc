require xorg-app-common.inc

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension command"

DESCRIPTION = "Xrandr is used to set the size, orientation and/or \
reflection of the outputs for a screen. It can also set the screen \
size."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=97a36d5c66965b1cea4666b3708ca6e8"
DEPENDS += "libxrandr libxrender"
PE = "1"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "2cafccb2aaf2491a4068676117a0d4f90ab307724b96fffc54cd1da953779400"

XORGBUILDCLASS = "meson"

BBCLASSEXTEND = "native nativesdk"

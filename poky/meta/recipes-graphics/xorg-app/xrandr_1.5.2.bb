require xorg-app-common.inc

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension command"

DESCRIPTION = "Xrandr is used to set the size, orientation and/or \
reflection of the outputs for a screen. It can also set the screen \
size."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fe1608bdb33cf8c62a4438f7d34679b3"
DEPENDS += "libxrandr libxrender"
PE = "1"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "c8bee4790d9058bacc4b6246456c58021db58a87ddda1a9d0139bf5f18f1f240"

BBCLASSEXTEND = "native nativesdk"

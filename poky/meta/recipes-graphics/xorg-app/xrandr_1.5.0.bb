require xorg-app-common.inc

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension command"

DESCRIPTION = "Xrandr is used to set the size, orientation and/or \
reflection of the outputs for a screen. It can also set the screen \
size."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fe1608bdb33cf8c62a4438f7d34679b3"
DEPENDS += "libxrandr libxrender"
PE = "1"

SRC_URI[md5sum] = "ebffac98021b8f1dc71da0c1918e9b57"
SRC_URI[sha256sum] = "c1cfd4e1d4d708c031d60801e527abc9b6d34b85f2ffa2cadd21f75ff38151cd"

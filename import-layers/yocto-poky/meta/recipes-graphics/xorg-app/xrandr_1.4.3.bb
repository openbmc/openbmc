require xorg-app-common.inc

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension command"

DESCRIPTION = "Xrandr is used to set the size, orientation and/or \
reflection of the outputs for a screen. It can also set the screen \
size."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fe1608bdb33cf8c62a4438f7d34679b3"
DEPENDS += "libxrandr libxrender"
PE = "1"

SRC_URI[md5sum] = "441fdb98d2abc6051108b7075d948fc7"
SRC_URI[sha256sum] = "7154ac3486b86923692f2d6cdb2991a2ee72bc32af2c4379a6f1c068f204be1b"

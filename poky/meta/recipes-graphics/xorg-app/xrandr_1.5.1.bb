require xorg-app-common.inc

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension command"

DESCRIPTION = "Xrandr is used to set the size, orientation and/or \
reflection of the outputs for a screen. It can also set the screen \
size."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fe1608bdb33cf8c62a4438f7d34679b3"
DEPENDS += "libxrandr libxrender"
PE = "1"

SRC_URI = "${XORG_MIRROR}/individual/app/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "fe40f7a4fd39dd3a02248d3e0b1972e4"
SRC_URI[sha256sum] = "7bc76daf9d72f8aff885efad04ce06b90488a1a169d118dea8a2b661832e8762"

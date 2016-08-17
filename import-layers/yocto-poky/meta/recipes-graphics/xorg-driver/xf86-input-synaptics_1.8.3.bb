require xorg-driver-input.inc

SUMMARY = "X.Org X server -- synaptics touchpad input driver"

DESCRIPTION = "synaptics is an Xorg input driver for the touchpads from \
Synaptics Incorporated. Even though these touchpads (by default, \
operating in a compatibility mode emulating a standard mouse) can be \
handled by the normal evdev or mouse drivers, this driver allows more \
advanced features of the touchpad to become available."

LIC_FILES_CHKSUM = "file://COPYING;md5=55aacd3535a741824955c5eb8f061398"

SRC_URI[md5sum] = "4e3c8bed1ab4a67db2160c2c3d7e2a34"
SRC_URI[sha256sum] = "d39f100c74f3673778b53f17bab7690161925e25dd998a15dd8cc69b52e83f01"

DEPENDS += "libxi mtdev libxtst libevdev"

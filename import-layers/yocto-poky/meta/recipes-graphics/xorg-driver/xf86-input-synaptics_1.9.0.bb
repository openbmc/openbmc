require xorg-driver-input.inc

SUMMARY = "X.Org X server -- synaptics touchpad input driver"

DESCRIPTION = "synaptics is an Xorg input driver for the touchpads from \
Synaptics Incorporated. Even though these touchpads (by default, \
operating in a compatibility mode emulating a standard mouse) can be \
handled by the normal evdev or mouse drivers, this driver allows more \
advanced features of the touchpad to become available."

LIC_FILES_CHKSUM = "file://COPYING;md5=55aacd3535a741824955c5eb8f061398"

SRC_URI[md5sum] = "58e5b7722a402114093bf193962d1e3a"
SRC_URI[sha256sum] = "afba3289d7a40217a19d90db98ce181772f9ca6d77e1898727b0afcf02073b5a"

DEPENDS += "libxi mtdev libxtst libevdev"

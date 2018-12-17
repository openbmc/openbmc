require xorg-driver-input.inc

SUMMARY = "X.Org X server -- synaptics touchpad input driver"

DESCRIPTION = "synaptics is an Xorg input driver for the touchpads from \
Synaptics Incorporated. Even though these touchpads (by default, \
operating in a compatibility mode emulating a standard mouse) can be \
handled by the normal evdev or mouse drivers, this driver allows more \
advanced features of the touchpad to become available."

LIC_FILES_CHKSUM = "file://COPYING;md5=55aacd3535a741824955c5eb8f061398"

SRC_URI[md5sum] = "cfb79d3c975151f9bbf30b727c260cb9"
SRC_URI[sha256sum] = "7af83526eff1c76e8b9e1553b34245c203d029028d8044dd9dcf71eef1001576"

DEPENDS += "libxi mtdev libxtst libevdev"

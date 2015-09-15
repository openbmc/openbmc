require xorg-driver-input.inc

SUMMARY = "X.Org X server -- synaptics touchpad input driver"

DESCRIPTION = "synaptics is an Xorg input driver for the touchpads from \
Synaptics Incorporated. Even though these touchpads (by default, \
operating in a compatibility mode emulating a standard mouse) can be \
handled by the normal evdev or mouse drivers, this driver allows more \
advanced features of the touchpad to become available."

LIC_FILES_CHKSUM = "file://COPYING;md5=55aacd3535a741824955c5eb8f061398"

SRC_URI[md5sum] = "8ed68e8cc674dd61adb280704764aafb"
SRC_URI[sha256sum] = "7b0e164ebd02a680e0c695955e783059f37edb0c2656398e0a972adc8e698c80"

DEPENDS += "libxi mtdev libxtst libevdev"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

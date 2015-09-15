require xorg-driver-input.inc

SUMMARY = "X.Org X server -- synaptics touchpad input driver"

DESCRIPTION = "synaptics is an Xorg input driver for the touchpads from \
Synaptics Incorporated. Even though these touchpads (by default, \
operating in a compatibility mode emulating a standard mouse) can be \
handled by the normal evdev or mouse drivers, this driver allows more \
advanced features of the touchpad to become available."

SRCREV = "934bc0012f948c52aadc8eda912f7728fb7394a2"
PV = "0.15.2+git${SRCPV}"
PR = "${INC_PR}.1"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/driver/xf86-input-synaptics"
S = "${WORKDIR}/git"

DEPENDS += "libxi mtdev libxtst"

require xorg-driver-input.inc

SUMMARY = "X.Org X server -- event devices (evdev) input driver"

DESCRIPTION = "evdev is an Xorg input driver for Linux's generic event \
devices. It therefore supports all input devices that the kernel knows \
about, including most mice and keyboards. \
\
The evdev driver can serve as both a pointer and a keyboard input \
device, and may be used as both the core keyboard and the core pointer. \
Multiple input devices are supported by multiple instances of this \
driver, with one Load directive for evdev in the Module section of your \
xorg.conf for each input device that will use this driver. "

LIC_FILES_CHKSUM = "file://COPYING;md5=fefe33b1cf0cacba0e72e3b0fa0f0e16"

DEPENDS += "mtdev libevdev"

SRC_URI[md5sum] = "94c2c8354c74ab1cfbbb0a222078b9c6"
SRC_URI[sha256sum] = "9edaa6205baf6d2922cc4db3d8e54a7e7773b5f733b0ae90f6be7725f983b70d"


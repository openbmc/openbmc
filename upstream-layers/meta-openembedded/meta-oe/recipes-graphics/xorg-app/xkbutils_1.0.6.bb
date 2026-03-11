require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "XKeyboard (XKB) extension to the X11 protocol"
DESCRIPTION = " \
xkbutils is a collection of small utilities utilizing the XKeyboard (XKB) \
extension to the X11 protocol. \
It includes: \
    xkbbell  - generate XKB bell events \
    xkbvleds - display the state of LEDs on an XKB keyboard in a window \
    xkbwatch - reports changes in the XKB keyboard state \
"
LIC_FILES_CHKSUM = "file://COPYING;md5=6767a97a97e21260134637b657b922ae"

DEPENDS += "libxaw libxkbfile"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "31a2bbee1e09ccba01de92897b8f540b545de812f318d31de07bd3a5a75ee25e"

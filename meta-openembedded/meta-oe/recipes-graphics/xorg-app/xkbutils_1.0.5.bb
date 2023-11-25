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
LIC_FILES_CHKSUM = "file://COPYING;md5=64322fab5239f5c8d97cf6e0e14f1c62"

DEPENDS += "libxaw libxkbfile"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "f6a4a8e9c54582beb3787b1faa8168caab125c1fee0ca9cfa5b6c9c1df25eea4"

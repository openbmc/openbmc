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
LIC_FILES_CHKSUM = "file://COPYING;md5=4f2fa564ec5be0f310ad939a8444a0bb"

DEPENDS += "libxaw libxkbfile"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "5f37be0ee31ada114415780c562638a1b4f03b385e56449927bf0d5665787a26"

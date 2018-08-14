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

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "502b14843f610af977dffc6cbf2102d5"
SRC_URI[sha256sum] = "d2a18ab90275e8bca028773c44264d2266dab70853db4321bdbc18da75148130"

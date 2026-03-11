require recipes-graphics/xorg-lib/xcb-util.inc

SUMMARY = "XCB port of libXcursor"

DEPENDS += "xcb-util xcb-util-renderutil xcb-util-image"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ce469b61c70ff8d7cce0547476891974"

SRC_URI[sha256sum] = "0caf99b0d60970f81ce41c7ba694e5eaaf833227bb2cbcdb2f6dc9666a663c57"

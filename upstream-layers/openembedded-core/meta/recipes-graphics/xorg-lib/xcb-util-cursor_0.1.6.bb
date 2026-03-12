require recipes-graphics/xorg-lib/xcb-util.inc

SUMMARY = "XCB port of libXcursor"

DEPENDS += "xcb-util xcb-util-renderutil xcb-util-image"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ce469b61c70ff8d7cce0547476891974"

SRC_URI[sha256sum] = "fdeb8bd127873519be5cc70dcd0d3b5d33b667877200f9925a59fdcad8f7a933"

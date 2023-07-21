require recipes-graphics/xorg-lib/xcb-util.inc

SUMMARY = "XCB port of libXcursor"

DEPENDS += "xcb-util xcb-util-renderutil xcb-util-image"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ce469b61c70ff8d7cce0547476891974"

SRC_URI[sha256sum] = "28dcfe90bcab7b3561abe0dd58eb6832aa9cc77cfe42fcdfa4ebe20d605231fb"

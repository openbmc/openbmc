require recipes-graphics/xorg-lib/xcb-util.inc

SUMMARY = "XCB port of libXcursor"

DEPENDS += "xcb-util xcb-util-renderutil xcb-util-image"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ce469b61c70ff8d7cce0547476891974"

SRC_URI[md5sum] = "6ac3b17cba51aaaa36ba035a53527214"
SRC_URI[sha256sum] = "05a10a0706a1a789a078be297b5fb663f66a71fb7f7f1b99658264c35926394f"

require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "Display a message or query in a window"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=73c7f696a728de728d7446cbca814cc5"

DEPENDS += "libxaw"

SRC_URI[sha256sum] = "703fccb7a0b772d61d7e603c189b9739866aa97ba985c727275420f829a30356"
SRC_URI_EXT = "xz"

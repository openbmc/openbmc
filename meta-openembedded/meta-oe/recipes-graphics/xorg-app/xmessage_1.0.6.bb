require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "Display a message or query in a window"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=73c7f696a728de728d7446cbca814cc5"

DEPENDS += "libxaw"

SRC_URI[sha256sum] = "d2eac545f137156b960877e052fcc8e29795ed735c02f7690fd7b439e6846a12"
SRC_URI_EXT = "xz"

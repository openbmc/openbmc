require recipes-graphics/xorg-lib/xcb-util.inc

SUMMARY = "xcb-util-errors gives human readable names to error codes and event codes"

DEPENDS += "xcb-util xorgproto"

export PYTHON="python3"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c90ee77011043c608617f5323a523726"

SRC_URI[sha256sum] = "5628c87b984259ad927bacd8a42958319c36bdf4b065887803c9d820fb80f357"

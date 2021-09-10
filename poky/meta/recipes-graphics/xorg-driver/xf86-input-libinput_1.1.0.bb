require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a22925127bd3c827c384cedd23ed2309"

DEPENDS += "libinput"

SRC_URI[sha256sum] = "e11d2a41419124a6e9b148f1df181bf7819fc7398c8ee9a1b6390b0742c68d16"

FILES:${PN} += "${datadir}/X11/xorg.conf.d"

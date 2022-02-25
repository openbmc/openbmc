require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a22925127bd3c827c384cedd23ed2309"

DEPENDS += "libinput"

XORG_DRIVER_COMPRESSOR = ".tar.xz"
SRC_URI[sha256sum] = "8151db5b9ddb317c0ce92dcb62da9a8db5079e5b8a95b60abc854da21e7e971b"

FILES:${PN} += "${datadir}/X11/xorg.conf.d"

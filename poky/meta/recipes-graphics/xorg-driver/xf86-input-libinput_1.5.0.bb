require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a22925127bd3c827c384cedd23ed2309"

DEPENDS += "libinput"

XORG_DRIVER_COMPRESSOR = ".tar.xz"
SRC_URI[sha256sum] = "2524c35f196554ea11aef3bba1cf324759454e1d49f98ac026ace2f6003580e6"

FILES:${PN} += "${datadir}/X11/xorg.conf.d"

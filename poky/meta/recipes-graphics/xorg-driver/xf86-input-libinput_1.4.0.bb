require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a22925127bd3c827c384cedd23ed2309"

DEPENDS += "libinput"

XORG_DRIVER_COMPRESSOR = ".tar.xz"
SRC_URI[sha256sum] = "3a3d14cd895dc75b59ae2783b888031956a0bac7a1eff16d240dbb9d5df3e398"

FILES:${PN} += "${datadir}/X11/xorg.conf.d"

require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "da47ef62eab1d0e922a8fa929ff81758"
SRC_URI[sha256sum] = "abca558fc2226f295691f1cf3412d4c0edeaa439f677ca25b5c9fab310d2387b"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

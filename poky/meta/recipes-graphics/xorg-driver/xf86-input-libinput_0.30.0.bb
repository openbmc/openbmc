require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "11dcfa2cc39f790731a9545fcdeea717"
SRC_URI[sha256sum] = "f9c7f9fd41ae14061e0e9c3bd45fa170e5e21027a2bc5810034e1e748db996c0"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

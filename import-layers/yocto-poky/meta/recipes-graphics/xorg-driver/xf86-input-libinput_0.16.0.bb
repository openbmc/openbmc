require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "2c8cb520f88da7bafaceebc0b34ea1d4"
SRC_URI[sha256sum] = "fdade531e91e79acf6dce8ac55fa4f65abe3f1358c5d3d777ae48dbc74b76f49"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

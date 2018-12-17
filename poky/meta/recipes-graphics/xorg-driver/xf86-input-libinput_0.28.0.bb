require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "2d7519ac0e39d4c88f3be32e81a637aa"
SRC_URI[sha256sum] = "21994d065fc26e85d1c3fc87d8479b9c22699ed5a0119df98fbe0000e84630a1"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

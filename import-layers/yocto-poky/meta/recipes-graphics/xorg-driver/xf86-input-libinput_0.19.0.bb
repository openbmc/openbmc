require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "52c38b1369764243bfcf6ead1e4c6d32"
SRC_URI[sha256sum] = "6c5d30dc7c8b8ae34261340e1dc9cbb8ef435078e084b8ef507527a8a21af477"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

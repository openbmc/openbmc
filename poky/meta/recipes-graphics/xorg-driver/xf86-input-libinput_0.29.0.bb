require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "d600e8e2e30747b8ce49ec5294ff0ab6"
SRC_URI[sha256sum] = "c28b56a21754b972db31798e6a4cf4dc9d69208d08f8fe41701a94def5e94bee"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

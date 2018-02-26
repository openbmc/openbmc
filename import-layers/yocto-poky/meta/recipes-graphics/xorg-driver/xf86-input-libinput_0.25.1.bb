require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "14003139614b25cc76c9a4cad059df89"
SRC_URI[sha256sum] = "489f7d591c9ef08463d4966e61f7c6ea433f5fcbb9f5370fb621da639a84c7e0"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

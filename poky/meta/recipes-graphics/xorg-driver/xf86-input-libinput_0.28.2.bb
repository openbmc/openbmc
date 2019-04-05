require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LIC_FILES_CHKSUM = "file://COPYING;md5=5e6b20ea2ef94a998145f0ea3f788ee0"

DEPENDS += "libinput"

SRC_URI[md5sum] = "b7548bc1d7e82d189205794ff86307af"
SRC_URI[sha256sum] = "b8b346962c6b62b8069928c29c0db83b6f544863bf2fc6830f324de841de2820"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a22925127bd3c827c384cedd23ed2309"

DEPENDS += "libinput"

SRC_URI[sha256sum] = "fddec49c115591918475155bf16aaf23017d7f814cee7823a0c11f867aca245b"

FILES_${PN} += "${datadir}/X11/xorg.conf.d"

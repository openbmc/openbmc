require xorg-driver-input.inc

SUMMARY = "Generic input driver for the X.Org server based on libinput"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a22925127bd3c827c384cedd23ed2309"

DEPENDS += "libinput"

SRC_URI[sha256sum] = "f80da3c514fe1cbf57fa1b1bd6ff97f6b0a1f87466ad89247bac59cd0a5869f6"

FILES:${PN} += "${datadir}/X11/xorg.conf.d"

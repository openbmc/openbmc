require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "tiny window manager"
DEPENDS += " libxext libxt libxmu bison-native"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6d42ef60e8166aa26606524c0b9586"

SRC_URI[md5sum] = "e322c08eeb635f924ede5b8eba3db54e"
SRC_URI[sha256sum] = "6449eadca16ce0f0d900605b0cc020b95f40619261b7beccfb46bcc1836445d7"

FILES_${PN} += "${datadir}/X11/twm/system.twmrc"
ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_PATH = "${bindir}/twm"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PRIORITY = "1"

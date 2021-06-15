require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "tiny window manager"
DEPENDS += " libxext libxt libxmu bison-native"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6d42ef60e8166aa26606524c0b9586"

SRC_URI = "${XORG_MIRROR}/individual/app/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "48e83210b39a7bfb492604ba0bcfb19e"
SRC_URI[sha256sum] = "410ecabac54e6db7afd5c20a78d89c0134f3c74b149bee71b1fec775e6e060cc"

FILES_${PN} += "${datadir}/X11/twm/system.twmrc"
ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_PATH = "${bindir}/twm"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PRIORITY = "1"

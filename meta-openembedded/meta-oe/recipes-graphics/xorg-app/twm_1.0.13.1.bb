require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "tiny window manager"
DEPENDS += " libxext libxt libxmu bison-native"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6d42ef60e8166aa26606524c0b9586"

SRC_URI = "${XORG_MIRROR}/individual/app/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "e16bdbc82ac57c096d606a90e63c42c73a4a8a6a8a04d97b7dad199f919c8471"

FILES:${PN} += "${datadir}/X11/twm/system.twmrc"
ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_PATH = "${bindir}/twm"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PRIORITY = "1"

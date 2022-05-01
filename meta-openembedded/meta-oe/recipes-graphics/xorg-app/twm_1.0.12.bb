require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "tiny window manager"
DEPENDS += " libxext libxt libxmu bison-native"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6d42ef60e8166aa26606524c0b9586"

SRC_URI = "${XORG_MIRROR}/individual/app/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "4150c9ec595520167ab8c4efcb5cf82641a4c4db78ce0a1cb4834e6aeb7c87fb"

FILES:${PN} += "${datadir}/X11/twm/system.twmrc"
ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_PATH = "${bindir}/twm"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PRIORITY = "1"

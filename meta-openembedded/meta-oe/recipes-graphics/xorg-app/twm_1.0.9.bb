require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "tiny window manager"
DEPENDS += " libxext libxt libxmu bison-native"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6d42ef60e8166aa26606524c0b9586"

SRC_URI[md5sum] = "59a6f076cdacb5f6945dac809bcf4906"
SRC_URI[sha256sum] = "c575167ac33edf3bd1592f1bedfe0559debb128ed5fb4873c1278e3be166310a"

FILES_${PN} += "${datadir}/X11/twm/system.twmrc"
ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_PATH = "${bindir}/twm"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PRIORITY = "1"

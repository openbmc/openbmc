require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

DEPENDS += "bc-native dtc-native gnutls-native python3-pyelftools-native"

SRCREV = "e50b1e8715011def8aff1588081a2649a2c6cd47"
SRC_URI = "git://source.denx.de/u-boot/u-boot.git;protocol=https;branch=master"

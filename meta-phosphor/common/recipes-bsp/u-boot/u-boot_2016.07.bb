require recipes-bsp/u-boot/u-boot.inc

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
DEPENDS += "dtc-native"

SRCREV = "be5e3b5b680746718f45ec5ad45e2debc61407b5"
UBRANCH = "v2016.07-aspeed-openbmc"
SRC_URI = "git://git@github.com/openbmc/u-boot.git;branch=${UBRANCH};protocol=https"

PV = "v2016.07+git${SRCPV}"

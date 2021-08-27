require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
require u-boot-common-gxp_2020.10.inc
require conf/machine/include/fitimage-sign.inc

S = "${WORKDIR}/git"

PROVIDES += "u-boot"

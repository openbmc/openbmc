FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-nuvoton:${THISDIR}/../../../../recipes-bsp/uboot/files:"

SRC_URI +="file://yosemite4-common.cfg \
           file://yosemite4.cfg"

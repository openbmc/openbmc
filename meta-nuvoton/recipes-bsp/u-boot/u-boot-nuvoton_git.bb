DESCRIPTION = "U-boot for Nuvoton NPCM7xx Baseboard Management Controller"

require u-boot-common-nuvoton.inc
require recipes-bsp/u-boot/u-boot.inc

PROVIDES += "u-boot"

DEPENDS += "dtc-native"

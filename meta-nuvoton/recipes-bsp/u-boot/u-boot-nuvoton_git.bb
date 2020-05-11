DESCRIPTION = "U-boot for Nuvoton NPCM7xx Baseboard Management Controller"

require u-boot-common-nuvoton.inc
require u-boot-nuvoton.inc

PROVIDES += "u-boot"

DEPENDS += "dtc-native"

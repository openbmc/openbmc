FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/u-boot-nuvoton:"

UBOOT_MAKE_TARGET:append:evb-npcm750 = " DEVICE_TREE=${UBOOT_DEVICETREE}"

SRC_URI:append:evb-npcm750 = " file://fixed_phy.cfg"

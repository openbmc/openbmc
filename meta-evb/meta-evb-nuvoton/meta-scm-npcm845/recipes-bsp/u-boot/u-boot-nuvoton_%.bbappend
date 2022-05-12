FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/u-boot-nuvoton:"

SRC_URI:append:scm-npcm845 = " file://emmc.cfg"
SRC_URI:append:scm-npcm845 = " file://scm.cfg"
SRC_URI:append:scm-npcm845 = " file://0001-uboot-scm-dts.patch"

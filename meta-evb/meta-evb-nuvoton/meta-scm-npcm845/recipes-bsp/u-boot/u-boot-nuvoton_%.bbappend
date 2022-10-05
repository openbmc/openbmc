FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/u-boot-nuvoton:"

inherit emmc-utils
SRC_URI:append:scm-npcm845 = " file://emmc.cfg"
SRC_URI:append:scm-npcm845 = " file://scm.cfg"
SRC_URI:append:scm-npcm845 = " file://0001-uboot-scm-dts.patch"
SRC_URI:append:scm-npcm845 = " file://0001-scm-npcm845-board-setting.patch"
SRC_URI:append:scm-npcm845 = " file://0001-i2c-mw-zero-len.patch"
SRC_URI:append:scm-npcm845 = " \
	${@emmc_enabled(d, 'file://0001-boot-openbmc-form-emmc.patch')}"

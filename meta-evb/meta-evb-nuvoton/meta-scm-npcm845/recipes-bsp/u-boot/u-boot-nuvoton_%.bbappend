FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/u-boot-nuvoton:"

inherit emmc-utils
SRC_URI:append:scm-npcm845 = " file://emmc.cfg"
SRC_URI:append:scm-npcm845 = " file://scm.cfg"
SRC_URI:append:scm-npcm845 = " file://0001-uboot-scm-dts.patch"
SRC_URI:append:scm-npcm845 = " file://0002-scm-npcm845-board-setting.patch"
SRC_URI:append:scm-npcm845 = " file://0003-i2c-mw-zero-len.patch"
SRC_URI:append:scm-npcm845 = " file://0004-net-designware-do-phy_config-before-start-up.patch"
SRC_URI:append:scm-npcm845 = " file://0005-net-phy-realtek-rtl8211f-introduce-phy_reset.patch"
SRC_URI:append:scm-npcm845 = " file://0006-set-clock-divisor-for-uart456.patch"
SRC_URI:append:scm-npcm845 = " file://0007-set-fiu0-drd_cfg-4-byte-addr.patch"
SRC_URI:append:scm-npcm845 = " \
	${@emmc_enabled(d, 'file://1111-boot-openbmc-form-emmc.patch')}"

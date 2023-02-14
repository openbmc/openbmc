FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/u-boot-nuvoton:"

inherit emmc-utils
SRC_URI:append:scm-npcm845 = " file://scm.cfg"
SRC_URI:append:scm-npcm845 = " file://nuvoton-npcm845-scm-pincfg.dtsi;subdir=git/arch/arm/dts/"
SRC_URI:append:scm-npcm845 = " file://nuvoton-npcm845-scm.dts;subdir=git/arch/arm/dts/"

SRC_URI:append:scm-npcm845 = " file://0001-uboot-scm-dts.patch"
SRC_URI:append:scm-npcm845 = " \
	${@emmc_enabled(d, 'file://1111-boot-openbmc-form-emmc.patch')}"

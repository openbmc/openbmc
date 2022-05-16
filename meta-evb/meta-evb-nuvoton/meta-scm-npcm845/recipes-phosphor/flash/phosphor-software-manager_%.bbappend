FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " file://support_update_uboot_with_emmc_image.patch"
PACKAGECONFIG:append:scm-npcm845 = " verify_signature"

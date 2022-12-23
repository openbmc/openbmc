FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " file://0001-Restore-and-verify-BIOS.patch"
SRC_URI:append:evb-npcm845 = " file://0002-Support-ignore-update-uboot-with-eMMC-image.patch"

PACKAGECONFIG:evb-npcm845 += "verify_signature flash_bios"
EXTRA_OEMESON:append:evb-npcm845 = " -Doptional-images=image-bios"

FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm750 = " \
    file://0001-Support-update-uboot-with-emmc-image.patch \
    file://0002-porting-bios-verify-feature.patch  \
    file://0003-Add-support-report-same-version-error.patch \
    "

PACKAGECONFIG:append:evb-npcm750 = " verify_signature flash_bios"
EXTRA_OEMESON:append:evb-npcm750 = " -Doptional-images=image-bios"

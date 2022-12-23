FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://support_update_uboot_with_emmc_image.patch"
SRC_URI:append:olympus-nuvoton = " file://restore_verify_bios.patch"
SRC_URI:append:olympus-nuvoton = " file://report_same_version.patch"
#SRC_URI:append:olympus-nuvoton = " file://avoid_update_bios_fail_remove_mmc"

PACKAGECONFIG:append:olympus-nuvoton = " verify_signature flash_bios"
EXTRA_OEMESON:append:olympus-nuvoton = " -Doptional-images=image-bios"

FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

SRC_URI:append:buv-runbmc = " \
    file://0001-Support-update-uboot-with-emmc-image.patch \
    file://0002-porting-bios-verify-feature.patch  \
    file://0003-Add-support-report-same-version-error.patch \
    file://0004-Add-support-for-MCU-firmware-upgrade.patch \
    "

PACKAGECONFIG[flash_mcu] = "-Dmcu-upgrade=enabled, -Dmcu-upgrade=disabled"
PACKAGECONFIG:append:buv-runbmc = " verify_signature flash_bios flash_mcu"
EXTRA_OEMESON:append:buv-runbmc = " -Doptional-images=image-bios"

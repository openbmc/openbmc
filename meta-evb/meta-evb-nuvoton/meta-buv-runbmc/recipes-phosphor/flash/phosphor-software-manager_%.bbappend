FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

SRC_URI:append:buv-runbmc = " \
    file://0001-Support-update-uboot-with-emmc-image.patch \
    file://0002-porting-bios-verify-feature.patch  \
    file://0003-Add-support-report-same-version-error.patch \
    "

PACKAGECONFIG:append:buv-runbmc = " verify_signature flash_bios"
EXTRA_OEMESON:append:buv-runbmc = " -Doptional-images=image-bios"
